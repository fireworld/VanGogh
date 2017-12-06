package cc.colorcat.vangogh;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by cxx on 2017/7/11.
 * xx.ch@outlook.com
 */
class RealCall implements Call {
    private final VanGogh vanGogh;
    private final Task task;
    private AtomicInteger count = new AtomicInteger(0);

    RealCall(VanGogh vanGogh, Task task) {
        this.vanGogh = vanGogh;
        this.task = task;
    }

    public int getCount() {
        return count.get();
    }

    public int getAndIncrement() {
        return count.getAndIncrement();
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
        List<Interceptor> interceptors = new ArrayList<>(users.size() + 8);
        interceptors.addAll(users);
        if (vanGogh.debug()) {
            interceptors.add(new WatermarkInterceptor());
        }
        interceptors.add(new TransformInterceptor());
        interceptors.add(new MemoryCacheInterceptor(vanGogh.memoryCache()));
        interceptors.add(new StreamInterceptor());
        interceptors.add(new ResourcesInterceptor(vanGogh.resources()));
        interceptors.add(new FileInterceptor());
        DiskCache cache = vanGogh.diskCache();
        if (cache != null) {
            interceptors.add(new DiskCacheInterceptor(cache));
        }
        interceptors.add(new NetworkInterceptor(vanGogh));
        Interceptor.Chain chain = new RealInterceptorChain(interceptors, 0, task, vanGogh.downloader());
        return chain.proceed(task);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RealCall realCall = (RealCall) o;

        if (task.fromPolicy() != realCall.task.fromPolicy()) return false;
        return task.stableKey().equals(realCall.task.stableKey());
    }

    @Override
    public int hashCode() {
        int result = task.stableKey().hashCode();
        result = 31 * result + task.fromPolicy();
        return result;
    }

    @Override
    public String toString() {
        return "RealCall{" +
                "vanGogh=" + vanGogh +
                ", task=" + task +
                ", count=" + count +
                '}';
    }
}
