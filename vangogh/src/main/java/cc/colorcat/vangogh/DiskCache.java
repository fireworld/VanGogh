package cc.colorcat.vangogh;

import android.support.annotation.Nullable;

import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by cxx on 2017/7/7.
 * xx.ch@outlook.com
 */
public final class DiskCache {
    private final LinkedHashMap<String, File> map;
    private File directory;

    private long maxSize;
    private long size;
    private int putCount;
    private int evictionCount;
    private int hitCount;
    private int missCount;

    public static DiskCache open(File directory, long maxSize) throws IOException {
        if (maxSize <= 0) {
            throw new IllegalArgumentException("maxSize <= 0");
        }
        if (directory.isFile()) {
            throw new IOException(directory.getAbsolutePath() + " is not a directory!");
        }
        DiskCache cache;
        synchronized (DiskCache.class) {
            File dir = new File(directory, "diskCache");
            if (dir.exists() || dir.mkdirs()) {
                cache = new DiskCache(dir, maxSize);
                cache.readFilesToMap();
                return cache;
            }
        }
        throw new IOException("create directory failure, path = " + directory.getAbsolutePath());
    }

    private DiskCache(File directory, long maxSize) {
        this.directory = directory;
        this.maxSize = maxSize;
        this.map = new LinkedHashMap<>(0, 0.75F, true);
    }

    private void readFilesToMap() {
        File[] files = directory.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.isFile();
            }
        });
        List<File> list = Arrays.asList(files);
        Collections.sort(list, new FileComparator());
        putCount = list.size();
        for (int i = 0; i < putCount; ++i) {
            File file = list.get(i);
            size += Utils.sizeOf(file);
            map.put(file.getName(), file);
        }
    }

    @Nullable
    public File get(String key) {
        if (key == null) {
            throw new NullPointerException("key == null");
        }

        synchronized (this) {
            File value = map.get(key);
            if (value != null) {
                if (value.exists()) {
                    ++hitCount;
                    if (value.canRead()) {
                        return value;
                    }
                } else {
                    throw new IllegalStateException(value.getAbsolutePath() + " was deleted.");
                }
            } else {
                ++missCount;
            }
        }
        return null;
    }

    public void save(String key, InputStream is) throws IOException {
        if (key == null || is == null) {
            throw new NullPointerException("key == null || is == null");
        }
        synchronized (this) {
            File newFile = new File(directory, key);
            if (newFile.exists() && !map.containsKey(key)) {
                throw new IllegalStateException(newFile.getAbsolutePath() + " exists but map not contains.");
            }
            FileOutputStream fos = null;
            try {
                if (!newFile.exists() || realRemove(key)) {
                    fos = new FileOutputStream(newFile);
                    Utils.justDump(is, fos);
                    if (newFile.exists()) {
                        ++putCount;
                        size += Utils.sizeOf(newFile);
                        File previous = map.put(key, newFile);
                        if (previous != null) {
                            throw new IllegalStateException(previous.getAbsolutePath() + " exists.");
                        }
                    }
                }
            } finally {
                Utils.close(fos);
            }
        }
        trimToSize(maxSize);
    }

    public void remove(String key) {
        if (key == null) {
            throw new NullPointerException("key == null");
        }

        synchronized (this) {
            realRemove(key);
        }
    }

    private boolean realRemove(String key) {
        File previous = map.remove(key);
        if (previous != null) {
            if (previous.exists()) {
                long previousSize = Utils.sizeOf(previous);
                if (previous.delete()) {
                    size -= previousSize;
                    ++evictionCount;
                    return true;
                } else {
                    map.put(key, previous);
                    return false;
                }
            } else {
                throw new IllegalStateException(previous.getAbsolutePath() + "was deleted.");
            }
        }
        return true;
    }

    public final synchronized void clear() {
        trimToSize(-1);
    }

    public final synchronized long size() {
        return size;
    }

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

    private void trimToSize(long maxSize) {
        while (true) {
            String key;
            File value;
            synchronized (this) {
                if (size < 0 || (map.isEmpty() && size != 0)) {
                    throw new IllegalStateException(getClass().getName() +
                            ".sizeOf() is reporting inconsistent results.");
                }
                if (size <= maxSize || map.isEmpty()) break;
                Map.Entry<String, File> toEvict = map.entrySet().iterator().next();
                key = toEvict.getKey();
                if (!realRemove(key)) {
                    throw new RuntimeException("can't delete " + toEvict.getValue().getAbsolutePath());
                }
            }
        }
    }

    private static class FileComparator implements Comparator<File> {
        @Override
        public int compare(File f1, File f2) {
            return (int) (f1.lastModified() - f2.lastModified());
        }
    }
}
