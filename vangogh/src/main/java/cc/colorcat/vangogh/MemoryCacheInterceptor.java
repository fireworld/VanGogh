package cc.colorcat.vangogh;

import android.graphics.Bitmap;

import java.io.IOException;

/**
 * Created by cxx on 2017/7/11.
 * xx.ch@outlook.com
 */
class MemoryCacheInterceptor implements Interceptor {
    private Cache<Bitmap> memoryCache;

    MemoryCacheInterceptor(Cache<Bitmap> cache) {
        this.memoryCache = cache;
    }

    @Override
    public Result intercept(Chain chain) throws IOException {
        Task task = chain.task();
        LoadedFrom from = task.from();
        if (from == LoadedFrom.NONE || from == LoadedFrom.MEMORY) {
            Bitmap bitmap = memoryCache.get(task.getKey());
            if (bitmap != null) {
                return new Result(bitmap, LoadedFrom.MEMORY);
            }
        }
        return chain.proceed(task);
    }
}
