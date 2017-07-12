package cc.colorcat.vangogh;

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
        LoadedFrom reqFrom = task.reqFrom();
        if (reqFrom == LoadedFrom.ANY || reqFrom == LoadedFrom.DISK) {
            DiskCache.Snapshot snapshot = diskCache.getSnapshot(task.stableKey());
            InputStream is = snapshot.getInputStream();
            long length = snapshot.getContentLength();
            if (is != null && length > 0L) {
                return new Result(is, length, LoadedFrom.DISK);
            }
        }

        Result result = chain.proceed(task);
        LoadedFrom resultFrom = result.from();
        if (resultFrom == LoadedFrom.NETWORK) {
            DiskCache.Snapshot snapshot = diskCache.getSnapshot(task.stableKey());
            OutputStream os = snapshot.getOutputStream();
            if (os != null) {
                InputStream is = result.stream();
                if (Utils.dumpAndCloseQuietly(is, os)) {
                    is = snapshot.getInputStream();
                    long contentLength = snapshot.getContentLength();
                    if (is != null && contentLength > 0L) {
                        return new Result(is, contentLength, LoadedFrom.NETWORK);
                    }
                    throw new IOException("DiskCache reporting error");
                }
            }
        }
        return result;
    }
}
