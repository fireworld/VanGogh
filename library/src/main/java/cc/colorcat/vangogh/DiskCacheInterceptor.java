package cc.colorcat.vangogh;

import android.graphics.Bitmap;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by cxx on 2017/7/11.
 * xx.ch@outlook.com
 */
class DiskCacheInterceptor implements Interceptor {
    private DiskCache diskCache;

    DiskCacheInterceptor(DiskCache cache) {
        this.diskCache = cache;
    }

    @Override
    public Result intercept(Chain chain) throws IOException {
        Task task = chain.task();
        Task.Options options = task.options();
        int fromPolicy = task.fromPolicy() & From.DISK.policy;
        if (fromPolicy != 0) {
            DiskCache.Snapshot snapshot = diskCache.getSnapshot(task.stableKey());
            Bitmap bitmap = decodeOrDelete(snapshot, options, false);
            if (bitmap != null) {
                return new Result(bitmap, From.DISK);
            }
        }

        Result result = chain.proceed(task);
        From resultFrom = result.from();
        if (resultFrom == From.NETWORK) {
            DiskCache.Snapshot snapshot = diskCache.getSnapshot(task.stableKey());
            OutputStream os = snapshot.getOutputStream();
            if (os != null) {
                InputStream is = result.stream();
                try {
                    Utils.dumpAndClose(is, os);
                    Bitmap bitmap = decodeOrDelete(snapshot, options, true);
                    result = new Result(bitmap, resultFrom);
                } catch (IOException e) {
                    snapshot.requireDelete();
                    throw e;
                }
            }
        }
        return result;
    }

    private static Bitmap decodeOrDelete(DiskCache.Snapshot snapshot, Task.Options ops, boolean canThrow) throws IOException {
        Bitmap result = null;
        InputStream is = snapshot.getInputStream();
        if (is != null) {
            if (ops.hasMaxSize()) {
                result = Utils.decodeStreamAndClose(is, ops);
            } else {
                result = Utils.decodeStreamAndClose(is);
            }
            if (result == null && !canThrow) {
                snapshot.requireDelete();
            }
        }
        if (result == null && canThrow) {
            throw new IOException("decode failed, snapshot = " + snapshot);
        }
        return result;
    }
}
