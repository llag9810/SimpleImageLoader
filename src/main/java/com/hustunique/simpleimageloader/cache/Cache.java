package com.hustunique.simpleimageloader.cache;

/**
 * Created by yifan on 3/14/17.
 */

public interface Cache<K, V> {

    /**
     * Gets an value for the specified {@code key} or return null.
     *
     * @param key key
     * @return the value or null if not exist.
     */
    V get(K key);

    /**
     * Puts an value in the cache for the specified {@code key}.
     *
     * @param key key
     * @param value value
     * @return true if successly put.
     */
    boolean put(K key, V value);


    /**
     * Returns the current available size of the cache.
     *
     * @return current memory size.
     */
    long getAvailableSize();

}

