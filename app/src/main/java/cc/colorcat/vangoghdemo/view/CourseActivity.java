package cc.colorcat.vangoghdemo.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import cc.colorcat.vangogh.CircleTransformation;
import cc.colorcat.vangogh.SquareTransformation;
import cc.colorcat.vangogh.Transformation;
import cc.colorcat.vangogh.VanGogh;
import cc.colorcat.vangoghdemo.R;
import cc.colorcat.vangoghdemo.contract.ICourses;
import cc.colorcat.vangoghdemo.entity.Course;
import cc.colorcat.vangoghdemo.internal.VanGoghScrollListener;
import cc.colorcat.vangoghdemo.presenter.CoursePresenter;
import cc.colorcat.vangoghdemo.widget.RvAdapter;
import cc.colorcat.vangoghdemo.widget.RvHolder;
import cc.colorcat.vangoghdemo.widget.SimpleRvAdapter;

/**
 * Created by cxx on 17-11-22.
 * xx.ch@outlook.com
 */
public class CourseActivity extends BaseActivity implements ICourses.View {
    private ICourses.Presenter mPresenter = new CoursePresenter();
    private SwipeRefreshLayout mRefreshLayout;
    private List<Course> mCourses = new ArrayList<>(30);
    private RvAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);

        RecyclerView recyclerView = findViewById(R.id.rv_courses);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addOnScrollListener(VanGoghScrollListener.get());
        mAdapter = new SimpleRvAdapter<Course>(mCourses, R.layout.item_course) {
            @Override
            public void bindView(RvHolder holder, Course data) {
                RvHolder.Helper helper = holder.getHelper();
                ImageView icon = helper.getView(R.id.iv_icon);
                Transformation trans = (helper.getPosition() & 1) == 0 ?
                        new CircleTransformation() : new SquareTransformation();
                VanGogh.with(CourseActivity.this)
                        .load(data.getPicSmallUrl())
                        .addTransformation(trans)
                        .into(icon);
                helper.setText(R.id.tv_name, data.getName())
                        .setText(R.id.tv_description, data.getDescription());
            }
        };
        recyclerView.setAdapter(mAdapter);

        mRefreshLayout = findViewById(R.id.srl_root);
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.toRefreshCourses();
            }
        });
        mPresenter.onCreate(this);
    }

    @Override
    protected void onDestroy() {
        mPresenter.onDestroy();
        super.onDestroy();
    }

    @Override
    public void refreshCourses(List<Course> courses) {
        mCourses.clear();
        mCourses.addAll(courses);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void stopRefresh() {
        mRefreshLayout.setRefreshing(false);
    }
}
