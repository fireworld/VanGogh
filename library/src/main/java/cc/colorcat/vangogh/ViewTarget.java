package cc.colorcat.vangogh;

import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.view.View;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

/**
 * Created by cxx on 17-12-7.
 * xx.ch@outlook.com
 */
public abstract class ViewTarget<V extends View> implements Target {
    private static final int TAG_ID = R.string.app_name;

    private final Reference<? extends V> ref;
    private final Object tag;

    public ViewTarget(V view, Object tag) {
        view.setTag(TAG_ID, tag);
        this.ref = new WeakReference<>(view);
        this.tag = tag;
    }

    @Override
    public void onPrepare(@Nullable Drawable placeHolder) {
        setDrawableWithCheck(placeHolder);
    }

    @Override
    public void onLoaded(Drawable drawable, From from) {
        setDrawableWithCheck(drawable);
    }

    @Override
    public void onFailed(@Nullable Drawable error, Exception cause) {
        setDrawableWithCheck(error);
        LogUtils.e(cause);
    }

    private void setDrawableWithCheck(Drawable drawable) {
        V view = ref.get();
        if (view != null && checkTag(view)) {
            setDrawable(view, drawable);
        }
    }

    private boolean checkTag(V view) {
        Object obj = view.getTag(TAG_ID);
        return tag == obj || (tag != null && tag.equals(obj));
    }

    protected abstract void setDrawable(V view, Drawable drawable);
}
