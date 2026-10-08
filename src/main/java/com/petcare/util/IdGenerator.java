package com.petcare.util;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Utility class to generate formatted unique IDs for various domain entities.
 * Examples: P001, APP001, APT001, MED001, VAC001.
 */
public class IdGenerator {
    private static final ConcurrentMap<String, AtomicInteger> COUNTERS = new ConcurrentHashMap<>();

    public static String nextId(String prefix) {
        AtomicInteger counter = COUNTERS.computeIfAbsent(prefix, k -> new AtomicInteger(0));
        int val = counter.incrementAndGet();
        return String.format("%s%03d", prefix, val);
    }

    /**
     * Seeds or advances the counter if pre-existing sample data is loaded.
     */
    public static void setCounterIfGreater(String prefix, int value) {
        COUNTERS.compute(prefix, (k, existing) -> {
            if (existing == null) {
                return new AtomicInteger(value);
            }
            if (value > existing.get()) {
                existing.set(value);
            }
            return existing;
        });
    }

    public static void reset() {
        COUNTERS.clear();
    }
}
