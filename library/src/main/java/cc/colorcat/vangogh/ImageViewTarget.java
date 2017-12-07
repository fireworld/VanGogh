package cc.colorcat.vangogh;

import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

/**
 * Created by cxx on 2017/7/10.
 * xx.ch@outlook.com
 */
class ImageViewTarget implements Target {
    private static final int TAG_ID = R.string.app_name;

    private final Reference<? extends ImageView> ref;
    private final String tag;

    ImageViewTarget(ImageView view, String tag) {
        this.ref = new WeakReference<>(view);
        this.tag = tag;
    }

    @Override
    public void onPrepare(@Nullable Drawable placeHolder) {
        ImageView view = ref.get();
        if (view != null) {
            view.setImageDrawable(placeHolder);
            view.setTag(TAG_ID, tag);
        }
    }

    @Override
    public void onLoaded(Drawable drawable, From from) {
        setDrawable(drawable);
    }

    @Override
    public void onFailed(@Nullable Drawable error, Exception cause) {
        setDrawable(error);
        LogUtils.e(cause);
    }

    private void setDrawable(Drawable drawable) {
        ImageView view = ref.get();
        if (view != null && checkTag(view)) {
            view.setImageDrawable(drawable);
        }
    }

    private boolean checkTag(ImageView view) {
        return tag.equals(view.getTag(TAG_ID));
    }
}
