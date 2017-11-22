package cc.colorcat.vangoghdemo;

import android.app.Application;

import cc.colorcat.vangogh.VanGogh;
import cc.colorcat.vangoghdemo.internal.ApiService;


/**
 * Created by cxx on 2017/7/13.
 * xx.ch@outlook.com
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ApiService.init(this);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        VanGogh.with(this).clear();
        VanGogh.with(this).releaseMemory();
    }
}
