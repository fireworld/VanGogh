package cc.colorcat.vangoghdemo.api;

import java.io.IOException;

import cc.colorcat.netbird3.MRequest;
import cc.colorcat.vangoghdemo.internal.ApiService;

/**
 * Created by cxx on 17-5-18.
 * xx.ch@outlook.com
 */
public abstract class BaseImpl<T> implements Api.Base<T> {
    private Object tag;

    @Override
    public void cancel() {
        ApiService.cancelWaiting(tag);
    }

    @Override
    public Object send(MRequest.Listener<? super T> listener) {
        MRequest<T> request = builder()
                .listener(listener)
                .build();
        tag = request.tag();
        return ApiService.send(request);
    }

    @Override
    public T execute() throws IOException {
        MRequest<T> request = builder()
                .build();
        tag = request.tag();
        return ApiService.execute(request);
    }

    protected abstract MRequest.Builder<T> builder();
}
