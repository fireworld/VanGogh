package cc.colorcat.vangogh;

import android.graphics.Bitmap;

import java.io.IOException;

/**
 * Created by cxx on 2017/8/8.
 * xx.ch@outlook.com
 */
class TransformInterceptor implements Interceptor {

    TransformInterceptor() {
    }

    @Override
    public Result intercept(Chain chain) throws IOException {
        Task task = chain.task();
        Result result = chain.proceed(task);
        Bitmap bitmap = Utils.transformResult(result.bitmap(), task.options(), task.transformations());
        return new Result(bitmap, result.from());
    }
}
