package cc.colorcat.vangogh;

import android.graphics.Bitmap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by cxx on 2017/8/8.
 * xx.ch@outlook.com
 */
class TransformInterceptor implements Interceptor {
    private List<Transformation> transformations;

    TransformInterceptor(List<Transformation> transformations) {
        this.transformations = new ArrayList<>(transformations);
    }

    @Override
    public Result intercept(Chain chain) throws IOException {
        Task task = chain.task();
        Result result = chain.proceed(task);
        List<Transformation> trans = new LinkedList<>(transformations);
        trans.addAll(task.transformations());
        Bitmap bitmap = Utils.transformResult(result.bitmap(), task.options(), trans);
        return new Result(bitmap, result.from());
    }
}
