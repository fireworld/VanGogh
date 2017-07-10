package cc.colorcat.vangogh;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.view.View;

/**
 * Created by cxx on 2017/7/11.
 * xx.ch@outlook.com
 */

public class EmptyTarget implements Target{
    @Override
    public View getView() {
        return null;
    }

    @Override
    public void onPrepareLoad(Drawable placeHolderDrawable) {

    }

    @Override
    public void onBitmapLoaded(@NonNull Bitmap bitmap, String url, LoadedFrom from) {

    }

    @Override
    public void onBitmapFailed(Drawable errorDrawable) {

    }
}
