package cc.colorcat.vangoghdemo.api;

import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;

import cc.colorcat.netbird4.MRequest;
import cc.colorcat.netbird4.Parser;
import cc.colorcat.vangoghdemo.internal.ApiService;
import cc.colorcat.vangoghdemo.internal.Result;
import cc.colorcat.vangoghdemo.internal.ResultParser;

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
        tag = ApiService.send(request);
        return tag;
    }

    @Override
    public T execute() throws IOException {
        MRequest<T> request = builder()
                .build();
        tag = request.tag();
        return ApiService.execute(request);
    }

    protected abstract MRequest.Builder<T> builder();

    @SuppressWarnings("unchecked")
    protected MRequest.Builder<T> builderOf() {
        ParameterizedType pt = (ParameterizedType) this.getClass().getGenericSuperclass();
        TypeToken token = TypeToken.getParameterized(Result.class, pt.getActualTypeArguments());
        return new MRequest.Builder<T>(ResultParser.create(token));
    }

    protected MRequest.Builder<T> builderOf(Parser<T> parser) {
        return new MRequest.Builder<>(parser);
    }
}
