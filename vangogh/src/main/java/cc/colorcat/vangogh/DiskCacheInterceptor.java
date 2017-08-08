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
        int fromPolicy = task.fromPolicy() & From.DISK.policy;
        if (fromPolicy != 0) {
            DiskCache.Snapshot snapshot = diskCache.getSnapshot(task.stableKey());
            InputStream is = snapshot.getInputStream();
            long length = snapshot.getContentLength();
            if (is != null && length > 0L) {
                return new Result(is, length, From.DISK);
            }
        }

        Result result = chain.proceed(task);
        From resultFrom = result.from();
        if (resultFrom == From.NETWORK) {
            DiskCache.Snapshot snapshot = diskCache.getSnapshot(task.stableKey());
            OutputStream os = snapshot.getOutputStream();
            if (os != null) {
                InputStream is = result.stream();
                Utils.dumpAndClose(is, os);
                is = snapshot.getInputStream();
                long contentLength = snapshot.getContentLength();
                if (is != null && contentLength > 0L) {
                    return new Result(is, contentLength, resultFrom);
                }
                throw new IOException("DiskCache reporting error");
            }
        }
        return result;
    }
}
