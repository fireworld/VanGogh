package cc.colorcat.vangogh;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

/**
 * Created by cxx on 2017/7/10.
 * xx.ch@outlook.com
 */
class ImageViewTarget implements Target {
    private Reference<ImageView> ref;

    ImageViewTarget(ImageView view) {
        ref = new WeakReference<>(view);
    }

    @Override
    public View getView() {
        return ref.get();
    }

    @Override
    public void onStart(Drawable placeHolder) {
        setDrawable(placeHolder);
    }

    @Override
    public void onSuccess(Bitmap bitmap, LoadedFrom from) {
        setBitmap(bitmap, from);
    }

    @Override
    public void onFailed(Drawable error, Exception cause) {
        cause.printStackTrace();
        setDrawable(error);
    }

    private void setBitmap(Bitmap bitmap, LoadedFrom from) {
        ImageView view = ref.get();
        if (view != null) {
            view.setImageBitmap(bitmap);
        }
    }

    private void setDrawable(Drawable drawable) {
        if (drawable != null) {
            ImageView view = ref.get();
            if (view != null) {
                view.setImageDrawable(drawable);
            }
        }
    }

    @Override
    public void onFinish() {
        Log.i("ImageViewTarget", "onFinish " + ref);
    }
}
