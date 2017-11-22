package cc.colorcat.vangoghdemo.contract;

import java.util.List;

import cc.colorcat.vangoghdemo.entity.Course;

/**
 * Created by cxx on 17-11-22.
 * xx.ch@outlook.com
 */
public interface ICourses {

    interface View extends IBase.View {

        void refreshCourses(List<Course> courses);

        void stopRefresh();
    }

    interface Presenter extends IBase.Presenter<View> {

        void doLoadCourses();

        void toRefreshCourses();
    }
}
