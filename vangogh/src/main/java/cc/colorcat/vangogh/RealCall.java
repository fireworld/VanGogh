package cc.colorcat.vangogh;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by cxx on 2017/7/11.
 * xx.ch@outlook.com
 */
class RealCall implements Call {
    private final VanGogh vanGogh;
    private final Task task;

    RealCall(VanGogh vanGogh, Task task) {
        this.vanGogh = vanGogh;
        this.task = task;
    }

    @Override
    public Task task() {
        return task;
    }

    @Override
    public Result execute() throws IOException {
        return getResultWithInterceptor();
    }

    private Result getResultWithInterceptor() throws IOException {
        List<Interceptor> users = vanGogh.interceptors();
        List<Interceptor> interceptors = new ArrayList<>(users.size() + 3);
        interceptors.addAll(users);
        if (vanGogh.debug()) {
            interceptors.add(new WatermarkInterceptor());
        }
        interceptors.add(new MemoryCacheInterceptor(vanGogh.memoryCache()));
        interceptors.add(new StreamInterceptor());
        DiskCache cache = vanGogh.diskCache();
        if (cache != null) {
            interceptors.add(new DiskCacheInterceptor(cache));
        }
        interceptors.add(new NetworkInterceptor(vanGogh));
        Interceptor.Chain chain = new RealInterceptorChain(interceptors, 0, task, vanGogh.downloader().clone());
        return chain.proceed(task);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RealCall realCall = (RealCall) o;

        return task.equals(realCall.task);
    }

    @Override
    public int hashCode() {
        return 17 * task.hashCode();
    }

    @Override
    public String toString() {
        return "RealCall{" +
                "task=" + task +
                '}';
    }
}
