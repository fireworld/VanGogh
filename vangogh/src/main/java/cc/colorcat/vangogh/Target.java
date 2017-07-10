package cc.colorcat.vangogh;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.view.View;

/**
 * Created by cxx on 2017/7/10.
 * xx.ch@outlook.com
 */
public interface Target {

    View getView();

    void onPrepareLoad(Drawable placeHolderDrawable);

    void onBitmapLoaded(@NonNull Bitmap bitmap, String url, LoadedFrom from);

    void onBitmapFailed(Drawable errorDrawable);
}
