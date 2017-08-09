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
        VanGogh vanGogh = new VanGogh.Builder(this)
                .debug(BuildConfig.DEBUG)
                .enableLog(true)
                .defaultLoading(R.mipmap.ic_launcher_round)
                .defaultError(R.mipmap.ic_launcher)
//                .fade(false)
//                .maxRunning(1)
                .build();
        VanGogh.setSingleton(vanGogh);
    }
}
