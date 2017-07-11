package cc.colorcat.vangogh;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by cxx on 2017/7/10.
 * xx.ch@outlook.com
 */
class ImageViewTarget implements Target {
    private ImageView view;

    ImageViewTarget(ImageView view) {
        this.view = view;
    }

    @Override
    public View getView() {
        return view;
    }

    @Override
    public void onStart(Drawable placeHolder) {
        if (placeHolder != null) {
            view.setImageDrawable(placeHolder);
        }
    }

    @Override
    public void onSuccess(Bitmap bitmap, LoadedFrom from) {
        view.setImageBitmap(bitmap);
    }

    @Override
    public void onFailed(Drawable error, Exception cause) {
        cause.printStackTrace();
        if (error != null) {
            view.setImageDrawable(error);
        }
    }

    @Override
    public void onFinish() {
        Log.i("ImageViewTarget", "onFinish " + view);
    }
}
