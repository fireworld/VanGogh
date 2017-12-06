package cc.colorcat.vangoghdemo.internal;

import android.graphics.Bitmap;

import com.squareup.picasso.Transformation;

import cc.colorcat.vangogh.SquareTransformation;

/**
 * Created by cxx on 2017/12/6.
 * xx.ch@outlook.com
 */
public class PicassoSquareTransformation implements Transformation {
    private SquareTransformation square = new SquareTransformation();

    @Override
    public Bitmap transform(Bitmap source) {
        Bitmap result = square.transform(source);
        if (result != source) {
            source.recycle();
        }
        return result;
    }

    @Override
    public String key() {
        return "picassoSquare";
    }
}
