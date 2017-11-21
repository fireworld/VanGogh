package cc.colorcat.vangogh;

import android.graphics.Bitmap;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by cxx on 2017/7/12.
 * xx.ch@outlook.com
 */

class StreamInterceptor implements Interceptor {

    @Override
    public Result intercept(Chain chain) throws IOException {
        Task task = chain.task();
        Result result = chain.proceed(task);
        Bitmap bitmap = result.bitmap();
        if (bitmap == null) {
            InputStream is = result.stream();
            Task.Options options = task.options();
            if (options.hasMaxSize()) {
                bitmap = Utils.decodeStreamAndClose(is, options);
            } else {
                bitmap = Utils.decodeStreamAndClose(is);
            }
            if (bitmap == null) {
                throw new IOException("decode failed, uri = " + task.uri());
            }
        }
        return new Result(bitmap, result.from());
    }
}
