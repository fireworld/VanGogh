package cc.colorcat.vangogh;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;

/**
 * Created by cxx on 2017/7/10.
 * xx.ch@outlook.com
 */
class ImageViewTarget extends ViewTarget<ImageView> {

    ImageViewTarget(ImageView view, Object tag) {
        super(view, tag);
    }

    @Override
    protected void setDrawable(ImageView view, Drawable drawable) {
        view.setImageDrawable(drawable);
    }
}
