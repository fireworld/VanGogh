package cc.colorcat.vangogh;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Looper;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by cxx on 2017/7/6.
 * xx.ch@outlook.com
 */

public class Utils {

    public static boolean isEmpty(CharSequence txt) {
        return txt == null || txt.length() == 0;
    }

    public static boolean isHttpUrl(String url) {
        return url != null && url.toLowerCase().matches("^(http)(s)?://(\\S)+");
    }

    public static void checkMain() {
        if (Looper.getMainLooper().getThread() != Thread.currentThread()) {
            throw new IllegalStateException("Method call should not happen reqFrom the main thread.");
        }
    }

    public static boolean isMain() {
        return Looper.getMainLooper().getThread() == Thread.currentThread();
    }

    public static Bitmap makeWatermark(Bitmap src, @ColorInt int color) {
        int width = src.getWidth();
        int height = src.getHeight();
        int size = Math.min(width / 4, height / 4);
        if (size < 2) return src;
        Bitmap result = Bitmap.createBitmap(width, height, src.getConfig());
        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(src, 0F, 0F, null);
        Paint paint = new Paint();
        paint.setColor(color);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        int r = size >> 1;
        canvas.drawCircle(r, r, r, paint);
        canvas.save();
        return result;
    }

    public static File getCacheDirectory(Context context) {
        File dir = context.getExternalCacheDir();
        if (dir == null) {
            dir = context.getCacheDir();
        }
        return dir;
    }

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

    static boolean dumpAndCloseQuietly(InputStream is, OutputStream os) {
        BufferedInputStream bis = new BufferedInputStream(is);
        BufferedOutputStream bos = new BufferedOutputStream(os);
        try {
            byte[] buffer = new byte[4096];
            for (int length = bis.read(buffer); length != -1; length = bis.read(buffer)) {
                bos.write(buffer, 0, length);
            }
            bos.flush();
            return true;
        } catch (IOException e) {
            return false;
        } finally {
            close(bis);
            close(bos);
        }
    }

    public static void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException ignore) {
            }
        }
    }

    public static void deleteContents(File dir) throws IOException {
        File[] files = dir.listFiles();
        if (files == null) throw new IOException("not a readable directory: " + dir);
        for (File file : files) {
            if (file.isDirectory()) {
                deleteContents(file);
            }
            if (!file.delete()) {
                throw new IOException("failed to delete file: " + file);
            }
        }
    }

    public static void deleteIfExists(File... files) throws IOException {
        for (File file : files) {
            deleteIfExists(file);
        }
    }

    public static void deleteIfExists(File file) throws IOException {
        if (file.exists() && !file.delete()) {
            throw new IOException("failed to delete " + file);
        }
    }

    public static void renameTo(File from, File to, boolean deleteDest) throws IOException {
        if (deleteDest) {
            deleteIfExists(to);
        }
        if (!from.renameTo(to)) {
            throw new IOException("failed to rename");
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T getService(Context ctx, String service) {
        return (T) ctx.getSystemService(service);
    }


    /**
     * md5 加密，如果加密失败则原样返回
     */
    static String md5(String resource) {
        String result = resource;
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.reset();
            digest.update(resource.getBytes());
            byte[] bytes = digest.digest();
            int len = bytes.length << 1;
            StringBuilder sb = new StringBuilder(len);
            for (byte b : bytes) {
                sb.append(Character.forDigit((b & 0xf0) >> 4, 16));
                sb.append(Character.forDigit(b & 0x0f, 16));
            }
            result = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            LogUtils.e(e);
        }
        return result;
    }

    public static Bitmap decodeStream(InputStream is) {
        return BitmapFactory.decodeStream(is);
    }

    public static byte[] toBytes(InputStream is) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        for (int length = is.read(buffer); length != -1; length = is.read(buffer)) {
            os.write(buffer, 0, length);
        }
        os.flush();
        return os.toByteArray();
    }

    public static Bitmap decodeStream(@NonNull InputStream is, int reqWidth, int reqHeight, Bitmap.Config config) throws IOException {
        try {
            is.mark(is.available());
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            if (config != null) options.inPreferredConfig = config;
            BitmapFactory.decodeStream(is, null, options);
            is.reset();
            if (!options.mCancel && options.outWidth != -1 && options.outHeight != -1) {
                options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
                options.inJustDecodeBounds = false;
                return BitmapFactory.decodeStream(is, null, options);
            }
            return BitmapFactory.decodeStream(is);
        } finally {
            close(is);
        }
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    private Utils() {
        throw new AssertionError("no instance");
    }
}
