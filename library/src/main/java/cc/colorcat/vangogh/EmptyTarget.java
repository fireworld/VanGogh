package cc.colorcat.vangogh;

import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;

/**
 * Created by cxx on 2017/7/11.
 * xx.ch@outlook.com
 */
class EmptyTarget implements Target {
    final static EmptyTarget EMPTY = new EmptyTarget();

    private EmptyTarget() {

    }

    @Override
    public void onPrepare(@Nullable Drawable placeHolder) {

    }

    @Override
    public void onLoaded(Drawable drawable, From from) {

    }

    @Override
    public void onFailed(@Nullable Drawable error, Exception cause) {

    }
}
