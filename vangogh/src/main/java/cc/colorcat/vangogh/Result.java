package cc.colorcat.vangogh;

import android.graphics.Bitmap;

import java.io.InputStream;

/**
 * Created by cxx on 2017/7/10.
 * xx.ch@outlook.com
 */

public final class Result {
    private final Bitmap bitmap;
    private final InputStream stream;
    private final long contentLength;
    private final LoadedFrom from;

    public Result(Bitmap bitmap, LoadedFrom from) {
        if (bitmap == null) {
            throw new NullPointerException("bitmap == null");
        }
        if (from == null) {
            throw new NullPointerException("from == null");
        }
        this.bitmap = bitmap;
        this.stream = null;
        this.contentLength = bitmap.getByteCount();
        this.from = from;
    }

    public Result(InputStream is, long contentLength, LoadedFrom from) {
        if (is == null) {
            throw new NullPointerException("is == null");
        }
        if (from == null) {
            throw new NullPointerException("from == null");
        }
        this.bitmap = null;
        this.stream = is;
        this.contentLength = contentLength;
        this.from = from;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public InputStream getStream() {
        return stream;
    }

    public long getContentLength() {
        return contentLength;
    }

    public LoadedFrom getFrom() {
        return from;
    }
}
