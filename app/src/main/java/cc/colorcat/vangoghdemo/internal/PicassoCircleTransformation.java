package cc.colorcat.vangoghdemo.internal;

import android.graphics.Bitmap;

import com.squareup.picasso.Transformation;

import cc.colorcat.vangogh.CircleTransformation;

/**
 * Created by cxx on 2017/12/6.
 * xx.ch@outlook.com
 */
public class PicassoCircleTransformation implements Transformation {
    private CircleTransformation circle = new CircleTransformation();

    @Override
    public Bitmap transform(Bitmap source) {
        Bitmap result = circle.transform(source);
        if (result != source) {
            source.recycle();
        }
        return result;
    }

    @Override
    public String key() {
        return "picassoCircle";
    }
}
