package cc.colorcat.vangogh;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by cxx on 2017/7/10.
 * xx.ch@outlook.com
 */
public class ImageViewTarget implements Target {
    private ImageView view;
    private boolean debug;

    private ImageViewTarget(ImageView view, boolean debug) {
        this.view = view;
        this.debug = debug;
    }

    @Override
    public View getView() {
        return view;
    }

    @Override
    public void onPrepareLoad(Drawable placeHolderDrawable) {
        if (placeHolderDrawable != null) {
            view.setImageDrawable(placeHolderDrawable);
        }
    }

    @Override
    public void onBitmapLoaded(@NonNull Bitmap bitmap, String url, VanGogh.LoadedFrom from) {
        view.setImageBitmap(bitmap);
        if (debug) {
            view.setBackgroundColor(from.debugColor);
        }
    }

    @Override
    public void onBitmapFailed(Drawable errorDrawable) {
        if (errorDrawable != null) {
            view.setImageDrawable(errorDrawable);
        }
    }
}
