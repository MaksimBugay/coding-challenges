package bmv.map_with_eviction_and_ttl;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

/**
 * A bounded concurrent map with FIFO (insertion-order) eviction and optional
 * expire-after-write TTL. Updating a key refreshes its TTL without moving its
 * insertion position. Reads and unsuccessful conditional writes do not refresh TTL.
 * Null keys and values are not supported.
 *
 * <p>Reads check a monotonic deadline even when background cleanup is delayed.
 * Expiration cleanup passes are separated by at least 100 milliseconds, measured
 * from the end of the previous pass, and run only in the background worker.
 * Normal operations never run an expiration cleanup pass. Writes replace their
 * own expired mapping when needed and enforce capacity with at most one eviction,
 * preferring an expired entry over a live FIFO victim. size() counts live entries without
 * forcing cleanup; counting an expired prefix takes O(number of expired entries).
 * Direct reads share a read lock; all mutations and remapping functions use one
 * write lock. Remapping functions run once per invocation, must be short, and
 * must not mutate this map or wait for another thread to access it. Recursive
 * mutation from a remapping function throws IllegalStateException.
 *
 * <p>The expiration list contains exactly one node per stored entry while TTL
 * is enabled. Refresh, removal and FIFO eviction take O(1) expected time.
 * Memory is O(maxCapacity), independent
 * of the number of writes. View iteration copies O(size) immutable entries;
 * iterators are snapshots, while the views themselves are backed by this map.
 * Iterator removal conditionally removes the captured key/value pair. Entry
 * setValue is unsupported; use replace or compute instead. Value objects are
 * not copied and require their own synchronization if mutable.
 *
 * <p>A TTL-enabled instance owns a daemon cleanup thread. Call close/shutdown
 * when it is no longer needed. Shutdown discards expired entries, disables TTL
 * for surviving and future entries, and signals the worker to exit without
 * waiting for any expiration deadline. The map remains usable afterwards.
 */
public final class ConcurrentMapWithEvictionAndKeyTtlEnhanced<K, V>
        extends AbstractMap<K, V> implements ConcurrentMap<K, V>, AutoCloseable {

    private static final long MAX_TTL_MILLIS = Long.MAX_VALUE / 1_000_000;
    private static final long MIN_CLEANUP_INTERVAL_NANOS = TimeUnit.MILLISECONDS.toNanos(100);

    private static final class Node<K, V> {
        final K key;
        V value;
        long writtenAt;
        Node<K, V> previous;
        Node<K, V> next;

        Node(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }

    // All mutable state except the volatile flag is protected by lock.
    private final LinkedHashMap<K, Node<K, V>> entries = new LinkedHashMap<>();
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final Condition expirationChanged = lock.writeLock().newCondition();
    private final int maxCapacity;
    private final long ttlMillis;
    private final long ttlNanos;
    private final LongSupplier ticker;
    private final Set<Entry<K, V>> entryView = new EntryView();
    private final Set<K> keyView = new KeyView();
    private Node<K, V> oldestExpiration;
    private Node<K, V> newestExpiration;
    private volatile boolean ttlEnabled;
    private boolean inRemappingFunction;
    private boolean cleanupHasRun;
    private long lastCleanupFinishedAt;

    public ConcurrentMapWithEvictionAndKeyTtlEnhanced(int maxCapacity) {
        this(maxCapacity, 0);
    }

    /**
     * @param maxCapacity positive maximum number of entries
     * @param ttlMillis TTL in milliseconds; zero/negative disables expiration
     * @throws IllegalArgumentException for invalid capacity or a positive TTL
     *         too large to represent in nanoseconds
     */
    public ConcurrentMapWithEvictionAndKeyTtlEnhanced(int maxCapacity, long ttlMillis) {
        this(maxCapacity, ttlMillis, System::nanoTime, true);
    }

    // Test seam: a monotonic ticker and optional manual maintenance.
    ConcurrentMapWithEvictionAndKeyTtlEnhanced(int maxCapacity, long ttlMillis,
                                              LongSupplier ticker, boolean startWorker) {
        if (maxCapacity < 1) {
            throw new IllegalArgumentException("Maximum capacity must be at least 1");
        }
        if (ttlMillis > MAX_TTL_MILLIS) {
            throw new IllegalArgumentException("TTL is too large to represent in nanoseconds");
        }
        this.maxCapacity = maxCapacity;
        this.ttlMillis = Math.max(0, ttlMillis);
        this.ttlNanos = TimeUnit.MILLISECONDS.toNanos(this.ttlMillis);
        this.ticker = Objects.requireNonNull(ticker);
        this.ttlEnabled = this.ttlMillis > 0;
        if (ttlEnabled && startWorker) {
            Thread worker = new Thread(this::cleanupLoop, "Map-TTL-Cleanup");
            worker.setDaemon(true);
            worker.start();
        }
    }

    private boolean expired(Node<K, V> node, long now) {
        // Subtraction handles nanoTime wrapping, unlike comparing absolute deadlines.
        return ttlEnabled && now - node.writtenAt >= ttlNanos;
    }

    private long now() {
        return ttlEnabled ? ticker.getAsLong() : 0;
    }

    private void unlinkExpiration(Node<K, V> node) {
        if (node.previous == null) {
            oldestExpiration = node.next;
        } else {
            node.previous.next = node.next;
        }
        if (node.next == null) {
            newestExpiration = node.previous;
        } else {
            node.next.previous = node.previous;
        }
        node.previous = null;
        node.next = null;
    }

    private void removeNode(Node<K, V> node) {
        entries.remove(node.key);
        if (ttlEnabled) {
            boolean wasHead = node == oldestExpiration;
            unlinkExpiration(node);
            if (wasHead) expirationChanged.signal();
        }
    }

    private void expireEntries(long now) {
        if (oldestExpiration == null || !expired(oldestExpiration, now)
                || cleanupDelay(now) > 0) return;
        removeExpiredEntries(now);
        lastCleanupFinishedAt = now();
        cleanupHasRun = true;
    }

    private long cleanupDelay(long now) {
        if (!cleanupHasRun) return 0;
        long elapsed = now - lastCleanupFinishedAt;
        return elapsed >= MIN_CLEANUP_INTERVAL_NANOS ? 0 : MIN_CLEANUP_INTERVAL_NANOS - elapsed;
    }

    private void removeExpiredEntries(long now) {
        while (oldestExpiration != null && expired(oldestExpiration, now)) {
            removeNode(oldestExpiration);
        }
    }

    // Targeted operations must treat expired keys as absent even between passes.
    // Called with the lock held; checking never removes an entry.
    private Node<K, V> liveNode(Object key) {
        Node<K, V> node = entries.get(key);
        return node == null || expired(node, now()) ? null : node;
    }

    private int liveSize(long now) {
        int size = entries.size();
        for (Node<K, V> node = oldestExpiration; node != null && expired(node, now); node = node.next) {
            size--;
        }
        return size;
    }

    // Called under the write lock with the node selected before any callback.
    // Keeping that selection preserves FIFO position if the callback crosses TTL.
    private V writeValue(K key, V value, Node<K, V> node) {
        long now = now();
        V previous = node == null ? null : node.value;
        if (node == null) {
            // A logically absent key may still have a physical expired mapping.
            // Replacing that mapping is a new insertion, with a new FIFO position.
            Node<K, V> stale = entries.get(key);
            if (stale != null) removeNode(stale);
        }
        boolean headChanged = oldestExpiration == null || node == oldestExpiration;
        if (node == null) {
            node = new Node<>(key, value);
            entries.put(key, node);
        } else if (ttlEnabled) {
            unlinkExpiration(node);
        }
        node.value = value;
        if (ttlEnabled) {
            node.writtenAt = now;
            node.previous = newestExpiration;
            if (newestExpiration == null) {
                oldestExpiration = node;
            } else {
                newestExpiration.next = node;
            }
            newestExpiration = node;
            if (headChanged) expirationChanged.signal();
        }
        if (entries.size() > maxCapacity) {
            // Make one slot, preferring an expired entry to a live FIFO victim.
            // This is bounded work even while a full cleanup pass is throttled.
            removeNode(oldestExpiration != null && expired(oldestExpiration, now)
                    ? oldestExpiration : entries.values().iterator().next());
        }
        return previous;
    }

    private <T> T mutate(Supplier<T> operation) {
        lock.writeLock().lock();
        try {
            if (inRemappingFunction) {
                throw new IllegalStateException("Remapping functions must not mutate this map");
            }
            return operation.get();
        } finally {
            lock.writeLock().unlock();
        }
    }

    private <T> T remap(Supplier<T> function) {
        inRemappingFunction = true;
        try {
            return function.get();
        } finally {
            inRemappingFunction = false;
        }
    }

    private void cleanupLoop() {
        lock.writeLock().lock();
        try {
            while (ttlEnabled) {
                long now = now();
                expireEntries(now);
                if (oldestExpiration == null) {
                    expirationChanged.await();
                } else {
                    now = now();
                    long untilExpiration = ttlNanos - (now - oldestExpiration.writtenAt);
                    // An overdue head must not cause a busy loop during the cooldown.
                    expirationChanged.awaitNanos(Math.max(untilExpiration, cleanupDelay(now)));
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public V get(Object key) {
        Objects.requireNonNull(key);
        lock.readLock().lock();
        try {
            Node<K, V> node = entries.get(key);
            return node == null || expired(node, now()) ? null : node.value;
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public V getOrDefault(Object key, V defaultValue) {
        V value = get(key);
        return value == null ? defaultValue : value;
    }

    @Override
    public boolean containsKey(Object key) {
        return get(key) != null;
    }

    @Override
    public boolean containsValue(Object value) {
        Objects.requireNonNull(value);
        lock.readLock().lock();
        try {
            long now = now();
            for (Node<K, V> node : entries.values()) {
                if (!expired(node, now) && value.equals(node.value)) return true;
            }
            return false;
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public int size() {
        lock.readLock().lock();
        try {
            return liveSize(now());
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public V put(K key, V value) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(value);
        return mutate(() -> writeValue(key, value, liveNode(key)));
    }

    @Override
    public V putIfAbsent(K key, V value) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(value);
        return mutate(() -> {
            Node<K, V> node = liveNode(key);
            return node == null ? writeValue(key, value, null) : node.value;
        });
    }

    @Override
    public V remove(Object key) {
        Objects.requireNonNull(key);
        return mutate(() -> {
            Node<K, V> node = liveNode(key);
            if (node == null) return null;
            removeNode(node);
            return node.value;
        });
    }

    @Override
    public boolean remove(Object key, Object value) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(value);
        return mutate(() -> {
            Node<K, V> node = liveNode(key);
            if (node == null || !value.equals(node.value)) return false;
            removeNode(node);
            return true;
        });
    }

    @Override
    public V replace(K key, V value) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(value);
        return mutate(() -> {
            Node<K, V> node = liveNode(key);
            if (node == null) return null;
            V previous = node.value;
            writeValue(key, value, node);
            return previous;
        });
    }

    @Override
    public boolean replace(K key, V oldValue, V newValue) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(oldValue);
        Objects.requireNonNull(newValue);
        return mutate(() -> {
            Node<K, V> node = liveNode(key);
            if (node == null || !oldValue.equals(node.value)) return false;
            writeValue(key, newValue, node);
            return true;
        });
    }

    @Override
    public V computeIfAbsent(K key, Function<? super K, ? extends V> function) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(function);
        return mutate(() -> {
            Node<K, V> node = liveNode(key);
            if (node != null) return node.value;
            V value = remap(() -> function.apply(key));
            if (value != null) writeValue(key, value, null);
            return value;
        });
    }

    @Override
    public V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> function) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(function);
        return mutate(() -> {
            Node<K, V> node = liveNode(key);
            return node == null ? null : computeValue(key, node, function);
        });
    }

    @Override
    public V compute(K key, BiFunction<? super K, ? super V, ? extends V> function) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(function);
        return mutate(() -> {
            Node<K, V> node = liveNode(key);
            return computeValue(key, node, function);
        });
    }

    private V computeValue(K key, Node<K, V> node,
                           BiFunction<? super K, ? super V, ? extends V> function) {
        V value = remap(() -> function.apply(key, node == null ? null : node.value));
        if (value == null) {
            if (node != null) removeNode(node);
        } else {
            writeValue(key, value, node);
        }
        return value;
    }

    @Override
    public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> function) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(value);
        Objects.requireNonNull(function);
        return mutate(() -> {
            Node<K, V> node = liveNode(key);
            if (node == null) {
                writeValue(key, value, null);
                return value;
            }
            return computeValue(key, node, (_, previous) -> function.apply(previous, value));
        });
    }

    @Override
    public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
        Objects.requireNonNull(function);
        mutate(() -> {
            for (K key : new ArrayList<>(entries.keySet())) {
                Node<K, V> node = entries.get(key);
                if (node != null && !expired(node, now())) {
                    V value = remap(() -> Objects.requireNonNull(function.apply(key, node.value)));
                    writeValue(key, value, node);
                }
            }
            return null;
        });
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> map) {
        Objects.requireNonNull(map);
        // Read the source before taking our write lock, avoiding cross-map lock order.
        List<Entry<K, V>> copy = new ArrayList<>();
        map.forEach((key, value) -> copy.add(new SimpleImmutableEntry<>(
                Objects.requireNonNull(key), Objects.requireNonNull(value))));
        mutate(() -> {
            for (Entry<K, V> entry : copy) {
                writeValue(entry.getKey(), entry.getValue(), liveNode(entry.getKey()));
            }
            return null;
        });
    }

    private void clearEntries() {
        entries.clear();
        oldestExpiration = null;
        newestExpiration = null;
        expirationChanged.signal();
    }

    @Override
    public void clear() {
        mutate(() -> {
            clearEntries();
            return null;
        });
    }

    public boolean clearIfFull() {
        return mutate(() -> {
            if (liveSize(now()) < maxCapacity) return false;
            clearEntries();
            return true;
        });
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }

    public boolean isAtCapacity() {
        return size() == maxCapacity;
    }

    /** Returns the configured TTL, or zero if constructed with TTL disabled. */
    public long getTtlMillis() {
        return ttlMillis;
    }

    public boolean isTtlEnabled() {
        return ttlEnabled;
    }

    public void shutdown() {
        mutate(() -> {
            // Final disposal bypasses the interval so disabling TTL cannot revive
            // expired entries that were retained during the cooldown.
            removeExpiredEntries(now());
            ttlEnabled = false;
            while (oldestExpiration != null) unlinkExpiration(oldestExpiration);
            expirationChanged.signal();
            return null;
        });
    }

    @Override
    public void close() {
        shutdown();
    }

    private List<Entry<K, V>> snapshot() {
        lock.readLock().lock();
        try {
            long now = now();
            List<Entry<K, V>> copy = new ArrayList<>(entries.size());
            for (Node<K, V> node : entries.values()) {
                if (!expired(node, now)) copy.add(new SimpleImmutableEntry<>(node.key, node.value));
            }
            return copy;
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public void forEach(BiConsumer<? super K, ? super V> action) {
        Objects.requireNonNull(action);
        for (Entry<K, V> entry : snapshot()) action.accept(entry.getKey(), entry.getValue());
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return entryView;
    }

    @Override
    public Set<K> keySet() {
        return keyView;
    }

    private final class KeyView extends AbstractSet<K> {
        @Override
        public int size() {
            return ConcurrentMapWithEvictionAndKeyTtlEnhanced.this.size();
        }

        @Override
        public boolean contains(Object key) {
            return containsKey(key);
        }

        @Override
        public boolean remove(Object key) {
            return ConcurrentMapWithEvictionAndKeyTtlEnhanced.this.remove(key) != null;
        }

        @Override
        public void clear() {
            ConcurrentMapWithEvictionAndKeyTtlEnhanced.this.clear();
        }

        @Override
        public Iterator<K> iterator() {
            Iterator<Entry<K, V>> iterator = entryView.iterator();
            return new Iterator<>() {
                @Override
                public boolean hasNext() {
                    return iterator.hasNext();
                }

                @Override
                public K next() {
                    return iterator.next().getKey();
                }

                @Override
                public void remove() {
                    iterator.remove();
                }
            };
        }
    }

    private final class EntryView extends AbstractSet<Entry<K, V>> {
        @Override
        public int size() {
            return ConcurrentMapWithEvictionAndKeyTtlEnhanced.this.size();
        }

        @Override
        public void clear() {
            ConcurrentMapWithEvictionAndKeyTtlEnhanced.this.clear();
        }

        @Override
        public boolean contains(Object object) {
            if (!(object instanceof Entry<?, ?> entry) || entry.getKey() == null
                    || entry.getValue() == null) return false;
            return entry.getValue().equals(get(entry.getKey()));
        }

        @Override
        public boolean remove(Object object) {
            if (!(object instanceof Entry<?, ?> entry) || entry.getKey() == null
                    || entry.getValue() == null) return false;
            return ConcurrentMapWithEvictionAndKeyTtlEnhanced.this.remove(entry.getKey(), entry.getValue());
        }

        @Override
        public Iterator<Entry<K, V>> iterator() {
            Iterator<Entry<K, V>> copy = snapshot().iterator();
            return new Iterator<>() {
                private Entry<K, V> current;

                @Override
                public boolean hasNext() {
                    return copy.hasNext();
                }

                @Override
                public Entry<K, V> next() {
                    if (!copy.hasNext()) throw new NoSuchElementException();
                    current = copy.next();
                    return current;
                }

                @Override
                public void remove() {
                    if (current == null) throw new IllegalStateException();
                    ConcurrentMapWithEvictionAndKeyTtlEnhanced.this.remove(current.getKey(), current.getValue());
                    current = null;
                }
            };
        }
    }
}
