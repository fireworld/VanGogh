package cc.colorcat.vangogh;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.widget.ImageView;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by cxx on 2017/7/11.
 * xx.ch@outlook.com
 */
public class Task {
    private static final int TAG_ID = R.string.app_name;

    private Uri uri;
    private String stableKey;
    private LoadedFrom reqFrom = LoadedFrom.ANY;
    private AtomicInteger executedCount = new AtomicInteger(0);

    private Target target;
    private Drawable loadingDrawable;
    private Drawable errorDrawable;

    private Options options;

    private Task(Creator creator) {
        uri = creator.uri;
        stableKey = creator.stableKey;
        reqFrom = creator.reqFrom;
        target = creator.target;
        loadingDrawable = creator.loadingDrawable;
        errorDrawable = creator.errorDrawable;
        options = creator.options;
    }

    Task(String url) {
        this(Uri.parse(url));
    }

    Task(Uri uri) {
        if (uri == null) throw new NullPointerException("uri == null");
        this.uri = uri;
        this.stableKey = Utils.md5(uri.toString());
    }

    public Uri uri() {
        return uri;
    }

    public String stableKey() {
        return stableKey;
    }

    public LoadedFrom reqFrom() {
        return reqFrom;
    }

    public Options options() {
        return options;
    }

    int getAndIncrementExecutedCount() {
        return executedCount.getAndIncrement();
    }

    int getExecutedCount() {
        return executedCount.get();
    }

    void onPreExecute() {
        target.onStart(loadingDrawable);
    }

    void onPostResult(Result result, Exception cause) {
        if (result != null) {
            target.onSuccess(result.bitmap(), result.from());
        } else if (cause != null) {
            target.onFailed(errorDrawable, cause);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Task task = (Task) o;

        if (!uri.equals(task.uri)) return false;
        if (!stableKey.equals(task.stableKey)) return false;
        if (reqFrom != task.reqFrom) return false;
        if (!target.equals(task.target)) return false;
        if (loadingDrawable != null ? !loadingDrawable.equals(task.loadingDrawable) : task.loadingDrawable != null)
            return false;
        if (errorDrawable != null ? !errorDrawable.equals(task.errorDrawable) : task.errorDrawable != null)
            return false;
        return options.equals(task.options);

    }

    @Override
    public int hashCode() {
        int result = uri.hashCode();
        result = 31 * result + stableKey.hashCode();
        result = 31 * result + reqFrom.hashCode();
        result = 31 * result + target.hashCode();
        result = 31 * result + (loadingDrawable != null ? loadingDrawable.hashCode() : 0);
        result = 31 * result + (errorDrawable != null ? errorDrawable.hashCode() : 0);
        result = 31 * result + options.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Task{" +
                "uri=" + uri +
                ", stableKey='" + stableKey + '\'' +
                ", reqFrom=" + reqFrom +
                ", executedCount=" + executedCount +
                ", target=" + target +
                ", loadingDrawable=" + loadingDrawable +
                ", errorDrawable=" + errorDrawable +
                ", options=" + options +
                '}';
    }

    public static class Options implements Cloneable {
        private int reqWidth = 0;
        private int reqHeight = 0;
        private Bitmap.Config config = Bitmap.Config.ARGB_8888;

        Options() {

        }

        public Options(int width, int height, Bitmap.Config config) {
            this.reqWidth = width;
            this.reqHeight = height;
            this.config = config;
        }

        public boolean hasSize() {
            return reqWidth != 0 && reqHeight != 0;
        }

        public int reqWidth() {
            return reqWidth;
        }

        public int reqHeight() {
            return reqHeight;
        }

        public Bitmap.Config config() {
            return config;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Options options = (Options) o;

            if (reqWidth != options.reqWidth) return false;
            if (reqHeight != options.reqHeight) return false;
            return config == options.config;

        }

        @Override
        public int hashCode() {
            int result = reqWidth;
            result = 31 * result + reqHeight;
            result = 31 * result + config.hashCode();
            return result;
        }

        @Override
        public String toString() {
            return "Options{" +
                    "reqWidth=" + reqWidth +
                    ", reqHeight=" + reqHeight +
                    ", config=" + config +
                    '}';
        }

        @SuppressWarnings("CloneDoesntCallSuperClone")
        public Options clone() {
            try {
                return (Options) super.clone();
            } catch (CloneNotSupportedException e) {
                LogUtils.e(e);
                throw new RuntimeException(e);
            }
        }
    }

    public static class Creator {
        private final VanGogh vanGogh;

        private Uri uri;
        private String stableKey;
        private LoadedFrom reqFrom = LoadedFrom.ANY;

        private Target target = EmptyTarget.EMPTY;
        private Drawable loadingDrawable;
        private Drawable errorDrawable;

        private Options options;

        Creator(VanGogh vanGogh, Uri uri, String stableKey) {
            this.vanGogh = vanGogh;
            this.uri = uri;
            this.stableKey = stableKey;
            this.options = vanGogh.defaultOptions();
        }

        public Creator from(LoadedFrom reqFrom) {
            if (reqFrom == null) throw new NullPointerException("reqFrom == null");
            this.reqFrom = reqFrom;
            return this;
        }

        public Creator loading(@DrawableRes int loadingResId) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                loadingDrawable = vanGogh.resources().getDrawable(loadingResId, null);
            } else {
                loadingDrawable = vanGogh.resources().getDrawable(loadingResId);
            }
            return this;
        }

        public Creator loading(Drawable loading) {
            if (loading == null) throw new NullPointerException("loading == null");
            loadingDrawable = loading;
            return this;
        }

        public Creator error(@DrawableRes int errorResId) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                errorDrawable = vanGogh.resources().getDrawable(errorResId, null);
            } else {
                errorDrawable = vanGogh.resources().getDrawable(errorResId);
            }
            return this;
        }

        public Creator error(Drawable error) {
            if (error == null) throw new NullPointerException("error == null");
            errorDrawable = error;
            return this;
        }

        public Creator resize(int width, int height) {
            if (width < 1 || height < 1) {
                throw new IllegalArgumentException("width < 1 || height < 1");
            }
            options.reqWidth = width;
            options.reqHeight = height;
            return this;
        }

        public Creator config(Bitmap.Config config) {
            if (config == null) throw new NullPointerException("config == null");
            options.config = config;
            return this;
        }

        public void into(ImageView view) {
            if (view == null) throw new NullPointerException("view == null");
            this.into(new ImageViewTarget(view, stableKey));
        }

        public void into(Target target) {
            if (target == null) throw new NullPointerException("target == null");
            this.target = target;
            if (!vanGogh.debug() && (reqFrom == LoadedFrom.ANY || reqFrom == LoadedFrom.MEMORY)) {
                Bitmap bitmap = vanGogh.quickMemoryCacheCheck(stableKey);
                if (bitmap != null) {
                    LogUtils.i("quick memory success.");
                    this.target.onSuccess(bitmap, LoadedFrom.MEMORY);
                    return;
                }
            }
            vanGogh.enqueue(new Task(this));
        }
    }
}
