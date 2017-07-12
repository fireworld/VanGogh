package cc.colorcat.vangogh;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by cxx on 2017/7/6.
 * xx.ch@outlook.com
 */

public class MemoryCache implements Cache<Bitmap> {
    private final LinkedHashMap<String, Bitmap> map;
    private final int maxSize;

    private int size;
    private int putCount;
    private int evictionCount;
    private int hitCount;
    private int missCount;

    public MemoryCache(Context ctx) {
        this(Utils.calculateMemoryCacheSize(ctx));
    }

    public MemoryCache(int maxSize) {
        if (maxSize <= 0) {
            throw new IllegalArgumentException("Max size must be positive.");
        }
        this.maxSize = maxSize;
        this.map = new LinkedHashMap<>(0, 0.75F, true);
    }


    @Nullable
    @Override
    public Bitmap get(String key) {
        if (key == null) {
            throw new NullPointerException("stableKey == null");
        }

        Bitmap value;
        synchronized (this) {
            value = map.get(key);
            if (value != null) {
                ++hitCount;
                return value;
            }
            ++missCount;
        }
        return null;
    }

    @Override
    public void save(String key, Bitmap bitmap) {
        if (key == null || bitmap == null) {
            throw new NullPointerException("stableKey == null || bitmap == null");
        }

        Bitmap previous;
        synchronized (this) {
            ++putCount;
            size += Utils.sizeOf(bitmap);
            previous = map.put(key, bitmap);
            if (previous != null) {
                size -= Utils.sizeOf(previous);
            }
        }

        trimToSize(maxSize);
    }

    @Override
    public void remove(String key) {
        if (key == null) {
            throw new NullPointerException("stableKey == null");
        }

        Bitmap previous;
        synchronized (this) {
            previous = map.remove(key);
            if (previous != null) {
                size -= Utils.sizeOf(previous);
                ++evictionCount;
            }
        }
    }

    @Override
    public final synchronized void clear() {
        trimToSize(-1);
    }

    @Override
    public final synchronized long size() {
        return size;
    }

    @Override
    public final synchronized long maxSize() {
        return maxSize;
    }

    public final synchronized int hitCount() {
        return hitCount;
    }

    public final synchronized int missCount() {
        return missCount;
    }

    public final synchronized int putCount() {
        return putCount;
    }

    public final synchronized int evictionCount() {
        return evictionCount;
    }

    private void trimToSize(int maxSize) {
        while (true) {
            String key;
            Bitmap value;
            synchronized (this) {
                if (size < 0 || (map.isEmpty() && size != 0)) {
                    throw new IllegalStateException(getClass().getName() +
                            ".sizeOf() is reporting inconsistent results!");
                }

                if (size <= maxSize || map.isEmpty()) break;
                Map.Entry<String, Bitmap> toEvict = map.entrySet().iterator().next();
                key = toEvict.getKey();
                value = toEvict.getValue();
                map.remove(key);
                size -= Utils.sizeOf(value);
                ++evictionCount;
            }
        }
    }
}
