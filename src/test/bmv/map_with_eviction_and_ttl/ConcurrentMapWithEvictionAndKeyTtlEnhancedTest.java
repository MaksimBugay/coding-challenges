package bmv.map_with_eviction_and_ttl;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;

class ConcurrentMapWithEvictionAndKeyTtlEnhancedTest {
    private static final long MS = 1_000_000L;

    private static <K, V> ConcurrentMapWithEvictionAndKeyTtlEnhanced<K, V> map(
            int capacity, long ttl, AtomicLong clock) {
        return new ConcurrentMapWithEvictionAndKeyTtlEnhanced<>(capacity, ttl, clock::get, false);
    }

    @Test
    void onlyBackgroundPassesCleanExpiredEntriesAndRespectCooldown() throws Exception {
        AtomicLong clock = new AtomicLong();
        try (var map = ConcurrentMapWithEvictionAndKeyTtlEnhancedTest.<String, Integer>map(20, 10, clock)) {
            map.put("first", 1);
            clock.set(5 * MS);
            map.put("second", 2);
            clock.set(10 * MS);
            map.put("probe", 3);
            assertEquals(3, storedSize(map)); // Writes never start cleanup.
            runCleanupPass(map); // Simulate the worker at a deterministic time.
            assertEquals(10 * MS, field(map, "lastCleanupFinishedAt"));
            clock.set(15 * MS);
            assertNull(map.get("second"));
            assertEquals(1, map.size());
            assertEquals(2, storedSize(map));
            for (int time = 16; time < 110; time++) {
                clock.set(time * MS);
                map.put("probe", time);
                map.putAll(Map.of("bulk", time));
                map.replaceAll((k, v) -> v + 1);
                map.compute("probe", (k, v) -> v + 1);
                assertFalse(map.clearIfFull());
                assertEquals(2, map.size());
                assertEquals(3, storedSize(map)); // Expired "second" stays queued.
                assertEquals(10 * MS, field(map, "lastCleanupFinishedAt"));
            }
            clock.set(110 * MS);
            map.put("probe", 4);
            assertEquals(3, storedSize(map)); // Even after the cooldown, writes don't clean.
            assertEquals(10 * MS, field(map, "lastCleanupFinishedAt"));
            runCleanupPass(map);
            assertEquals(2, storedSize(map));
            assertEquals(110 * MS, field(map, "lastCleanupFinishedAt"));
        }
    }

    @Test
    void targetedOperationsTreatExpiredKeysAsAbsentDuringCooldown() throws Exception {
        List<Consumer<ConcurrentMapWithEvictionAndKeyTtlEnhanced<String, Integer>>> operations = List.of(
                m -> assertNull(m.put("stale", 9)),
                m -> assertNull(m.putIfAbsent("stale", 9)),
                m -> assertEquals(9, m.computeIfAbsent("stale", k -> 9)),
                m -> assertNull(m.computeIfPresent("stale", (k, v) -> fail("Expired value"))),
                m -> assertEquals(9, m.compute("stale", (k, v) -> { assertNull(v); return 9; })),
                m -> assertEquals(9, m.merge("stale", 9, (a, b) -> fail("Expired value"))),
                m -> assertNull(m.replace("stale", 9)),
                m -> assertFalse(m.replace("stale", 2, 9)),
                m -> assertNull(m.remove("stale")),
                m -> assertFalse(m.remove("stale", 2)));
        for (var operation : operations) {
            AtomicLong clock = new AtomicLong();
            try (var map = ConcurrentMapWithEvictionAndKeyTtlEnhancedTest.<String, Integer>map(10, 10, clock)) {
                map.put("first", 1);
                clock.set(5 * MS);
                map.put("stale", 2);
                map.put("untouched", 3);
                clock.set(10 * MS);
                runCleanupPass(map);
                map.put("probe", 4);
                clock.set(15 * MS);
                operation.accept(map);
                assertTrue(((Map<?, ?>) field(map, "entries")).containsKey("untouched"));
                assertEquals(10 * MS, field(map, "lastCleanupFinishedAt"));
                assertExpirationIndex(map);
            }
        }
    }

    @Test
    void capacityEvictionPrefersExpiredEntriesDuringCooldown() throws Exception {
        AtomicLong clock = new AtomicLong();
        try (var map = ConcurrentMapWithEvictionAndKeyTtlEnhancedTest.<String, Integer>map(3, 10, clock)) {
            map.put("first", 1);
            clock.set(MS);
            map.put("live", 2);
            clock.set(2 * MS);
            map.put("stale", 3);
            clock.set(8 * MS);
            map.put("live", 4);
            clock.set(10 * MS);
            runCleanupPass(map);
            map.put("probe", 5);
            clock.set(13 * MS);
            map.put("new", 6); // Must evict "stale", not the older live entry.
            assertEquals(Map.of("live", 4, "probe", 5, "new", 6), map);
            assertEquals(10 * MS, field(map, "lastCleanupFinishedAt"));
            assertEquals(3, storedSize(map));
            assertExpirationIndex(map);
        }
    }

    @Test
    void shutdownDiscardsExpiredEntriesEvenDuringCooldown() throws Exception {
        AtomicLong clock = new AtomicLong();
        var map = ConcurrentMapWithEvictionAndKeyTtlEnhancedTest.<String, Integer>map(3, 10, clock);
        map.put("first", 1);
        clock.set(5 * MS);
        map.put("stale", 2);
        clock.set(10 * MS);
        runCleanupPass(map);
        map.put("live", 3);
        clock.set(15 * MS);
        map.shutdown();
        assertEquals(Map.of("live", 3), map);
        assertEquals(1, storedSize(map));
        assertFalse(map.isTtlEnabled());
    }

    @Test
    void cooldownHandlesNanoTimeWrap() throws Exception {
        long start = Long.MAX_VALUE - 50 * MS;
        AtomicLong clock = new AtomicLong(start);
        try (var map = ConcurrentMapWithEvictionAndKeyTtlEnhancedTest.<String, Integer>map(3, 10, clock)) {
            map.put("first", 1);
            clock.addAndGet(5 * MS);
            map.put("second", 2);
            clock.addAndGet(5 * MS);
            runCleanupPass(map);
            map.put("probe", 3);
            clock.addAndGet(99 * MS); // Clock has now wrapped.
            map.put("probe", 4);
            assertEquals(2, storedSize(map));
            assertEquals(start + 10 * MS, field(map, "lastCleanupFinishedAt"));
            clock.addAndGet(MS);
            map.put("probe", 5);
            assertEquals(2, storedSize(map));
            runCleanupPass(map);
            assertEquals(1, storedSize(map));
            assertEquals(clock.get(), field(map, "lastCleanupFinishedAt"));
        }
    }

    @Test
    void normalOperationsNeverCleanUnrelatedExpiredEntries() throws Exception {
        AtomicLong clock = new AtomicLong();
        try (var map = ConcurrentMapWithEvictionAndKeyTtlEnhancedTest.<String, Integer>map(100, 10, clock)) {
            for (int i = 0; i < 50; i++) map.put("expired-" + i, i);
            clock.set(1_000 * MS); // No worker: all entries are physically retained.
            assertTrue(map.isEmpty());
            assertEquals(0, map.size());
            assertNull(map.get("expired-0"));
            assertFalse(map.containsValue(0));
            assertTrue(map.entrySet().isEmpty());
            assertNull(map.replace("expired-0", 9));
            assertFalse(map.replace("expired-0", 0, 9));
            assertNull(map.remove("expired-0"));
            assertFalse(map.remove("expired-0", 0));
            assertNull(map.computeIfPresent("expired-0", (k, v) -> fail("Expired value")));
            assertNull(map.computeIfAbsent("expired-0", k -> null));
            assertNull(map.compute("expired-0", (k, v) -> { assertNull(v); return null; }));
            map.replaceAll((k, v) -> fail("Expired value"));
            assertFalse(map.clearIfFull());
            map.put("live", 1);
            map.putIfAbsent("live", 2);
            map.compute("live", (k, v) -> v + 1);
            map.merge("live", 1, Integer::sum);
            map.putAll(Map.of("bulk", 4));
            map.replaceAll((k, v) -> v + 1);
            assertEquals(Map.of("live", 4, "bulk", 5), map);
            assertEquals(52, storedSize(map));
            assertEquals(false, field(map, "cleanupHasRun"));
            assertExpirationIndex(map);
        }
    }

    @Test
    void replacingAnExpiredMappingGivesItANewFifoPositionWithoutCleaningOthers() throws Exception {
        AtomicLong clock = new AtomicLong();
        try (var map = ConcurrentMapWithEvictionAndKeyTtlEnhancedTest.<String, Integer>map(4, 10, clock)) {
            map.put("a", 1);
            clock.set(5 * MS);
            map.put("b", 2);
            clock.set(10 * MS);
            assertNull(map.put("a", 3));
            assertEquals(List.of("b", "a"), new ArrayList<>(map.keySet()));
            clock.set(15 * MS);
            map.put("c", 4);
            assertEquals(3, storedSize(map)); // Expired b is not cleaned by inserting c.
            assertEquals(2, map.size());
            assertEquals(false, field(map, "cleanupHasRun"));
            assertExpirationIndex(map);
        }
    }

    @Test
    void refreshSurvivesOriginalDeadlineAndExpiresAtNewDeadline() {
        AtomicLong clock = new AtomicLong();
        try (var map = ConcurrentMapWithEvictionAndKeyTtlEnhancedTest.<String, String>map(2, 100, clock)) {
            map.put("key", "old");
            clock.set(60 * MS);
            assertEquals("old", map.put("key", "new"));
            clock.set(100 * MS);
            assertEquals("new", map.get("key"));
            assertEquals(1, map.size());
            clock.set(160 * MS);
            assertNull(map.get("key"));
            assertEquals(0, map.size());
        }
    }

    @Test
    void removingClearingAndEvictingCannotExpireReinsertedKey() {
        List<Consumer<ConcurrentMapWithEvictionAndKeyTtlEnhanced<String, String>>> removals = List.of(
                m -> m.remove("key"), Map::clear,
                ConcurrentMapWithEvictionAndKeyTtlEnhanced::clearIfFull,
                m -> m.put("other", "evicts"),
                m -> m.compute("key", (k, v) -> null));
        for (var removal : removals) {
            AtomicLong clock = new AtomicLong();
            try (var map = ConcurrentMapWithEvictionAndKeyTtlEnhancedTest.<String, String>map(1, 100, clock)) {
                map.put("key", "old");
                clock.set(60 * MS);
                removal.accept(map);
                map.put("key", "new");
                clock.set(100 * MS);
                assertEquals("new", map.get("key"));
                assertEquals(1, map.size());
                clock.set(160 * MS);
                assertTrue(map.isEmpty());
            }
        }
    }

    @Test
    void allSuccessfulUpdateMethodsRefreshTtl() {
        List<Consumer<ConcurrentMapWithEvictionAndKeyTtlEnhanced<String, Integer>>> updates = List.of(
                m -> m.put("key", 2), m -> m.putAll(Map.of("key", 2)),
                m -> m.replace("key", 2), m -> assertTrue(m.replace("key", 1, 2)),
                m -> m.compute("key", (k, v) -> 2),
                m -> m.computeIfPresent("key", (k, v) -> 2),
                m -> m.merge("key", 1, Integer::sum), m -> m.replaceAll((k, v) -> 2));
        for (var update : updates) {
            AtomicLong clock = new AtomicLong();
            try (var map = ConcurrentMapWithEvictionAndKeyTtlEnhancedTest.<String, Integer>map(2, 100, clock)) {
                map.put("key", 1);
                clock.set(60 * MS);
                update.accept(map);
                clock.set(100 * MS);
                assertEquals(2, map.get("key"));
                clock.set(160 * MS);
                assertNull(map.get("key"));
            }
        }
    }

    @Test
    void readsAndFailedConditionalUpdatesDoNotRefreshTtl() {
        AtomicLong clock = new AtomicLong();
        try (var map = ConcurrentMapWithEvictionAndKeyTtlEnhancedTest.<String, Integer>map(2, 100, clock)) {
            map.put("key", 1);
            clock.set(60 * MS);
            assertEquals(1, map.get("key"));
            assertTrue(map.containsKey("key"));
            assertEquals(1, map.putIfAbsent("key", 2));
            assertEquals(1, map.computeIfAbsent("key", k -> fail("Unexpected callback")));
            assertFalse(map.replace("key", 2, 3));
            assertFalse(map.remove("key", 2));
            clock.set(100 * MS);
            assertNull(map.get("key"));
        }
    }

    @Test
    void readsExcludeExpiredEntriesBeforeMaintenance() throws Exception {
        AtomicLong clock = new AtomicLong();
        try (var map = ConcurrentMapWithEvictionAndKeyTtlEnhancedTest.<String, Integer>map(2, 100, clock)) {
            map.put("key", 1);
            clock.set(100 * MS);
            assertEquals(1, storedSize(map)); // No worker has removed the entry.
            assertNull(map.get("key"));
            assertEquals(9, map.getOrDefault("key", 9));
            assertFalse(map.containsKey("key"));
            assertFalse(map.containsValue(1));
            assertFalse(map.entrySet().iterator().hasNext());
            assertFalse(map.keySet().iterator().hasNext());
            assertFalse(map.values().iterator().hasNext());
            map.forEach((k, v) -> fail("Expired entry visible to forEach"));
            assertEquals(1, storedSize(map));
            assertFalse(map.clearIfFull());
            assertTrue(map.isEmpty());
        }
    }

    @Test
    void expirationPrecedesCapacityEvictionAndUpdatesPreserveFifo() {
        AtomicLong clock = new AtomicLong();
        try (var map = ConcurrentMapWithEvictionAndKeyTtlEnhancedTest.<String, Integer>map(2, 100, clock)) {
            map.put("a", 1);
            map.put("b", 2);
            clock.set(50 * MS);
            map.put("a", 3);
            assertEquals(List.of("a", "b"), new ArrayList<>(map.keySet()));
            clock.set(100 * MS);
            map.put("c", 4);
            assertEquals(Map.of("a", 3, "c", 4), map);
            map.get("a");
            map.put("d", 5);
            assertEquals(List.of("c", "d"), new ArrayList<>(map.keySet()));
            assertTrue(map.isAtCapacity());
            assertTrue(map.clearIfFull());
            assertTrue(map.isEmpty());
        }
    }

    @Test
    void remappingAcrossDeadlinePreservesFifoAndRefreshesAtCompletion() {
        AtomicLong clock = new AtomicLong();
        try (var map = ConcurrentMapWithEvictionAndKeyTtlEnhancedTest.<String, Integer>map(2, 100, clock)) {
            map.put("a", 1);
            clock.set(50 * MS);
            map.put("b", 2);
            clock.set(60 * MS);
            assertEquals(3, map.compute("a", (k, v) -> {
                clock.set(110 * MS);
                return v + 2;
            }));
            assertEquals(List.of("a", "b"), new ArrayList<>(map.keySet()));
            clock.set(209 * MS);
            assertEquals(3, map.get("a"));
            clock.set(210 * MS);
            assertNull(map.get("a"));
        }
    }

    @Test
    void absentExpiredAndDeletedComputeCases() {
        AtomicLong clock = new AtomicLong();
        try (var map = ConcurrentMapWithEvictionAndKeyTtlEnhancedTest.<String, Integer>map(3, 100, clock)) {
            assertNull(map.computeIfAbsent("a", k -> null));
            assertNull(map.computeIfPresent("a", (k, v) -> fail("Unexpected callback")));
            assertEquals(1, map.computeIfAbsent("a", k -> 1));
            assertEquals(2, map.compute("b", (k, v) -> { assertNull(v); return 2; }));
            assertEquals(3, map.merge("c", 3, (a, b) -> fail("Unexpected callback")));
            clock.set(100 * MS);
            assertNull(map.replace("a", 9));
            assertFalse(map.replace("b", 2, 9));
            assertEquals(4, map.computeIfAbsent("a", k -> 4));
            assertNull(map.computeIfPresent("a", (k, v) -> null));
            map.put("b", 5);
            assertNull(map.merge("b", 1, (a, b) -> null));
            assertTrue(map.isEmpty());
        }
    }

    @Test
    void viewsAreBackedAndEntriesAreImmutableSnapshots() {
        try (var map = new ConcurrentMapWithEvictionAndKeyTtlEnhanced<String, Integer>(10)) {
            Set<String> keys = map.keySet();
            Collection<Integer> values = map.values();
            Set<Map.Entry<String, Integer>> entries = map.entrySet();
            map.put("a", 1);
            Iterator<Map.Entry<String, Integer>> iterator = entries.iterator();
            assertThrows(IllegalStateException.class, iterator::remove);
            Map.Entry<String, Integer> snapshot = iterator.next();
            assertThrows(UnsupportedOperationException.class, () -> snapshot.setValue(2));
            map.put("a", 2);
            assertEquals(1, snapshot.getValue());
            iterator.remove(); // Captured value no longer matches.
            assertEquals(2, map.get("a"));
            assertThrows(IllegalStateException.class, iterator::remove);
            assertTrue(keys.remove("a"));
            map.putAll(Map.of("b", 3, "c", 4));
            assertTrue(values.remove(3));
            assertFalse(entries.remove(Map.entry("c", 99)));
            assertTrue(entries.remove(Map.entry("c", 4)));
            map.put("d", 5);
            assertEquals(Map.of("d", 5), map);
            assertEquals(Map.of("d", 5).hashCode(), map.hashCode());
            assertEquals(Map.of("d", 5), Map.copyOf(map));
            values.clear();
            assertTrue(keys.isEmpty());
        }
    }

    @Test
    void remappingExceptionsAndRecursiveMutationLeaveStateConsistent() {
        try (var map = new ConcurrentMapWithEvictionAndKeyTtlEnhanced<String, Integer>(2)) {
            map.put("a", 1);
            assertThrows(IllegalArgumentException.class,
                    () -> map.compute("a", (k, v) -> { throw new IllegalArgumentException(); }));
            assertThrows(IllegalStateException.class, () -> map.compute("a", (k, v) -> {
                map.put("b", 2);
                return 3;
            }));
            assertThrows(IllegalStateException.class,
                    () -> map.compute("a", (k, v) -> { map.shutdown(); return 3; }));
            assertEquals(Map.of("a", 1), map);
            assertEquals(2, map.compute("a", (k, v) -> map.get(k) + map.size()));
            map.forEach((k, v) -> map.put(k, v + 1)); // forEach runs outside the lock.
            assertEquals(3, map.get("a"));
        }
    }

    @Test
    void tickerWrapAndInputValidation() {
        AtomicLong clock = new AtomicLong(Long.MAX_VALUE - 50 * MS);
        try (var map = ConcurrentMapWithEvictionAndKeyTtlEnhancedTest.<String, Integer>map(1, 100, clock)) {
            map.put("a", 1);
            clock.addAndGet(99 * MS);
            assertEquals(1, map.get("a"));
            clock.addAndGet(MS);
            assertNull(map.get("a"));
        }
        assertThrows(IllegalArgumentException.class,
                () -> new ConcurrentMapWithEvictionAndKeyTtlEnhanced<>(0));
        assertThrows(IllegalArgumentException.class,
                () -> new ConcurrentMapWithEvictionAndKeyTtlEnhanced<>(1, Long.MAX_VALUE));
        try (var map = new ConcurrentMapWithEvictionAndKeyTtlEnhanced<String, Integer>(Integer.MAX_VALUE, -1)) {
            assertEquals(0, map.getTtlMillis());
            assertFalse(map.isTtlEnabled());
            map.put("a", 1); // Capacity arithmetic must not overflow or preallocate a huge table.
            assertThrows(NullPointerException.class, () -> map.put(null, 1));
            assertThrows(NullPointerException.class, () -> map.put("b", null));
            assertThrows(NullPointerException.class, () -> map.get(null));
            assertThrows(NullPointerException.class, () -> map.replaceAll((k, v) -> null));
            assertEquals(Map.of("a", 1), map);
        }
    }

    @Test
    void shutdownDisablesExpirationForSurvivingAndFutureEntries() throws Exception {
        AtomicLong clock = new AtomicLong();
        var map = ConcurrentMapWithEvictionAndKeyTtlEnhancedTest.<String, Integer>map(2, 100, clock);
        map.put("expired", 1);
        clock.set(60 * MS);
        map.put("live", 2);
        clock.set(100 * MS);
        map.shutdown();
        assertFalse(map.isTtlEnabled());
        assertEquals(100, map.getTtlMillis());
        assertEquals(Map.of("live", 2), map);
        map.put("new", 3);
        clock.set(100_000 * MS);
        assertEquals(Map.of("live", 2, "new", 3), map);
        assertNull(field(map, "oldestExpiration"));
        assertNull(field(map, "newestExpiration"));
        map.close();
    }

    @Test
    void timerMetadataStaysBoundedUnderHeavyChurn() throws Exception {
        AtomicLong clock = new AtomicLong();
        try (var map = ConcurrentMapWithEvictionAndKeyTtlEnhancedTest.<Integer, Integer>map(4, 1_000_000, clock)) {
            map.put(0, 0);
            Object node = field(map, "oldestExpiration");
            for (int i = 1; i < 10_000; i++) map.put(0, i);
            assertSame(node, field(map, "oldestExpiration"));
            for (int i = 0; i < 20_000; i++) {
                map.put(i % 17, i);
                if (i % 3 == 0) map.remove(i % 17);
                if (i % 101 == 0) map.clear();
                if (i % 100 == 0) assertExpirationIndex(map);
            }
            assertExpirationIndex(map);
        }
    }

    @Test
    void randomizedOperationsMatchSimpleReferenceModel() {
        AtomicLong clock = new AtomicLong();
        Random random = new Random(77231);
        Map<Integer, Integer> expected = new LinkedHashMap<>();
        Map<Integer, Long> written = new LinkedHashMap<>();
        try (var map = ConcurrentMapWithEvictionAndKeyTtlEnhancedTest.<Integer, Integer>map(7, 10, clock)) {
            for (int i = 0; i < 10_000; i++) {
                clock.addAndGet(random.nextInt(3) * MS);
                expected.keySet().removeIf(k -> clock.get() - written.get(k) >= 10 * MS);
                int key = random.nextInt(15);
                int value = random.nextInt(100);
                switch (random.nextInt(4)) {
                    case 0 -> {
                        assertEquals(expected.put(key, value), map.put(key, value));
                        written.put(key, clock.get());
                    }
                    case 1 -> assertEquals(expected.remove(key), map.remove(key));
                    case 2 -> {
                        Integer old = expected.putIfAbsent(key, value);
                        assertEquals(old, map.putIfAbsent(key, value));
                        if (old == null) written.put(key, clock.get());
                    }
                    case 3 -> assertEquals(expected.get(key), map.get(key));
                }
                if (expected.size() > 7) expected.remove(expected.keySet().iterator().next());
                assertEquals(expected, map);
                assertEquals(new ArrayList<>(expected.keySet()), new ArrayList<>(map.keySet()));
            }
        }
    }

    @Test
    void concurrentInsertAndComputeHaveOneWinner() throws Exception {
        try (var map = new ConcurrentMapWithEvictionAndKeyTtlEnhanced<String, Integer>(2)) {
            AtomicInteger winners = new AtomicInteger();
            parallel(12, () -> { if (map.putIfAbsent("a", 1) == null) winners.incrementAndGet(); });
            assertEquals(1, winners.get());
            AtomicInteger calls = new AtomicInteger();
            parallel(12, () -> assertEquals(2,
                    map.computeIfAbsent("b", key -> { calls.incrementAndGet(); return 2; })));
            assertEquals(1, calls.get());
        }
    }

    @Test
    void concurrentMergeComputeAndCompareAndReplaceDoNotLoseUpdates() throws Exception {
        try (var map = new ConcurrentMapWithEvictionAndKeyTtlEnhanced<String, Integer>(3)) {
            map.putAll(Map.of("merge", 0, "compute", 0, "replace", 0));
            parallel(8, () -> {
                for (int i = 0; i < 2_000; i++) {
                    map.merge("merge", 1, Integer::sum);
                    map.compute("compute", (k, v) -> v + 1);
                    while (true) {
                        Integer old = map.get("replace");
                        if (map.replace("replace", old, old + 1)) break;
                    }
                }
            });
            assertEquals(Map.of("merge", 16_000, "compute", 16_000, "replace", 16_000), map);
        }
    }

    @Test
    void concurrentChurnKeepsCapacityAndExpirationIndexConsistent() throws Exception {
        AtomicLong clock = new AtomicLong();
        try (var map = ConcurrentMapWithEvictionAndKeyTtlEnhancedTest.<Integer, Integer>map(32, 1, clock)) {
            parallel(8, () -> {
                for (int i = 0; i < 3_000; i++) {
                    int key = (i + (int) Thread.currentThread().threadId()) % 100;
                    clock.addAndGet(100);
                    map.put(key, i);
                    map.computeIfPresent(key, (k, v) -> v + 1);
                    map.remove(key, i + 1);
                    map.putIfAbsent(key, i);
                    if (i % 47 == 0) map.replaceAll((k, v) -> v + 1);
                    if (i % 101 == 0) map.clearIfFull();
                    assertTrue(map.size() <= 32);
                    if (i % 100 == 0) {
                        for (var entry : map.entrySet()) assertNotNull(entry.getValue());
                    }
                }
            });
            assertExpirationIndex(map);
        }
    }

    @Test
    void backgroundWorkerReclaimsEntriesWithoutReadsAndShutdownIsPrompt() throws Exception {
        try (var map = new ConcurrentMapWithEvictionAndKeyTtlEnhanced<String, Integer>(1, 20)) {
            map.put("a", 1);
            long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(5);
            while (storedSize(map) != 0 && System.nanoTime() - deadline < 0) Thread.sleep(5);
            assertEquals(0, storedSize(map));
        }
        // Capture the worker before shutdown, then check that it actually exits.
        Set<Thread> before = Thread.getAllStackTraces().keySet();
        var map = new ConcurrentMapWithEvictionAndKeyTtlEnhanced<String, Integer>(1, 60_000);
        map.put("a", 1);
        Set<Thread> workers = new HashSet<>(Thread.getAllStackTraces().keySet());
        workers.removeAll(before);
        workers.removeIf(t -> !t.getName().equals("Map-TTL-Cleanup"));
        assertEquals(1, workers.size());
        long start = System.nanoTime();
        map.shutdown();
        assertTrue(System.nanoTime() - start < TimeUnit.SECONDS.toNanos(1));
        for (Thread worker : workers) {
            worker.join(1_000);
            assertFalse(worker.isAlive());
        }
    }

    private static void parallel(int threads, Runnable action) throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        CountDownLatch ready = new CountDownLatch(threads);
        CountDownLatch start = new CountDownLatch(1);
        List<Future<?>> futures = new ArrayList<>();
        try {
            for (int i = 0; i < threads; i++) futures.add(executor.submit(() -> {
                ready.countDown();
                try {
                    if (!start.await(5, TimeUnit.SECONDS)) throw new AssertionError("Start timeout");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new AssertionError(e);
                }
                action.run();
            }));
            assertTrue(ready.await(5, TimeUnit.SECONDS));
            start.countDown();
            for (Future<?> future : futures) future.get(20, TimeUnit.SECONDS);
        } finally {
            executor.shutdownNow();
            assertTrue(executor.awaitTermination(5, TimeUnit.SECONDS));
        }
    }

    private static Object field(Object target, String name) throws Exception {
        Field field = target.getClass().getDeclaredField(name);
        field.setAccessible(true);
        return field.get(target);
    }

    private static void runCleanupPass(Object map) throws Exception {
        ReentrantReadWriteLock lock = (ReentrantReadWriteLock) field(map, "lock");
        lock.writeLock().lock();
        try {
            Method now = map.getClass().getDeclaredMethod("now");
            Method cleanup = map.getClass().getDeclaredMethod("expireEntries", long.class);
            now.setAccessible(true);
            cleanup.setAccessible(true);
            cleanup.invoke(map, now.invoke(map));
        } finally {
            lock.writeLock().unlock();
        }
    }

    private static int storedSize(Object map) throws Exception {
        ReentrantReadWriteLock lock = (ReentrantReadWriteLock) field(map, "lock");
        lock.readLock().lock();
        try {
            return ((Map<?, ?>) field(map, "entries")).size();
        } finally {
            lock.readLock().unlock();
        }
    }

    private static void assertExpirationIndex(ConcurrentMapWithEvictionAndKeyTtlEnhanced<?, ?> map)
            throws Exception {
        Map<?, ?> entries = (Map<?, ?>) field(map, "entries");
        Set<Object> seen = new HashSet<>();
        Object node = field(map, "oldestExpiration");
        Object previous = null;
        while (node != null) {
            assertTrue(seen.add(node), "Cycle in expiration list");
            assertSame(previous, field(node, "previous"));
            previous = node;
            node = field(node, "next");
        }
        assertSame(previous, field(map, "newestExpiration"));
        assertEquals(new HashSet<>(entries.values()), seen);
        assertTrue(seen.size() <= map.getMaxCapacity());
    }

    // Optional standalone runner; these tests also run through Maven and the IDE.
    public static void main(String[] args) throws Exception {
        var tests = new ConcurrentMapWithEvictionAndKeyTtlEnhancedTest();
        int passed = 0;
        for (Method method : tests.getClass().getDeclaredMethods()) {
            if (!method.isAnnotationPresent(Test.class)) continue;
            try {
                method.invoke(tests);
                System.out.println("PASS " + method.getName());
                passed++;
            } catch (InvocationTargetException e) {
                throw new AssertionError(method.getName(), e.getCause());
            }
        }
        System.out.println("Passed " + passed + " tests");
    }
}
