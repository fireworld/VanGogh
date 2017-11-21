package cc.colorcat.vangogh;

import android.net.Uri;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by cxx on 17-8-29.
 * xx.ch@outlook.com
 */
public class FileInterceptor implements Interceptor {

    @Override
    public Result intercept(Chain chain) throws IOException {
        Task task = chain.task();
        int fromPolicy = task.fromPolicy() & From.DISK.policy;
        Uri uri = task.uri();
        if (fromPolicy != 0 && "file".equals(uri.getScheme())) {
            File file = new File(uri.getPath());
            long length = file.length();
            return new Result(new FileInputStream(file), length, From.DISK);
        }
        return chain.proceed(task);
    }
}
