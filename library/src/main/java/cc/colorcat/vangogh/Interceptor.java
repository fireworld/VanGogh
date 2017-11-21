package cc.colorcat.vangogh;

import java.io.IOException;

/**
 * Created by cxx on 2017/7/11.
 * xx.ch@outlook.com
 */
public interface Interceptor {
    Result intercept(Chain chain) throws IOException;

    interface Chain {
        Downloader loader();

        Task task();

        Result proceed(Task task) throws IOException;
    }
}
