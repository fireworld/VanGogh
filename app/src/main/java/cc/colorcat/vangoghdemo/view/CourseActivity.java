package cc.colorcat.vangoghdemo.view;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import cc.colorcat.vangogh.CornerTransformation;
import cc.colorcat.vangogh.Transformation;
import cc.colorcat.vangogh.VanGogh;
import cc.colorcat.vangoghdemo.R;
import cc.colorcat.vangoghdemo.contract.ICourses;
import cc.colorcat.vangoghdemo.entity.Course;
import cc.colorcat.vangoghdemo.internal.VanGoghScrollListener;
import cc.colorcat.vangoghdemo.presenter.CoursePresenter;
import cc.colorcat.vangoghdemo.widget.ChoiceRvAdapter;
import cc.colorcat.vangoghdemo.widget.RvHolder;
import cc.colorcat.vangoghdemo.widget.SimpleAutoChoiceRvAdapter;
import cc.colorcat.vangoghdemo.widget.Tip;

/**
 * Created by cxx on 17-11-22.
 * xx.ch@outlook.com
 */
public class CourseActivity extends BaseActivity implements ICourses.View, Tip.Listener {
    private static final String TAG = CourseActivity.class.getSimpleName();

    private ICourses.Presenter mPresenter = new CoursePresenter();
    private SwipeRefreshLayout mRefreshLayout;
    private List<Course> mCourses = new ArrayList<>(30);
    private ChoiceRvAdapter mAdapter;
    private int[] mUnselectable = {};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);

        RecyclerView recyclerView = findViewById(R.id.rv_courses);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addOnScrollListener(VanGoghScrollListener.get());
        recyclerView.setAdapter(createAdapter());

        mRefreshLayout = findViewById(R.id.srl_root);
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.toRefreshCourses();
            }
        });
        mPresenter.onCreate(this);
    }

    private ChoiceRvAdapter createAdapter() {
        mAdapter = new SimpleAutoChoiceRvAdapter<Course>(mCourses, R.layout.item_course) {
            private final Transformation TL_BR = CornerTransformation.create(
                    CornerTransformation.TYPE_TL | CornerTransformation.TYPE_BR,
                    16F,
                    Color.RED
            );
            private final Transformation BL_TR = CornerTransformation.create(
                    CornerTransformation.TYPE_BL | CornerTransformation.TYPE_TR,
                    16F,
                    Color.YELLOW
            );

            @Override
            public void bindView(RvHolder holder, Course data) {
                RvHolder.Helper helper = holder.getHelper();
                ImageView icon = helper.getView(R.id.iv_icon);
                Transformation trans = (helper.getPosition() & 1) == 0 ? TL_BR : BL_TR;
                VanGogh.with(CourseActivity.this)
                        .load(data.getPicBigUrl())
                        .addTransformation(trans)
                        .into(icon);
                helper.setText(R.id.tv_serial_number, String.valueOf(helper.getPosition()))
                        .setText(R.id.tv_name, data.getName())
                        .setText(R.id.tv_description, data.getDescription());
            }
        };
        mAdapter.setOnItemSelectedChangedListener(new ChoiceRvAdapter.OnItemSelectedChangedListener() {
            @Override
            public void onItemSelectedChanged(int position, boolean selected) {
                if (selected) {
                    Log.i(TAG, "onItemSelectedChanged, selected  position = " + position);
                } else {
                    Log.d(TAG, "onItemSelectedChanged, unselected  position = " + position);
                }
            }
        });
        return mAdapter;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.choice_test, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.single:
                mAdapter.setChoiceMode(ChoiceRvAdapter.ChoiceMode.SINGLE);
                mAdapter.notifyDataSetChanged();
                return true;
            case R.id.multi:
                mAdapter.setChoiceMode(ChoiceRvAdapter.ChoiceMode.MULTIPLE);
                mAdapter.notifyDataSetChanged();
                return true;
            case R.id.none:
                mAdapter.setChoiceMode(ChoiceRvAdapter.ChoiceMode.NONE);
                mAdapter.notifyDataSetChanged();
                return true;
            case R.id.select_first:
                mAdapter.setSelection(0);
                return true;
            case R.id.move:
                if (mCourses.size() >= 5) {
                    List<Course> moved = new ArrayList<>(mCourses.subList(1, 4));
                    mCourses.removeAll(moved);
                    mAdapter.notifyItemRangeRemoved(1, 3);
                    mCourses.addAll(2, moved);
                    mAdapter.notifyItemRangeInserted(2, 3);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onDestroy() {
        mPresenter.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onTipClick() {
        mPresenter.toRefreshCourses();
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

    @Override
    public void setSelected(int[] positions) {
        if (positions.length != 0) {
            if (mAdapter.getChoiceMode() == ChoiceRvAdapter.ChoiceMode.SINGLE) {
                mAdapter.setSelection(positions[0]);
            } else {
                for (int position : positions) {
                    mAdapter.setSelection(position);
                }
            }
        }
    }

    @Override
    public void setUnselectable(int[] positions) {

    }
}
