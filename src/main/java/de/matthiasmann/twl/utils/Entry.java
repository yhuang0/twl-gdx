package de.matthiasmann.twl.utils;

public class Entry<K, V> extends HashEntry<K, Entry<K, V>> {
    V value;

    public Entry(K key, V value) {
        super(key);
        this.value = value;
    }
}

