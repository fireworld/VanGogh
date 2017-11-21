package cc.colorcat.vangogh;

import android.support.annotation.Nullable;

/**
 * Created by cxx on 2017/7/6.
 * xx.ch@outlook.com
 */
public interface Cache<T> {

    @Nullable
    T get(String key);

    void save(String key, T t);

    void remove(String key);

    void clear();

    long size();

    long maxSize();
}
