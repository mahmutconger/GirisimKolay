package com.anlarsinsoftware.girisimkolay.core.data

import com.anlarsinsoftware.girisimkolay.core.domain.Clock

class MemoryCache<K, V>(
    private val clock: Clock,
    private val ttlMillis: Long
) {
    private val entries = mutableMapOf<K, CacheEntry<V>>()

    fun get(key: K): V? {
        val entry = entries[key] ?: return null
        return if (clock.nowMillis() - entry.cachedAt <= ttlMillis) {
            entry.value
        } else {
            entries.remove(key)
            null
        }
    }

    fun put(key: K, value: V) {
        entries[key] = CacheEntry(value = value, cachedAt = clock.nowMillis())
    }

    fun invalidate(key: K) {
        entries.remove(key)
    }

    fun clear() {
        entries.clear()
    }

    private data class CacheEntry<V>(
        val value: V,
        val cachedAt: Long
    )
}
