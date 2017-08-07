package cc.colorcat.vangogh;

import android.graphics.Bitmap;

/**
 * Created by cxx on 2017/8/8.
 * xx.ch@outlook.com
 */

public class SquareTransformation implements Transformation {

    @Override
    public Bitmap transform(Bitmap source) {
        final int width = source.getWidth(), height = source.getHeight();
        final int side = Math.min(width, height);
        final int left = (width - side) >> 1;
        final int top = (height - side) >> 1;
        return Bitmap.createBitmap(source, left, top, side, side);
    }
}
