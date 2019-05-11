package com.edoctor.api.utils

import org.hibernate.internal.util.collections.ConcurrentReferenceHashMap

class MutexFactory<K> {

    private val map: ConcurrentReferenceHashMap<K, Any> = ConcurrentReferenceHashMap()

    fun getMutex(key: K): Any = map.compute(key) { k, v -> v ?: Any() } as Any

}