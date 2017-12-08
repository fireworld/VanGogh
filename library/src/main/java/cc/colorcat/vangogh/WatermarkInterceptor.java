package cc.colorcat.vangogh;

import android.graphics.Bitmap;

import java.io.IOException;

/**
 * Created by cxx on 2017/7/12.
 * xx.ch@outlook.com
 */
class WatermarkInterceptor implements Interceptor {

    @Override
    public Result intercept(Chain chain) throws IOException {
        Task task = chain.task();
        Result result = chain.proceed(task);
        From resultFrom = result.from();
        Bitmap bitmap = Utils.makeWatermark(result.bitmap(), resultFrom.debugColor, task.options());
        return new Result(bitmap, resultFrom);
    }
}
