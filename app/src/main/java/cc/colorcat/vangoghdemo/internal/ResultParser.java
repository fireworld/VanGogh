package cc.colorcat.vangoghdemo.internal;

import android.support.annotation.NonNull;

import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;

import cc.colorcat.netbird3.NetworkData;
import cc.colorcat.netbird3.Parser;
import cc.colorcat.netbird3.Response;
import cc.colorcat.netbird3.StateIOException;

/**
 * Created by cxx on 17-11-22.
 * xx.ch@outlook.com
 */
public class ResultParser<T> implements Parser<T> {

    private TypeToken<Result<T>> token;

    public static <T> ResultParser<T> create(TypeToken<Result<T>> token) {
        if (token == null) {
            throw new NullPointerException("token == null");
        }
        return new ResultParser<>(token);
    }

    private ResultParser(TypeToken<Result<T>> token) {
        this.token = token;
    }

    @NonNull
    @Override
    public NetworkData<? extends T> parse(@NonNull Response response) throws IOException {
        try {
            String content = response.body().string();
            Result<T> result = Utils.fromJson(content, token);
            int code = result.getStatus();
            T data = result.getData();
            if (code == Result.STATUS_OK && data != null) {
                return NetworkData.newSuccess(result.getData());
            }
            return NetworkData.newFailure(code, result.getMsg());
        } catch (JsonParseException e) {
            throw new StateIOException(response.msg(), e, response.code());
        }
    }
}
