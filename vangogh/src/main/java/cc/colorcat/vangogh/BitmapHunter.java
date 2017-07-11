package cc.colorcat.vangogh;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;

import java.io.InputStream;

/**
 * Created by cxx on 2017/7/10.
 * xx.ch@outlook.com
 */
class BitmapHunter {
    private static final int TAG_ID = R.string.app_name;

    private Target target;
    private Drawable placeHolder;
    private Drawable error;
    private Task task;
    private LoadedFrom reqFrom;

    private BitmapHunter(Creator creator) {
        this.target = creator.target;
        this.placeHolder = creator.placeHolder;
        this.error = creator.error;
        this.task = creator.task;
        this.reqFrom = creator.reqFrom;
    }

    Task task() {
        return task;
    }

    void start() {
        View view = target.getView();
        if (view != null) {
            view.setTag(TAG_ID, task.getUri());
        }
        target.onStart(placeHolder);
    }

    void hunted(Result result) {
        View view = target.getView();
        if (view == null || task.getUri().equals(view.getTag(TAG_ID))) {
            Bitmap bitmap = process(result);
            if (bitmap != null) {
                target.onSuccess(bitmap, result.from());
            }
        }
    }

    void failed(Exception e) {
        View view = target.getView();
        if (view == null || task.getUri().equals(view.getTag(TAG_ID))) {
            target.onFailed(error, e);
        }
    }

    void finish() {
        View view = target.getView();
        if (view == null || task.getUri().equals(view.getTag(TAG_ID))) {
            target.onFinish();
        }
    }

    private Bitmap process(Result result) {
        Bitmap bitmap = result.bitmap();
        if (bitmap == null) {
            InputStream is = result.stream();
            try {
                bitmap = Utils.decodeStream(result.stream());
            } finally {
                Utils.close(is);
            }
        }
        return bitmap;
    }

    public static class Creator {
        private Target target;
        private Drawable placeHolder;
        private Drawable error;
        private Task task;
        private LoadedFrom reqFrom = LoadedFrom.ANY;

        Creator(String url) {
            this(Uri.parse(url));
        }

        Creator(Uri uri) {
            task = new Task(uri);
            target = EmptyTarget.EMPTY;
        }

        private Creator(BitmapHunter hunter) {
            this.target = hunter.target;
            this.placeHolder = hunter.placeHolder;
            this.error = hunter.error;
            this.task = hunter.task();
            this.reqFrom = hunter.reqFrom;
        }

        public Creator placeHolder(Drawable drawable) {
            if (drawable == null) {
                throw new NullPointerException("drawable == null");
            }
            this.placeHolder = drawable;
            return this;
        }

        public Creator error(Drawable drawable) {
            if (drawable == null) {
                throw new NullPointerException("drawable == null");
            }
            this.error = drawable;
            return this;
        }

        public Creator from(LoadedFrom from) {
            if (from == null) {
                throw new NullPointerException("from == null");
            }
            task.setFrom(from);
            return this;
        }

        public Creator maxSize(int width, int height) {
            if (width < 1 || height < 1) {
                throw new IllegalArgumentException("width < 1 || height < 1");
            }
            task.setMaxSize(width, height);
            return this;
        }

        public Creator config(Bitmap.Config config) {
            if (config == null) {
                throw new NullPointerException("config == null");
            }
            task.config(config);
            return this;
        }

        public void into(Target target) {
            if (target == null) {
                throw new NullPointerException("target == null");
            }
            this.target = target;
            VanGogh.enqueue(new BitmapHunter(this));
        }

        public void into(ImageView view) {
            if (view == null) {
                throw new NullPointerException("view == null");
            }
            this.target = new ImageViewTarget(view);
            VanGogh.enqueue(new BitmapHunter(this));
        }

        public void into() {
            VanGogh.enqueue(new BitmapHunter(this));
        }
    }
}
