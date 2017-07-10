package cc.colorcat.vangogh;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.View;

/**
 * Created by cxx on 2017/7/10.
 * xx.ch@outlook.com
 */
public class Request {
    private static final int TAG_ID = View.generateViewId();

    private String url;
    private String key;
    private Target target;
    private Drawable preparePlaceHolder;
    private Drawable errorDrawable;
    private Callback callback;

    Request(String url) {
        if (!Utils.isHttpUrl(url)) {
            throw new IllegalArgumentException("is not http/https: " + url);
        }
        this.url = url;
        this.key = Utils.md5(url);
    }

    Request target(Target target) {
        this.target = target;
        if (this.target != null) {
            this.target.getView().setTag(TAG_ID, key);
        }
        return this;
    }

    Request prepareDrawable(Drawable placeHolder) {
        this.preparePlaceHolder = placeHolder;
        return this;
    }

    Request errorDrawable(Drawable placeHolder) {
        this.errorDrawable = placeHolder;
        return this;
    }

    String key() {
        return key;
    }

    String url() {
        return url;
    }

    void prepare() {
        if (target != null && preparePlaceHolder != null) {
            target.onPrepareLoad(preparePlaceHolder);
        }
    }

    void deliver(Bitmap bitmap, VanGogh.LoadedFrom from, Exception e) {
        boolean deliverToTarget = target != null && key.equals(target.getView().getTag(TAG_ID));
        if (bitmap != null) {
            if (deliverToTarget) {
                target.onBitmapLoaded(bitmap, url, from);
            }
            if (callback != null) {
                callback.onSuccess(bitmap, url);
            }
        } else {
            if (deliverToTarget && errorDrawable != null) {
                target.onBitmapFailed(errorDrawable);
            }
            if (callback != null) {
                callback.onError(url, e);
            }
        }
    }

    interface Callback {

        void onSuccess(Bitmap bitmap, String url);

        void onError(String url, Exception e);
    }
}
