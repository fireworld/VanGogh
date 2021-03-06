package cc.colorcat.vangogh;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.text.TextUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by cxx on 2017/7/6.
 * xx.ch@outlook.com
 */
@SuppressWarnings("unused")
public class VanGogh {
    private static volatile VanGogh singleton;

    private final Dispatcher dispatcher;
    private final int maxRunning;
    private final int retryCount;
    private final int connectTimeOut;
    private final int readTimeOut;

    private final List<Interceptor> interceptors;
    private final Downloader downloader;
    private final int defaultFromPolicy;

    private final Cache<Bitmap> memoryCache;
    private final DiskCache diskCache;

    private final Task.Options defaultOptions;
    private final Resources resources;
    private final Resources.Theme theme;
    private final boolean debug;

    private final List<Transformation> transformations;

    private final Drawable loadingDrawable;
    private final Drawable errorDrawable;

    private final boolean fade;

    /**
     * Set the global instance.
     * NOTE: This method must be called before calls to {@link #with}.
     */
    public static void setSingleton(VanGogh vanGogh) {
        synchronized (VanGogh.class) {
            if (singleton != null) {
                throw new IllegalStateException("Singleton instance already exists.");
            }
            singleton = vanGogh;
        }
    }

    /**
     * Get the global instance.
     * If the global instance is null which will be initialized with default.
     */
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

    public static VanGogh get() {
        if (singleton == null) {
            throw new IllegalStateException("The singleton is null.");
        }
        return singleton;
    }

    private VanGogh(Builder builder, Cache<Bitmap> memoryCache, DiskCache diskCache) {
        maxRunning = builder.maxRunning;
        retryCount = builder.retryCount;
        connectTimeOut = builder.connectTimeOut;
        readTimeOut = builder.readTimeOut;
        interceptors = Utils.immutableList(builder.interceptors);
        downloader = builder.downloader;
        defaultFromPolicy = builder.defaultFromPolicy;
        defaultOptions = builder.defaultOptions;
        resources = builder.resources;
        theme = builder.theme;
        debug = builder.debug;
        transformations = Utils.immutableList(builder.transformations);
        loadingDrawable = builder.loadingDrawable;
        errorDrawable = builder.errorDrawable;
        fade = builder.fade;
        this.memoryCache = memoryCache;
        this.diskCache = diskCache;
        this.dispatcher = new Dispatcher(this, builder.executor);
    }

    /**
     * Create a {@link Task.Creator} using the specified path.
     *
     * @param uri May be a remote URL, file or android resource.
     * @see #load(Uri)
     * @see #load(File)
     * @see #load(int)
     */
    public Task.Creator load(String uri) {
        return this.load(TextUtils.isEmpty(uri) ? Uri.EMPTY : Uri.parse(uri));
    }

    /**
     * Create a {@link Task.Creator} using the specified drawable resource ID.
     *
     * @see #load(Uri)
     * @see #load(File)
     * @see #load(String)
     */
    public Task.Creator load(@DrawableRes int resId) {
        Uri uri = Uri.parse(Utils.SCHEME_VANGOGH + "://" + Utils.HOST_RESOURCE + "?id=" + resId);
        return this.load(uri);
    }

    /**
     * Create a {@link Task.Creator} using the specified image file.
     *
     * @see #load(Uri)
     * @see #load(String)
     * @see #load(int)
     */
    public Task.Creator load(File file) {
        return this.load(file == null ? Uri.EMPTY : Uri.fromFile(file));
    }

    /**
     * Create a {@link Task.Creator} using the specified uri.
     *
     * @see #load(String)
     * @see #load(File)
     * @see #load(int)
     */
    public Task.Creator load(Uri uri) {
        Uri u = (uri == null ? Uri.EMPTY : uri);
        String stableKey = Utils.md5(u.toString());
        return new Task.Creator(this, u, stableKey);
    }

    /**
     * Pause all tasks.
     *
     * @see #resume()
     */
    public void pause() {
        dispatcher.pause();
    }

    /**
     * Resume all tasks.
     *
     * @see #pause()
     */
    public void resume() {
        dispatcher.resume();
    }

    /**
     * Clear all pending tasks.
     */
    public void clear() {
        dispatcher.clear();
    }

    /**
     * Clear all cached bitmaps from the memory.
     */
    public void releaseMemory() {
        memoryCache.clear();
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

    int connectTimeOut() {
        return connectTimeOut;
    }

    int readTimeOut() {
        return readTimeOut;
    }

    List<Interceptor> interceptors() {
        return interceptors;
    }

    Downloader downloader() {
        return downloader.clone();
    }

    int defaultFromPolicy() {
        return defaultFromPolicy;
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

    Resources.Theme theme() {
        return theme;
    }

    boolean debug() {
        return debug;
    }

    List<Transformation> transformations() {
        return transformations;
    }

    boolean fade() {
        return fade;
    }

    Drawable defaultLoading() {
        return loadingDrawable;
    }

    Drawable defaultError() {
        return errorDrawable;
    }

    Bitmap checkMemoryCache(String stableKey) {
        return memoryCache.get(stableKey);
    }


    public static class Builder {
        private ExecutorService executor;
        private int maxRunning;
        private int retryCount;
        private int connectTimeOut;
        private int readTimeOut;

        private List<Interceptor> interceptors;
        private Downloader downloader;
        private int defaultFromPolicy;

        private long memoryCacheSize;
        private File cacheDirectory;
        private long diskCacheSize;

        private Task.Options defaultOptions;
        private Resources resources;
        private Resources.Theme theme;
        private boolean debug;

        private List<Transformation> transformations;
        private boolean fade;

        private Drawable loadingDrawable;
        private Drawable errorDrawable;

        public Builder(Context context) {
            maxRunning = 6;
            retryCount = 1;
            connectTimeOut = 5000;
            readTimeOut = 5000;
            interceptors = new ArrayList<>(4);
            downloader = new HttpDownloader();
            defaultFromPolicy = From.ANY.policy;
            memoryCacheSize = Utils.calculateMemoryCacheSize(context);
            cacheDirectory = Utils.getCacheDirectory(context);
            diskCacheSize = (long) Math.min(50 * 1024 * 1024, cacheDirectory.getUsableSpace() * 0.1);
            defaultOptions = new Task.Options();
            resources = context.getResources();
            theme = context.getTheme();
            debug = false;
            transformations = new ArrayList<>(4);
            fade = true;
        }

        /**
         * @param executor The executor service for loading images in the background.
         */
        public Builder executor(ExecutorService executor) {
            if (executor == null) {
                throw new NullPointerException("executor == null");
            }
            this.executor = executor;
            return this;
        }

        /**
         * @param maxRunning The maximum number of concurrent tasks.
         * @throws IllegalArgumentException if the maxRunning less than 1.
         */
        public Builder maxRunning(int maxRunning) {
            if (maxRunning < 1) {
                throw new IllegalArgumentException("maxRunning < 1");
            }
            this.maxRunning = maxRunning;
            return this;
        }

        /**
         * @param retryCount The maximum number of retries.
         * @throws IllegalArgumentException if the retryCount less than 0.
         */
        public Builder retryCount(int retryCount) {
            if (retryCount < 0) {
                throw new IllegalArgumentException("retryCount < 0");
            }
            this.retryCount = retryCount;
            return this;
        }

        public Builder connectTimeOut(int timeOut) {
            if (timeOut < 0) {
                throw new IllegalArgumentException("timeOut < 0");
            }
            this.connectTimeOut = timeOut;
            return this;
        }

        public Builder readTimeOut(int timeOut) {
            if (timeOut < 0) {
                throw new IllegalArgumentException("timeOut < 0");
            }
            this.readTimeOut = timeOut;
            return this;
        }

        public Builder addInterceptor(Interceptor interceptor) {
            if (interceptor == null) {
                throw new NullPointerException("interceptor == null");
            }
            if (!interceptors.contains(interceptor)) {
                interceptors.add(interceptor);
            }
            return this;
        }

        /**
         * @param downloader The {@link Downloader} will be used for download images.
         * @throws NullPointerException if downloader is null
         * @see HttpDownloader
         */
        public Builder downloader(Downloader downloader) {
            if (downloader == null) {
                throw new NullPointerException("downloader == null");
            }
            this.downloader = downloader;
            return this;
        }

        /**
         * The default policy of image source.
         * Any source, <code>From.ANY.policy</code>
         * Memory and Disk, <code>From.MEMORY.policy | From.DISK.policy</code>
         * Memory and Network, <code>From.MEMORY.policy | From.NETWORK.policy</code>
         * ...
         *
         * @see From
         */
        public Builder defaultFromPolicy(int fromPolicy) {
            From.checkFromPolicy(fromPolicy);
            this.defaultFromPolicy = fromPolicy;
            return this;
        }

        public Builder memoryCacheSize(long sizeInByte) {
            if (sizeInByte <= 0L) {
                throw new IllegalArgumentException("sizeInByte <= 0");
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
            if (sizeInByte <= 0L) {
                throw new IllegalArgumentException("sizeInByte <= 0");
            }
            this.diskCacheSize = sizeInByte;
            return this;
        }

        public Builder defaultOptions(Task.Options options) {
            if (options == null) {
                throw new NullPointerException("options == null");
            }
            defaultOptions = options;
            return this;
        }

        public Builder debug(boolean debug) {
            this.debug = debug;
            return this;
        }

        public Builder addTransformation(Transformation transformation) {
            if (transformation == null) {
                throw new NullPointerException("transformation == null");
            }
            if (!transformations.contains(transformation)) {
                transformations.add(transformation);
            }
            return this;
        }

        public Builder fade(boolean fade) {
            this.fade = fade;
            return this;
        }

        /**
         * The default drawable to be used while the image is being loaded.
         */
        public Builder defaultLoading(Drawable loading) {
            loadingDrawable = loading;
            return this;
        }

        /**
         * The default drawable to be used while the image is being loaded.
         */
        public Builder defaultLoading(@DrawableRes int resId) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                loadingDrawable = resources.getDrawable(resId, theme);
            } else {
                loadingDrawable = resources.getDrawable(resId);
            }
            return this;
        }

        /**
         * The default drawable to be used if the request image could not be loaded.
         */
        public Builder defaultError(Drawable error) {
            errorDrawable = error;
            return this;
        }

        /**
         * The default drawable to be used if the request image could not be loaded.
         */
        public Builder defaultError(@DrawableRes int resId) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                errorDrawable = resources.getDrawable(resId, theme);
            } else {
                errorDrawable = resources.getDrawable(resId);
            }
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
                LogUtils.e(e);
            }
            if (executor == null) {
                executor = new ThreadPoolExecutor(maxRunning, 10, 60L, TimeUnit.SECONDS,
                        new LinkedBlockingDeque<Runnable>(), new ThreadPoolExecutor.DiscardOldestPolicy());
            }
            return new VanGogh(this, new MemoryCache((int) memoryCacheSize), diskCache);
        }
    }
}
