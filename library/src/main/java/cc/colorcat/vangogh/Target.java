package cc.colorcat.vangogh;

import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;

/**
 * Created by cxx on 2017/7/10.
 * xx.ch@outlook.com
 */
public interface Target {

    void onPrepare(@Nullable Drawable placeHolder);

    void onLoaded(Drawable drawable, From from);

    void onFailed(@Nullable Drawable error, Exception cause);
}
