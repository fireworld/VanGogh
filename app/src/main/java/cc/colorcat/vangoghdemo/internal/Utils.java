package cc.colorcat.vangoghdemo.internal;


import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.WindowManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

/**
 * Created by cxx on 15/12/1.
 * xx.ch@outlook.com
 */
public final class Utils {
    private static final Gson GSON;

    static {
        GSON = new GsonBuilder()
                .serializeNulls()
                .create();
    }

    public static <T> T fromJson(String json, TypeToken<T> token) {
        return GSON.fromJson(json, token.getType());
    }

    public static int calculateMemoryCacheSize(Context ctx) {
        ActivityManager am = getService(ctx, Context.ACTIVITY_SERVICE);
        int memoryClass = am.getMemoryClass();
        return Math.min(1024 * 1024 * memoryClass / 7, 1024 * 1024 * 20);
    }

    public static int getScreenWidth(Context ctx) {
        Point size = getScreenSize(ctx);
        return Math.min(size.x, size.y);
    }

    public static Point getScreenSize(Context ctx) {
        WindowManager wm = getService(ctx, Context.WINDOW_SERVICE);
        Point size = new Point();
        wm.getDefaultDisplay().getSize(size);
        return size;
    }

    public static boolean hasAvailableNetwork(Context ctx) {
        ConnectivityManager cm = getService(ctx, Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        return info != null && info.isConnectedOrConnecting();
    }

    @SuppressWarnings("unchecked")
    private static <T> T getService(Context ctx, String service) {
        return (T) ctx.getSystemService(service);
    }

    private Utils() {
        throw new AssertionError("no instance");
    }
}
