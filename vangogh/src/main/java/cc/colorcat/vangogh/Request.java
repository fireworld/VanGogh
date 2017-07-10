package cc.colorcat.vangogh;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.DrawableRes;
import android.view.View;

/**
 * Created by cxx on 2017/7/10.
 * xx.ch@outlook.com
 */
public class Request {
    private static final int TAG_ID = View.generateViewId();

    private Uri uri;
    private String key;
    private Target target;
    private Drawable placeHolder;
    private Drawable error;
    private Listener listener;


    public static class Builder {
        private Uri uri;
        private String key;
        private Target target;
        private Drawable placeHolder;
        private Drawable error;
        private Listener listener;

        private Builder(Uri uri) {
            this.uri = uri;
            this.key = Utils.md5(uri.toString());
        }

        private Builder(String url) {
            this.uri = Uri.parse(url);
            this.key = Utils.md5(url);
        }

        private Builder(Request request) {
            this.uri = request.uri;
            this.key = request.key;
            this.target = request.target;
            this.placeHolder = request.placeHolder;
            this.error = request.error;
            this.listener = request.listener;
        }

        public Builder placeHolder(Drawable drawable) {
            if (drawable == null) {
                throw new NullPointerException("drawable == null");
            }
            this.placeHolder = drawable;
            return this;
        }

        public Builder error(Drawable drawable) {
            if (drawable == null) {
                throw new NullPointerException("drawable == null");
            }
            this.error = drawable;
            return this;
        }

        public Builder(Listener listener) {
            this.listener = listener;
        }

        public Builder into(Target target) {
            this.target = target;
            return this;
        }
    }

    public interface Listener {

        void onSuccess(Request request, Bitmap bitmap);

        void onFailure(Request request, Exception e);
    }
}
