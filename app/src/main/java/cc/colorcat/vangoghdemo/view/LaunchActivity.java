package cc.colorcat.vangoghdemo.view;

import android.os.Bundle;
import android.view.View;

import cc.colorcat.vangoghdemo.R;

/**
 * Created by cxx on 15/12/13.
 * xx.ch@outlook.com
 */
public class LaunchActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        findViewById(R.id.btn_show_courses).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateTo(CourseActivity.class);
            }
        });
    }
}
