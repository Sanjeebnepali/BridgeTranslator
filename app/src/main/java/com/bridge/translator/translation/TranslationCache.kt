package com.bridge.translator.translation

import android.util.LruCache

class TranslationCache {
    private val cache = LruCache<String, String>(500)

    fun get(key: String): String? = cache.get(key)

    fun put(key: String, value: String) = cache.put(key, value)

    fun clear() = cache.evictAll()
}
