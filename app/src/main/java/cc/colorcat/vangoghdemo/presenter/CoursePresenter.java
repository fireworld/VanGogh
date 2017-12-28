package cc.colorcat.vangoghdemo.presenter;

import java.util.List;

import cc.colorcat.vangoghdemo.api.Api;
import cc.colorcat.vangoghdemo.api.CoursesImpl;
import cc.colorcat.vangoghdemo.contract.ICourses;
import cc.colorcat.vangoghdemo.entity.Course;
import cc.colorcat.vangoghdemo.internal.WeakListener;

/**
 * Created by cxx on 17-11-22.
 * xx.ch@outlook.com
 */
public class CoursePresenter extends BasePresenter<ICourses.View> implements ICourses.Presenter {
    private Api.Courses mService = new CoursesImpl();
    private ICourses.View mView;

    @Override
    public void onCreate(ICourses.View view) {
        mView = view;
        doLoadCourses();
    }

    @Override
    public void onDestroy() {
        mView = null;
        super.onDestroy();
    }

    @Override
    public void doLoadCourses() {
        realLoadCourses();
    }

    @Override
    public void toRefreshCourses() {
        realLoadCourses();
    }

    private void realLoadCourses() {
        mService.setType(4).setNumber(30).send(new WeakListener<List<Course>, ICourses.View>(mView) {
            @Override
            public void onSuccess(ICourses.View view, List<Course> data) {
                view.refreshCourses(data);
            }

            @Override
            public void onFinish(ICourses.View view) {
                super.onFinish(view);
                view.stopRefresh();
            }
        });
    }
}
