package cc.colorcat.vangogh;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.View;

/**
 * Created by cxx on 2017/7/11.
 * xx.ch@outlook.com
 */

class EmptyTarget implements Target {
    public final static EmptyTarget EMPTY = new EmptyTarget();

    private EmptyTarget() {

    }

    @Override
    public View getView() {
        return null;
    }

    @Override
    public void onStart(Drawable placeHolder) {

    }

    @Override
    public void onSuccess(Bitmap bitmap, LoadedFrom from) {

    }

    @Override
    public void onFailed(Drawable error, Exception cause) {

    }

    @Override
    public void onFinish() {

    }
}
