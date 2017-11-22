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

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

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

    public static String readStream(String httpUrl, String charsetName) throws IOException {
        InputStream is = new URL(httpUrl).openStream();
        InputStreamReader reader = new InputStreamReader(is, charsetName);
        BufferedReader br = new BufferedReader(reader);
        StringBuilder builder = new StringBuilder();
        for (String line = br.readLine(); line != null; line = br.readLine()) {
            builder.append(line);
        }
        is.close();
        reader.close();
        br.close();
        return builder.toString();
    }

    public static <T> T fromJson(String json, TypeToken<T> token) {
        return GSON.fromJson(json, token.getType());
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        return GSON.fromJson(json, clazz);
    }

    public static String getString(String httpUrl) throws IOException {
        BufferedReader reader = null;
        try {
            InputStream is = new URL(httpUrl).openStream();
            reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();
            char[] buffer = new char[1024];
            for (int length = reader.read(buffer); length != -1; length = reader.read(buffer)) {
                sb.append(buffer, 0, length);
            }
            return sb.toString();
        } finally {
            close(reader);
        }
    }

    private static void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException ignore) {
            }
        }
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
