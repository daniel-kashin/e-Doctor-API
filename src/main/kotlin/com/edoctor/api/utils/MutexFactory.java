package com.edoctor.api.utils;

import org.hibernate.internal.util.collections.ConcurrentReferenceHashMap;
import org.springframework.stereotype.Component;

@Component
public class MutexFactory<K> {

    private ConcurrentReferenceHashMap<K, Object> map;

    public MutexFactory() {
        this.map = new ConcurrentReferenceHashMap<>();
    }

    public Object getMutex(K key) {
        return this.map.compute(key, (k, v) -> v == null ? new Object() : v);
    }

}