package cc.colorcat.vangogh;

import java.io.IOException;
import java.util.List;

/**
 * Created by cxx on 2017/7/11.
 * xx.ch@outlook.com
 */
class RealInterceptorChain implements Interceptor.Chain {
    private final List<Interceptor> interceptors;
    private final int index;
    private final Task task;
    private final Downloader downloader;

    RealInterceptorChain(List<Interceptor> interceptors, int index, Task task, Downloader downloader) {
        this.interceptors = interceptors;
        this.index = index;
        this.task = task;
        this.downloader = downloader;
    }

    @Override
    public Downloader loader() {
        return downloader;
    }

    @Override
    public Task task() {
        return task;
    }

    @Override
    public Result proceed(Task task) throws IOException {
        RealInterceptorChain next = new RealInterceptorChain(interceptors, index + 1, task, downloader);
        Interceptor interceptor = interceptors.get(index);
        return interceptor.intercept(next);
    }
}
