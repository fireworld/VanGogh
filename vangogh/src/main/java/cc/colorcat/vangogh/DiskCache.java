package cc.colorcat.vangogh;

import android.support.annotation.NonNull;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by cxx on 2017/7/7.
 * xx.ch@outlook.com
 */
public final class DiskCache {
    private final LinkedHashMap<String, Snapshot> map;
    private File directory;

    private long maxSize;
    private long size;

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
                cache.cleanDirtyFile();
                cache.readSnapshots();
                return cache;
            }
        }
        throw new IOException("create directory failure, path = " + directory.getAbsolutePath());
    }

    public Snapshot getSnapshot(String key) {
        Snapshot snapshot = map.get(key);
        if (snapshot == null) {
            snapshot = new Snapshot(key);
            map.put(key, snapshot);
        }
        return snapshot;
    }

    private DiskCache(File directory, long maxSize) {
        this.directory = directory;
        this.maxSize = maxSize;
        this.map = new LinkedHashMap<>(0, 0.75F, true);
    }

    private void cleanDirtyFile() throws IOException {
        File[] dirty = directory.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.isFile() && file.getName().endsWith(".tmp");
            }
        });
        Utils.deleteIfExists(dirty);
    }

    private void readSnapshots() throws IOException {
        File[] files = directory.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.isFile();
            }
        });
        List<File> list = Arrays.asList(files);
        Collections.sort(list, new FileComparator());
        int putCount = list.size();
        for (int i = 0; i < putCount; ++i) {
            File file = list.get(i);
            size += Utils.sizeOf(file);
            String name = file.getName();
            map.put(name, new Snapshot(name));
        }
    }

    private void completeWrite(Snapshot snapshot, boolean success) throws IOException {
        try {
            File dirty = snapshot.getDirtyFile();
            File clean = snapshot.getCleanFile();
            if (!success) {
                Utils.deleteIfExists(dirty);
                if (!clean.exists()) {
                    map.remove(snapshot.key);
                }
            } else {
                long oldLength = clean.length();
                long newLength = dirty.length();
                Utils.renameTo(dirty, clean, true);
                size = size - oldLength + newLength;
                trimToSize(maxSize);
            }
        } finally {
            snapshot.writing = false;
            snapshot.committed = false;
            snapshot.hasErrors = false;
        }
    }

    private void trimToSize(long maxSize) throws IOException {
        Iterator<Map.Entry<String, Snapshot>> iterator = map.entrySet().iterator();
        while (size > maxSize && iterator.hasNext()) {
            Map.Entry<String, Snapshot> toEvict = iterator.next();
            String key = toEvict.getKey();
            Snapshot value = toEvict.getValue();
            if (value.readCount == 0 && !value.writing) {
                File clean = value.getCleanFile();
                long cleanLength = clean.length();
                Utils.deleteIfExists(value.getCleanFile());
                size -= cleanLength;
                iterator.remove();
            }
        }
    }

    public final class Snapshot {
        private String key;
        private int readCount = 0;
        private boolean writing = false;
        private boolean committed = false;
        private boolean hasErrors = false;

        private Snapshot(String key) {
            this.key = key;
        }

        public InputStream getInputStream() {
            synchronized (DiskCache.this) {
                try {
                    ++readCount;
                    return new FileInputStream(getCleanFile());
                } catch (FileNotFoundException e) {
                    --readCount;
                    return null;
                }
            }
        }

        public OutputStream getOutputStream() {
            synchronized (DiskCache.this) {
                if (!writing) {
                    try {
                        FileOutputStream fos = new FileOutputStream(getDirtyFile());
                        writing = true;
                        return new FaultHidingOutputStream(fos);
                    } catch (FileNotFoundException e) {
                        writing = false;
                        throw new IllegalStateException(directory + " not exists.");
                    }
                }
                return null;
            }
        }

        public void complete() throws IOException {
            synchronized (DiskCache.this) {
                --readCount;
                if (readCount < 0) {
                    throw new IllegalStateException("readCount < 0");
                }
                if (readCount == 0) {
                    if (writing && committed) {
                        completeWrite(this, !hasErrors);
                    }
                }
            }
        }

        public void commit() throws IOException {
            synchronized (DiskCache.this) {
                if (writing && !committed) {
                    committed = true;
                    if (readCount == 0) {
                        completeWrite(this, !hasErrors);
                    }
                } else {
                    throw new IllegalStateException("not writing or committed.");
                }
            }
        }

        private File getCleanFile() {
            return new File(directory, key);
        }

        private File getDirtyFile() {
            return new File(directory, key + ".tmp");
        }


        private class FaultHidingOutputStream extends FilterOutputStream {

            private FaultHidingOutputStream(OutputStream out) {
                super(out);
            }

            @Override
            public void write(int oneByte) {
                try {
                    out.write(oneByte);
                } catch (IOException e) {
                    hasErrors = true;
                }
            }

            @Override
            public void write(@NonNull byte[] buffer, int offset, int length) {
                try {
                    out.write(buffer, offset, length);
                } catch (IOException e) {
                    hasErrors = true;
                }
            }

            @Override
            public void close() {
                try {
                    out.close();
                } catch (IOException e) {
                    hasErrors = true;
                }
            }

            @Override
            public void flush() {
                try {
                    out.flush();
                } catch (IOException e) {
                    hasErrors = true;
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
