package cc.colorcat.vangogh;

import android.graphics.Bitmap;
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

    private Reference<ImageView> ref;
    private String tag;
    private Drawable original;

    ImageViewTarget(ImageView view) {
        this(view, Long.toString(System.currentTimeMillis()));
    }

    ImageViewTarget(ImageView view, String tag) {
        view.setTag(TAG_ID, tag);
        this.ref = new WeakReference<>(view);
        this.tag = tag;
        this.original = view.getDrawable();
    }

    @Override
    public void onStart(@Nullable Drawable placeHolder) {
        setDrawable(placeHolder, true);
    }

    @Override
    public void onSuccess(Bitmap bitmap, LoadedFrom from) {
        setBitmap(bitmap, from);
    }

    @Override
    public void onFailed(@Nullable Drawable error, Exception cause) {
        setDrawable(error, false);
        LogUtils.e(cause);
    }

    private void setBitmap(Bitmap bitmap, LoadedFrom from) {
        ImageView view = ref.get();
        if (view != null && checkTag(view)) {
            view.setImageBitmap(bitmap);
        }
    }

    private void setDrawable(Drawable drawable, boolean start) {
        ImageView view = ref.get();
        if (view != null && checkTag(view)) {
            if (drawable != null) {
                view.setImageDrawable(drawable);
            } else if (original != null) {
                view.setImageDrawable(original);
            }
        }
    }

    private boolean checkTag(ImageView view) {
        return tag.equals(view.getTag(TAG_ID));
    }
}
