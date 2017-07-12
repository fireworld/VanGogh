package cc.colorcat.vangogh;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by cxx on 2017/7/6.
 * xx.ch@outlook.com
 */

public class VanGogh {
    private static volatile VanGogh singleton;

    private Dispatcher dispatcher;
    private int maxRunning;
    private int retryCount;

    private List<Interceptor> interceptors;
    private Downloader downloader;

    private Cache<Bitmap> memoryCache;
    private DiskCache diskCache;

    private Task.Options defaultOptions;
    private Resources resources;
    private boolean debug;

    public static void setSingleton(VanGogh vanGogh) {
        synchronized (VanGogh.class) {
            if (singleton != null) {
                throw new IllegalStateException("Singleton instance already exists.");
            }
            singleton = vanGogh;
        }
    }

    public static VanGogh with(Context ctx) {
        if (singleton == null) {
            synchronized (VanGogh.class) {
                if (singleton == null) {
                    singleton = new Builder(ctx).build();
                }
            }
        }
        return singleton;
    }

    private VanGogh(Builder builder, Cache<Bitmap> memoryCache, DiskCache diskCache) {
        maxRunning = builder.maxRunning;
        retryCount = builder.retryCount;
        interceptors = Collections.unmodifiableList(new ArrayList<>(builder.interceptors));
        downloader = builder.downloader;
        defaultOptions = builder.defaultOptions;
        resources = builder.resources;
        debug = builder.debug;
        this.memoryCache = memoryCache;
        this.diskCache = diskCache;
        this.dispatcher = new Dispatcher(this, builder.executor);
    }

    public Task.Creator load(String url) {
        if (url == null || url.length() == 0) throw new NullPointerException("url is empty");
        return this.load(Uri.parse(url));
    }

    public Task.Creator load(Uri uri) {
        if (uri == null) throw new NullPointerException("uri == null");
        String stableKey = Utils.md5(uri.toString());
        return new Task.Creator(this, uri, stableKey);
    }

    public void close() {
        singleton = null;
    }

    void enqueue(Task task) {
        dispatcher.enqueue(task);
    }

    Task.Options defaultOptions() {
        return defaultOptions.clone();
    }

    int maxRunning() {
        return maxRunning;
    }

    int retryCount() {
        return retryCount;
    }

    List<Interceptor> interceptors() {
        return interceptors;
    }

    Downloader downloader() {
        return downloader;
    }

    Cache<Bitmap> memoryCache() {
        return memoryCache;
    }

    DiskCache diskCache() {
        return diskCache;
    }

    Resources resources() {
        return resources;
    }

    boolean debug() {
        return debug;
    }

    Bitmap quickMemoryCacheCheck(String stableKey) {
        return memoryCache.get(stableKey);
    }


    public static class Builder {
        private ExecutorService executor;
        private int maxRunning = 6;
        private int retryCount = 1;

        private List<Interceptor> interceptors = new ArrayList<>();
        private Downloader downloader;

        private long memoryCacheSize;
        private File cacheDirectory;
        private long diskCacheSize;

        private Task.Options defaultOptions;
        private Resources resources;
        private boolean debug = false;

        public Builder(Context context) {
            if (context == null) throw new NullPointerException("context == null");
            downloader = new HttpDownloader();
            memoryCacheSize = Utils.calculateMemoryCacheSize(context);
            cacheDirectory = Utils.getCacheDirectory(context);
            diskCacheSize = (long) Math.min(50 * 1024 * 1024, cacheDirectory.getUsableSpace() * 0.1);
            defaultOptions = new Task.Options();
            resources = context.getResources();
        }

        public Builder executor(ExecutorService executor) {
            if (executor == null) {
                throw new NullPointerException("executor == null");
            }
            this.executor = executor;
            return this;
        }

        public Builder maxRunning(int maxRunning) {
            if (maxRunning < 1) {
                throw new IllegalArgumentException("maxRunning < 1");
            }
            this.maxRunning = maxRunning;
            return this;
        }

        public Builder retryCount(int retryCount) {
            if (retryCount < 0) {
                throw new IllegalArgumentException("retryCount must be positive");
            }
            this.retryCount = retryCount;
            return this;
        }

        public Builder addInterceptor(Interceptor interceptor) {
            if (interceptor == null) {
                throw new NullPointerException("interceptor == null");
            }
            interceptors.add(interceptor);
            return this;
        }

        public Builder Downloader(Downloader downloader) {
            if (downloader == null) {
                throw new NullPointerException("downloader == null");
            }
            this.downloader = downloader;
            return this;
        }

        public Builder memoryCacheSize(long sizeInByte) {
            if (sizeInByte < 1L) {
                throw new IllegalArgumentException("sizeInByte < 1");
            }
            this.memoryCacheSize = sizeInByte;
            return this;
        }

        public Builder diskCache(File directory) {
            if (directory == null) {
                throw new NullPointerException("directory == null");
            }
            this.cacheDirectory = directory;
            return this;
        }

        public Builder diskCacheSize(long sizeInByte) {
            if (sizeInByte < 1L) {
                throw new IllegalArgumentException("sizeInByte < 1");
            }
            this.diskCacheSize = sizeInByte;
            return this;
        }

        public Builder defaultOptions(Task.Options options) {
            if (options == null) throw new NullPointerException("options == null");
            defaultOptions = options;
            return this;
        }

        public Builder debug(boolean debug) {
            this.debug = debug;
            return this;
        }

        public Builder enableLog(boolean enabled) {
            LogUtils.init(enabled);
            return this;
        }

        public VanGogh build() {
            DiskCache diskCache = null;
            try {
                diskCache = DiskCache.open(cacheDirectory, diskCacheSize);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (executor == null) {
                executor = new ThreadPoolExecutor(maxRunning, 10, 60L, TimeUnit.SECONDS,
                        new LinkedBlockingDeque<Runnable>(), new ThreadPoolExecutor.DiscardOldestPolicy());
            }
            return new VanGogh(this, new MemoryCache((int) memoryCacheSize), diskCache);
        }
    }
}
