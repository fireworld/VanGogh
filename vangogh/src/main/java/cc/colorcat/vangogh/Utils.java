package cc.colorcat.vangogh;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by cxx on 2017/7/6.
 * xx.ch@outlook.com
 */

public class Utils {

    public static int calculateMemoryCacheSize(Context ctx) {
        ActivityManager am = getService(ctx, Context.ACTIVITY_SERVICE);
        int memoryClass = am.getMemoryClass();
        return 1024 * 1024 * memoryClass / 7;
    }

    public static int sizeOf(Bitmap bitmap) {
        return bitmap.getByteCount();
    }

    public static long sizeOf(File file) {
        return file.length();
    }

    public static void justDump(InputStream is, OutputStream os) throws IOException {
        BufferedInputStream bis = new BufferedInputStream(is);
        BufferedOutputStream bos = new BufferedOutputStream(os);
        byte[] buffer = new byte[4096];
        for (int length = bis.read(buffer); length != -1; length = bis.read(buffer)) {
            bos.write(buffer, 0, length);
        }
        bos.flush();
    }

    public static void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException ignore) {

            }
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T getService(Context ctx, String service) {
        return (T) ctx.getSystemService(service);
    }

    private Utils() {
        throw new AssertionError("no instance");
    }
}
