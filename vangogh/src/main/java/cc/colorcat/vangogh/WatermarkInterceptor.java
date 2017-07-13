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
        Result result = chain.proceed(chain.task());
        From resultFrom = result.from();
        Bitmap bitmap = Utils.makeWatermark(result.bitmap(), resultFrom.debugColor);
        return new Result(bitmap, resultFrom);
    }
}
