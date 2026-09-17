package bmv.map_with_eviction_and_ttl;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.BiFunction;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class ConcurrentMapWithEvictionAndKeyTtl<K, V> implements Map<K, V> {

    private final LinkedHashMap<K, V> insertionOrderMap;
    private final int maxCapacity;
    private final ReentrantReadWriteLock lock;

    // TTL support fields
    private final long ttlMillis;
    private final DelayQueue<ExpirationEntry<K>> expirationQueue;
    private final ExecutorService cleanupExecutor;
    private final AtomicBoolean running;

    /**
     * Internal class representing a key expiration entry for the DelayQueue.
     * Implements Delayed interface to enable time-based expiration.
     */
    private record ExpirationEntry<K>(K key, long expirationTime) implements Delayed {
        /**
         * Creates a new expiration entry for the given key.
         *
         * @param key            the key to expire
         * @param expirationTime time-to-live in milliseconds
         */
        private ExpirationEntry(K key, long expirationTime) {
            this.key = key;
            this.expirationTime = System.currentTimeMillis() + expirationTime;
        }

        @Override
        public long getDelay(TimeUnit unit) {
            long remainingTime = expirationTime - System.currentTimeMillis();
            return unit.convert(remainingTime, TimeUnit.MILLISECONDS);
        }

        @Override
        public int compareTo(Delayed other) {
            if (this == other) return 0;
            ExpirationEntry<?> otherEntry = (ExpirationEntry<?>) other;
            return Long.compare(this.expirationTime, otherEntry.expirationTime);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof ExpirationEntry<?>(Object key1, long time))) return false;
            return Objects.equals(key, key1) && expirationTime == time;
        }

        @Override
        public String toString() {
            return "ExpirationEntry{key=" + key + ", remainingMs=" + getDelay(TimeUnit.MILLISECONDS) + "}";
        }
    }

    /**
     * Creates a new concurrent map with capacity-based eviction only (no TTL).
     *
     * @param maxCapacity the maximum number of entries before eviction occurs
     * @throws IllegalArgumentException if maxCapacity is less than 1
     */
    public ConcurrentMapWithEvictionAndKeyTtl(int maxCapacity) {
        this(maxCapacity, 0); // 0 means no TTL
    }

    /**
     * Creates a new concurrent map with both capacity-based eviction and TTL support.
     *
     * @param maxCapacity the maximum number of entries before eviction occurs
     * @param ttlMillis   time-to-live for keys in milliseconds (0 or negative means no TTL)
     * @throws IllegalArgumentException if maxCapacity is less than 1
     */
    public ConcurrentMapWithEvictionAndKeyTtl(int maxCapacity, long ttlMillis) {
        if (maxCapacity < 1) {
            throw new IllegalArgumentException("Maximum capacity must be at least 1");
        }

        this.maxCapacity = maxCapacity;
        this.ttlMillis = ttlMillis;
        this.insertionOrderMap = new LinkedHashMap<>(maxCapacity * 4 / 3, 0.75f, false) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
                return size() > maxCapacity;
            }
        };
        this.lock = new ReentrantReadWriteLock();

        // Initialize TTL support if enabled
        if (ttlMillis > 0) {
            this.expirationQueue = new DelayQueue<>();
            this.running = new AtomicBoolean(true);
            this.cleanupExecutor = Executors.newSingleThreadExecutor(r -> {
                Thread t = new Thread(r, "TTL-Cleanup-Thread");
                t.setDaemon(true);
                return t;
            });
            startCleanupThread();
        } else {
            this.expirationQueue = null;
            this.running = null;
            this.cleanupExecutor = null;
        }
    }

    /**
     * Starts the background cleanup thread for TTL expiration.
     * This method is called automatically when TTL is enabled.
     */
    private void startCleanupThread() {
        cleanupExecutor.submit(() -> {
            try {
                while (running.get()) {
                    ExpirationEntry<K> expired = expirationQueue.take(); // Blocks until expiration
                    executeWithWriteLock(() -> insertionOrderMap.remove(expired.key));
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                // Exit cleanup thread gracefully
            }
        });
    }

    /**
     * Schedules a key for expiration if TTL is enabled.
     *
     * @param key the key to schedule for expiration
     */
    private void scheduleKeyExpiration(K key) {
        if (expirationQueue != null && ttlMillis > 0) {
            expirationQueue.offer(new ExpirationEntry<>(key, ttlMillis));
        }
    }

    /**
     * Executes a write operation under write lock protection.
     *
     * @param operation the operation to execute
     * @return the result of the operation
     */
    private <T> T executeWithWriteLock(Supplier<T> operation) {
        lock.writeLock().lock();
        try {
            return operation.get();
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Executes a read operation under read lock protection.
     *
     * @param operation the operation to execute
     * @return the result of the operation
     */
    private <T> T executeWithReadLock(Supplier<T> operation) {
        lock.readLock().lock();
        try {
            return operation.get();
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Executes a void write operation under write lock protection.
     *
     * @param operation the operation to execute
     */
    private void executeWithWriteLock(Runnable operation) {
        lock.writeLock().lock();
        try {
            operation.run();
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public V put(K key, V value) {
        return executeWithWriteLock(() -> {
            V oldValue = insertionOrderMap.put(key, value);
            scheduleKeyExpiration(key);
            return oldValue;
        });
    }

    @Override
    public V get(Object key) {
        return executeWithReadLock(() -> insertionOrderMap.get(key));
    }

    @Override
    public V remove(Object key) {
        return executeWithWriteLock(() -> insertionOrderMap.remove(key));
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        executeWithWriteLock(() -> {
            insertionOrderMap.putAll(m);
            // Schedule expiration for all newly added keys
            for (K key : m.keySet()) {
                scheduleKeyExpiration(key);
            }
        });
    }

    @Override
    public void clear() {
        executeWithWriteLock(insertionOrderMap::clear);
    }

    @Override
    public int size() {
        return executeWithReadLock(insertionOrderMap::size);
    }

    @Override
    public boolean isEmpty() {
        return executeWithReadLock(insertionOrderMap::isEmpty);
    }

    @Override
    public boolean containsKey(Object key) {
        return executeWithReadLock(() -> insertionOrderMap.containsKey(key));
    }

    @Override
    public boolean containsValue(Object value) {
        return executeWithReadLock(() -> insertionOrderMap.containsValue(value));
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }

    public boolean isAtCapacity() {
        return size() >= maxCapacity;
    }

    /**
     * Atomically clears the map if it has reached maximum capacity.
     * This operation is performed under a write lock to ensure atomicity.
     *
     * @return true if the map was cleared, false if it was not at capacity
     */
    public boolean clearIfFull() {
        return executeWithWriteLock(() -> {
            if (insertionOrderMap.size() >= maxCapacity) {
                insertionOrderMap.clear();
                return true;
            }
            return false;
        });
    }

    @Override
    public Set<K> keySet() {
        return executeWithReadLock(() -> new LinkedHashSet<>(insertionOrderMap.keySet()));
    }

    @Override
    public Collection<V> values() {
        return executeWithReadLock(() -> new ArrayList<>(insertionOrderMap.values()));
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return executeWithReadLock(() -> new LinkedHashSet<>(insertionOrderMap.entrySet()));
    }

    public V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        return executeWithWriteLock(() -> {
            V result = insertionOrderMap.compute(key, remappingFunction);
            // If compute resulted in a value being present, schedule expiration
            if (result != null) {
                scheduleKeyExpiration(key);
            }
            return result;
        });
    }

    /**
     * Shuts down the TTL cleanup thread gracefully.
     * This method should be called when the map is no longer needed to prevent resource leaks.
     * After calling this method, TTL functionality will be disabled.
     */
    public void shutdown() {
        if (cleanupExecutor != null && running != null) {
            running.set(false);
            cleanupExecutor.shutdown();
            try {
                if (!cleanupExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                    cleanupExecutor.shutdownNow();
                    if (!cleanupExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                        System.err.println("TTL cleanup thread did not terminate gracefully");
                    }
                }
            } catch (InterruptedException e) {
                cleanupExecutor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Returns the TTL value in milliseconds.
     *
     * @return TTL in milliseconds, or 0 if TTL is disabled
     */
    public long getTtlMillis() {
        return ttlMillis;
    }

    /**
     * Checks if TTL is enabled for this map.
     *
     * @return true if TTL is enabled, false otherwise
     */
    public boolean isTtlEnabled() {
        return ttlMillis > 0;
    }

    @Override
    public String toString() {
        return executeWithReadLock(() ->
                String.format("ConcurrentMapWithEvictionAndKeyTtl{size=%d, maxCapacity=%d, ttlMs=%d, map=%s}",
                        size(), maxCapacity, ttlMillis, insertionOrderMap.toString()));
    }
}