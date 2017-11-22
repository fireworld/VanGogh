package cc.colorcat.vangogh;

import android.support.annotation.Nullable;

/**
 * Created by cxx on 2017/7/6.
 * xx.ch@outlook.com
 */
public interface Cache<T> {

    /**
     * Returns the value for {@code key} if it exists in the cache or null if not cached.
     */
    @Nullable
    T get(String key);

    void save(String key, T t);

    void remove(String key);

    void clear();

    long size();

    /**
     * @return the maximum sum of the sizes of the entries in this cache.
     */
    long maxSize();
}
