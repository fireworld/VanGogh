package cc.colorcat.vangoghdemo.api;

import com.google.gson.reflect.TypeToken;

import java.util.List;

import cc.colorcat.netbird4.MRequest;
import cc.colorcat.netbird4.Method;
import cc.colorcat.vangoghdemo.entity.Course;
import cc.colorcat.vangoghdemo.internal.Result;
import cc.colorcat.vangoghdemo.internal.ResultParser;

/**
 * Created by cxx on 17-11-22.
 * xx.ch@outlook.com
 */
public class CoursesImpl extends BaseImpl<List<Course>> implements Api.Courses {
    private int type;
    private int number;

    @Override
    public Api.Courses setType(int type) {
        this.type = type;
        return this;
    }

    @Override
    public Api.Courses setNumber(int number) {
        this.number = number;
        return this;
    }

    @Override
    protected MRequest.Builder<List<Course>> builder() {
        TypeToken<Result<List<Course>>> token = new TypeToken<Result<List<Course>>>() {
        };
        return new MRequest.Builder<>(ResultParser.create(token))
                .path(Api.Courses.PATH)
                .method(Method.GET)
                .add("type", Integer.toString(type))
                .add("num", Integer.toString(number));
    }
}
