package cc.colorcat.vangoghdemo;

import android.app.Application;

import cc.colorcat.vangogh.VanGogh;


/**
 * Created by cxx on 2017/7/13.
 * xx.ch@outlook.com
 */

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        VanGogh.setSingleton(new VanGogh.Builder(this).debug(BuildConfig.DEBUG).enableLog(true).build());
    }
}
