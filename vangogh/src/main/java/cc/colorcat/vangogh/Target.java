package cc.colorcat.vangogh;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.view.View;

/**
 * Created by cxx on 2017/7/10.
 * xx.ch@outlook.com
 */
public interface Target {

    void onStart(@Nullable Drawable placeHolder);

    void onSuccess(Bitmap bitmap, LoadedFrom from);

    void onFailed(@Nullable Drawable error, Exception cause);
}
