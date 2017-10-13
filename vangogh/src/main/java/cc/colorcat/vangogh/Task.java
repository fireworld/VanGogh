package cc.colorcat.vangogh;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by cxx on 2017/7/11.
 * xx.ch@outlook.com
 */
public class Task {
    private Resources resources;

    private Uri uri;
    private String stableKey;
    private int fromPolicy;

    private int connectTimeOut;
    private int readTimeOut;

    private Target target;
    private Drawable loadingDrawable;
    private Drawable errorDrawable;

    private Options options;

    private List<Transformation> transformations;
    private boolean fade;

    private Task(Creator creator) {
        resources = creator.vanGogh.resources();
        uri = creator.uri;
        stableKey = creator.stableKey;
        fromPolicy = creator.fromPolicy;
        connectTimeOut = creator.connectTimeOut;
        readTimeOut = creator.readTimeOut;
        target = creator.target;
        loadingDrawable = creator.loadingDrawable;
        errorDrawable = creator.errorDrawable;
        options = creator.options;
        transformations = Utils.immutableList(creator.transformations);
        fade = creator.fade;
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

    public int connectTimeOut() {
        return connectTimeOut;
    }

    public int readTimeOut() {
        return readTimeOut;
    }

    public Options options() {
        return options;
    }

    public List<Transformation> transformations() {
        return transformations;
    }

    void onPreExecute() {
        target.onStart(loadingDrawable);
    }

    void onPostResult(Result result, Exception cause) {
        if (result != null) {
            target.onSuccess(new VanGoghDrawable(resources, result.bitmap(), fade), result.from());
        } else if (cause != null) {
            target.onFailed(errorDrawable, cause);
        }
    }

    @Override
    public String toString() {
        return "Task{" +
                "resources=" + resources +
                ", uri=" + uri +
                ", stableKey='" + stableKey + '\'' +
                ", fromPolicy=" + fromPolicy +
                ", connectTimeOut=" + connectTimeOut +
                ", readTimeOut=" + readTimeOut +
                ", target=" + target +
                ", loadingDrawable=" + loadingDrawable +
                ", errorDrawable=" + errorDrawable +
                ", options=" + options +
                ", transformations=" + transformations +
                ", fade=" + fade +
                '}';
    }


    public static class Options implements Cloneable {
        private Bitmap.Config config = Bitmap.Config.ARGB_8888;
        private int reqWidth = 0;
        private int reqHeight = 0;
        private float rotationDegrees;
        private boolean hasRotation;
        private float rotationPivotX;
        private float rotationPivotY;
        private boolean hasRotationPivot;

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

        public void resize(int width, int height) {
            if (width < 1 || height < 1) {
                throw new IllegalArgumentException("width < 1 || height < 1");
            }
            this.reqWidth = width;
            this.reqHeight = height;
        }

        public int reqWidth() {
            return reqWidth;
        }

        public int reqHeight() {
            return reqHeight;
        }

        public void rotate(float degrees, float pivotX, float pivotY) {
            rotate(degrees);
            rotationPivotX = pivotX;
            rotationPivotY = pivotY;
            hasRotationPivot = true;
        }

        public void rotate(float degrees) {
            rotationDegrees = degrees;
            hasRotation = true;
        }

        public float rotationDegrees() {
            return rotationDegrees;
        }

        public float rotationPivotX() {
            return rotationPivotX;
        }

        public float rotationPivotY() {
            return rotationPivotY;
        }

        public boolean hasRotation() {
            return hasRotation;
        }

        public boolean hasRotationPivot() {
            return hasRotationPivot;
        }

        @Override
        public String toString() {
            return "Options{" +
                    "config=" + config +
                    ", reqWidth=" + reqWidth +
                    ", reqHeight=" + reqHeight +
                    ", rotationDegrees=" + rotationDegrees +
                    ", hasRotation=" + hasRotation +
                    ", rotationPivotX=" + rotationPivotX +
                    ", rotationPivotY=" + rotationPivotY +
                    ", hasRotationPivot=" + hasRotationPivot +
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

        private int connectTimeOut;
        private int readTimeOut;

        private Target target = EmptyTarget.EMPTY;
        private Drawable loadingDrawable;
        private Drawable errorDrawable;

        private Options options;

        private List<Transformation> transformations = new ArrayList<>(4);
        private boolean fade;

        Creator(VanGogh vanGogh, Uri uri, String stableKey) {
            this.vanGogh = vanGogh;
            this.uri = uri;
            this.stableKey = stableKey;
            this.fromPolicy = vanGogh.defaultFromPolicy();
            this.connectTimeOut = vanGogh.connectTimeOut();
            this.readTimeOut = vanGogh.readTimeOut();
            this.loadingDrawable = vanGogh.defaultLoading();
            this.errorDrawable = vanGogh.defaultError();
            this.options = vanGogh.defaultOptions();
            this.fade = vanGogh.fade();
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

        public Creator connectTimeOut(int timeOut) {
            if (timeOut < 0) {
                throw new IllegalArgumentException("timeOut(" + timeOut + ") < 0");
            }
            this.connectTimeOut = timeOut;
            return this;
        }

        public Creator readTimeOut(int timeOut) {
            if (timeOut < 0) {
                throw new IllegalArgumentException("timeOut(" + timeOut + ") < 0");
            }
            this.readTimeOut = timeOut;
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
                //noinspection deprecation
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
            options.resize(width, height);
            return this;
        }

        public Creator config(Bitmap.Config config) {
            options.config(config);
            return this;
        }

        public Creator rotate(float degrees) {
            options.rotate(degrees);
            return this;
        }

        public Creator rotate(float degrees, float pivotX, float pivotY) {
            options.rotate(degrees, pivotX, pivotY);
            return this;
        }

        public Creator addTransformation(Transformation transformation) {
            if (transformation == null) throw new NullPointerException("transformation == null");
            transformations.add(transformation);
            return this;
        }

        public Creator fade(boolean fade) {
            this.fade = fade;
            return this;
        }

        public void into(ImageView view) {
            if (view == null) throw new NullPointerException("view == null");
            this.into(new ImageViewTarget(view, stableKey));
        }

        public void into(Target target) {
            if (target == null) throw new NullPointerException("target == null");
            this.target = target;
            int policy = fromPolicy & From.MEMORY.policy;
            if (policy != 0) {
                Bitmap bitmap = vanGogh.quickMemoryCacheCheck(stableKey);
                if (bitmap != null) {
//                    LogUtils.i("quick memory success.");
                    List<Transformation> trans = new LinkedList<>(vanGogh.transformations());
                    trans.addAll(transformations);
                    bitmap = Utils.transformResult(bitmap, options, trans);
                    if (vanGogh.debug()) {
                        bitmap = Utils.makeWatermark(bitmap, From.MEMORY.debugColor);
                    }
                    target.onSuccess(new BitmapDrawable(vanGogh.resources(), bitmap), From.MEMORY);
                    return;
                }
            }
            vanGogh.enqueue(new Task(this));
        }
    }
}
