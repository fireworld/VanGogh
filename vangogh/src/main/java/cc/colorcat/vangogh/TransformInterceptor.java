package cc.colorcat.vangogh;

import android.graphics.Bitmap;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by cxx on 2017/8/8.
 * xx.ch@outlook.com
 */

class TransformInterceptor implements Interceptor {
    private List<Transformation> transformations = new LinkedList<>();

    TransformInterceptor(List<Transformation> transformations) {
        this.transformations.addAll(transformations);
    }

    @Override
    public Result intercept(Chain chain) throws IOException {
        Task task = chain.task();
        Result result = chain.proceed(task);
        Bitmap bitmap = result.bitmap();
        Task.Options options = task.options();
        if (options.hasSize() || options.hasRotation()) {
            bitmap = Utils.transformResult(result.bitmap(), options);
        }
        transformations.addAll(task.transformations());
        for (Transformation transformation : transformations) {
            bitmap = transformation.transform(bitmap);
        }
        return new Result(bitmap, result.from());
    }
}
