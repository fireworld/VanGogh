package cc.colorcat.vangogh;

import java.io.IOException;

/**
 * Created by cxx on 2017/7/11.
 * xx.ch@outlook.com
 */
class NetworkInterceptor implements Interceptor {
    private static final String HTTP = "http";
    private static final String HTTPS = "https";

    NetworkInterceptor() {
    }

    @Override
    public Result intercept(Chain chain) throws IOException {
        Task task = chain.task();
        int fromPolicy = task.fromPolicy() & From.NETWORK.policy;
        String scheme = task.uri().getScheme();
        if (fromPolicy != 0 && (HTTP.equalsIgnoreCase(scheme) || HTTPS.equalsIgnoreCase(scheme))) {
            Downloader downloader = chain.loader();
            Result result = downloader.load(task);
            if (result != null) {
                return result;
            }
        }
        return chain.proceed(task);
    }
}
