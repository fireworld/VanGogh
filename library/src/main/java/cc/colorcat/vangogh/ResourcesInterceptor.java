package cc.colorcat.vangogh;

import android.content.res.Resources;
import android.net.Uri;

import java.io.IOException;

/**
 * Created by cxx on 17-8-29.
 * xx.ch@outlook.com
 */
class ResourcesInterceptor implements Interceptor {
    private Resources resources;

    ResourcesInterceptor(Resources resources) {
        this.resources = resources;
    }

    @Override
    public Result intercept(Chain chain) throws IOException {
        Task task = chain.task();
        int fromPolicy = task.fromPolicy() & From.DISK.policy;
        if (fromPolicy != 0) {
            Uri uri = task.uri();
            if (Utils.SCHEME_VANGOGH.equals(uri.getScheme()) && Utils.HOST_RESOURCE.equals(uri.getHost())) {
                int resId = Integer.parseInt(uri.getQueryParameter("id"));
                return new Result(resources.openRawResource(resId), From.DISK);
            }
        }
        return chain.proceed(task);
    }
}
