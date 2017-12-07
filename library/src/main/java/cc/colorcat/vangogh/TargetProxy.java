package cc.colorcat.vangogh;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;

/**
 * Created by cxx on 17-12-7.
 * xx.ch@outlook.com
 */
class TargetProxy implements Target {
    private final Target target;
    private final Callback callback;

    TargetProxy(Target target, Callback callback) {
        this.target = target;
        this.callback = callback;
    }

    @Override
    public void onPrepare(@Nullable Drawable placeHolder) {
        target.onPrepare(placeHolder);
    }

    @Override
    public void onLoaded(Drawable drawable, From from) {
        target.onLoaded(drawable, from);
        if (drawable instanceof BitmapDrawable) {
            callback.onSuccess(((BitmapDrawable) drawable).getBitmap());
        }
    }

    @Override
    public void onFailed(@Nullable Drawable error, Exception cause) {
        target.onFailed(error, cause);
        callback.onError(cause);
    }
}
