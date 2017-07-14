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
    private Uri uri;
    private String stableKey;
    private int fromPolicy;
    private AtomicInteger executedCount = new AtomicInteger(0);

    private Target target;
    private Drawable loadingDrawable;
    private Drawable errorDrawable;

    private Options options;

    private Task(Creator creator) {
        uri = creator.uri;
        stableKey = creator.stableKey;
        fromPolicy = creator.fromPolicy;
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

    public int fromPolicy() {
        return fromPolicy;
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

        if (fromPolicy != task.fromPolicy) return false;
        if (!uri.equals(task.uri)) return false;
        if (!stableKey.equals(task.stableKey)) return false;
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
        result = 31 * result + fromPolicy;
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
                ", fromPolicy=" + fromPolicy +
                ", executedCount=" + executedCount +
                ", target=" + target +
                ", loadingDrawable=" + loadingDrawable +
                ", errorDrawable=" + errorDrawable +
                ", options=" + options +
                '}';
    }


    public static class Options implements Cloneable {
        private Bitmap.Config config = Bitmap.Config.ARGB_8888;
        private int reqWidth = 0;
        private int reqHeight = 0;
        private boolean centerInside = false;

        Options() {

        }

        public Bitmap.Config config() {
            return config;
        }

        public void config(Bitmap.Config config) {
            if (config == null) throw new NullPointerException("config == null");
            this.config = config;
        }

        public boolean hasSize() {
            return reqWidth != 0 && reqHeight != 0;
        }

        public void reqWidth(int width) {
            if (width < 1) throw new IllegalArgumentException("width < 1");
            this.reqWidth = width;
        }

        public void reqHeight(int height) {
            if (height < 1) throw new IllegalArgumentException("height < 1");
            this.reqHeight = height;
        }

        public int reqWidth() {
            return reqWidth;
        }

        public int reqHeight() {
            return reqHeight;
        }

        public void centerInside(boolean centerInside) {
            this.centerInside = centerInside;
        }

        public boolean centerInside() {
            return centerInside;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Options options = (Options) o;

            if (reqWidth != options.reqWidth) return false;
            if (reqHeight != options.reqHeight) return false;
            if (centerInside != options.centerInside) return false;
            return config == options.config;

        }

        @Override
        public int hashCode() {
            int result = config.hashCode();
            result = 31 * result + reqWidth;
            result = 31 * result + reqHeight;
            result = 31 * result + (centerInside ? 1 : 0);
            return result;
        }

        @Override
        public String toString() {
            return "Options{" +
                    "config=" + config +
                    ", reqWidth=" + reqWidth +
                    ", reqHeight=" + reqHeight +
                    ", centerInside=" + centerInside +
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
        private int fromPolicy;

        private Target target = EmptyTarget.EMPTY;
        private Drawable loadingDrawable;
        private Drawable errorDrawable;

        private Options options;

        Creator(VanGogh vanGogh, Uri uri, String stableKey) {
            this.vanGogh = vanGogh;
            this.uri = uri;
            this.stableKey = stableKey;
            this.fromPolicy = vanGogh.defaultFromPolicy();
            this.options = vanGogh.defaultOptions();
        }

        /**
         * 数据来源策略配置，含内存、磁盘、网络三种基本模式，也可将三种模式组合使用
         * 如默认的 {@link From#ANY#policy} 即是将三种模式组合使用，会按照优先内存，其次磁盘，最后网络的方式获取
         * 如需其它的组合方式，可使用如下形式：
         * 只从内存和磁盘：<code>From.MEMORY.policy | From.DISK.policy</code>
         * 只从内存和网络：<code>From.MEMORY.policy | From.NETWORK.policy</code>
         * ...
         *
         * @param fromPolicy {@link From#MEMORY#policy}, {@link From#DISK#policy},
         *                   {@link From#NETWORK#policy}, {@link From#ANY#policy}
         * @see From
         */
        public Creator from(int fromPolicy) {
            From.checkFromPolicy(fromPolicy);
            this.fromPolicy = fromPolicy;
            return this;
        }

        public Creator loading(@DrawableRes int loadingResId) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                loadingDrawable = vanGogh.resources().getDrawable(loadingResId, null);
            } else {
                //noinspection deprecation
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
            int policy = this.fromPolicy & From.MEMORY.policy;
            if (!vanGogh.debug() && policy != 0) {
                Bitmap bitmap = vanGogh.quickMemoryCacheCheck(stableKey);
                if (bitmap != null) {
//                    LogUtils.i("quick memory success.");
                    this.target.onSuccess(bitmap, From.MEMORY);
                    return;
                }
            }
            vanGogh.enqueue(new Task(this));
        }
    }
}
