package cc.colorcat.vangoghdemo.api;

import java.io.IOException;
import java.util.List;

import cc.colorcat.netbird4.MRequest;
import cc.colorcat.vangoghdemo.entity.Course;

/**
 * Created by cxx on 17-11-22.
 * xx.ch@outlook.com
 */
public interface Api {

    interface Base<T> {
        Object send(MRequest.Listener<? super T> listener);

        T execute() throws IOException;

        void cancel();
    }


    interface Courses extends Base<List<Course>> {
        String PATH = "/api/teacher";

        Courses setType(int type);

        Courses setNumber(int number);
    }
}
