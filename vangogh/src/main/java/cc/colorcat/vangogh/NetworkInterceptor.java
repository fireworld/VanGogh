package cc.colorcat.vangogh;

import java.io.IOException;

/**
 * Created by cxx on 2017/7/11.
 * xx.ch@outlook.com
 */
class NetworkInterceptor implements Interceptor {
    private VanGogh vanGogh;

    NetworkInterceptor(VanGogh vanGogh) {
        this.vanGogh = vanGogh;
    }

    @Override
    public Result intercept(Chain chain) throws IOException {
        Task task = chain.task();
        int fromPolicy = task.fromPolicy() & From.NETWORK.policy;
        if (fromPolicy != 0) {
            Downloader downloader = chain.loader();
            Result result = downloader.load(vanGogh, task);
            if (result != null) {
                return result;
            }
        }
        return chain.proceed(task);
    }
}
