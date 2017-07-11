package cc.colorcat.vangogh;

import java.io.ByteArrayInputStream;
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
        if (diskCache == null) {
            Result result = chain.proceed(chain.task());
            InputStream is = result.getStream();
            LoadedFrom resultFrom = result.getFrom();
            if (is != null && result.getFrom() == LoadedFrom.NETWORK) {
                byte[] bytes = Utils.toBytes(is);
                is.close();
                return new Result(new ByteArrayInputStream(bytes), result.getContentLength(), resultFrom);
            }
            return result;
        }


        LoadedFrom from = task.from();
        if (from == LoadedFrom.NONE || from == LoadedFrom.DISK) {
            InputStream is = null;
            try {
                DiskCache.Snapshot snapshot = diskCache.getSnapshot(task.getKey());
                is = snapshot.getInputStream();
                long length = snapshot.getContentLength();
                if (is != null && length > 0L) {
                    return new Result(is, length, LoadedFrom.DISK);
                }
            } finally {
                Utils.close(is);
            }
        }
        Result result = chain.proceed(task);
        LoadedFrom resultFrom = result.getFrom();
        InputStream is = result.getStream();
        if (is != null && resultFrom == LoadedFrom.NETWORK) {
            byte[] bytes = Utils.toBytes(is);
            is.close();
            DiskCache.Snapshot snapshot = diskCache.getSnapshot(task.getKey());
            OutputStream os = null;
            try {
                os = snapshot.getOutputStream();
                if (os != null) {
                    Utils.justDump(new ByteArrayInputStream(bytes), os);
                }
            } finally {
                Utils.close(os);
            }
            return new Result(new ByteArrayInputStream(bytes), result.getContentLength(), resultFrom);
        }

        return chain.proceed(task);
    }
}
