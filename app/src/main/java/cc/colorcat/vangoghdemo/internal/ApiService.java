package cc.colorcat.vangoghdemo.internal;

import android.content.Context;

import java.io.File;
import java.io.IOException;

import cc.colorcat.netbird3.MRequest;
import cc.colorcat.netbird3.NetBird;
import cc.colorcat.netbird3.android.AndroidLogger;
import cc.colorcat.netbird3.logging.Filter;
import cc.colorcat.netbird3.logging.LoggingTailInterceptor;
import cc.colorcat.vangogh.Task;
import cc.colorcat.vangogh.VanGogh;
import cc.colorcat.vangoghdemo.BuildConfig;
import cc.colorcat.vangoghdemo.R;

/**
 * Created by cxx on 2017/8/9.
 * xx.ch@outlook.com
 */
public final class ApiService {
    private static final int TIME_OUT_CONNECT = 10000;
    private static final int TIME_OUT_READ = 10000;

    private static NetBird netBird;

    public static void init(Context context) {
        final boolean debug = BuildConfig.DEBUG;

        NetBird.Builder nBuilder = new NetBird.Builder(BuildConfig.BASE_URL)
                .connectTimeOut(TIME_OUT_CONNECT)
                .readTimeOut(TIME_OUT_READ)
                .enableGzip(true)
                .enableExceptionLog(debug);
        if (debug) {
            nBuilder.addTailInterceptor(new LoggingTailInterceptor(new AndroidLogger(), new LoggingFilter()));
        }
        netBird = nBuilder.build();

        int maxImageSize = Utils.getScreenWidth(context) >> 1;
        Task.Options global = new Task.Options();
        global.maxSize(maxImageSize, maxImageSize);
        VanGogh.Builder vBuilder = new VanGogh.Builder(context)
                .memoryCacheSize(Utils.calculateMemoryCacheSize(context))
                .defaultOptions(global)
                .defaultLoading(R.mipmap.ic_launcher_round)
                .defaultError(R.mipmap.ic_launcher)
//                .debug(debug)
                .enableLog(debug);
        File imageCache = new File(context.getCacheDir(), "ImageCache");
        if (imageCache.exists() || imageCache.mkdirs()) {
            long maxDiskSize = (long) Math.min(50 * 1024 * 1024, imageCache.getUsableSpace() * 0.1);
            vBuilder.diskCache(imageCache).diskCacheSize(maxDiskSize);
        }
        VanGogh.setSingleton(vBuilder.build());
    }

    public static Object send(MRequest<?> request) {
        return netBird.send(request);
    }

    public static <T> T execute(MRequest<T> request) throws IOException {
        return netBird.execute(request);
    }

    public static void cancelWaiting(Object tag) {
        netBird.cancelWaiting(tag);
    }

    private ApiService() {
        throw new AssertionError("no instance");
    }

    private static class LoggingFilter implements Filter {
        @Override
        public boolean filter(String contentType) {
            String s = contentType.toLowerCase();
            return s.contains("json") || s.contains("text");
        }
    }
}
