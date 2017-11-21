package cc.colorcat.vangogh;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;

/**
 * Created by cxx on 2017/8/8.
 * xx.ch@outlook.com
 */
public class VanGoghDrawable extends BitmapDrawable {
    private boolean animating;
    private int mAlpha = 0; // [0, 255]

    public VanGoghDrawable(Resources res, Bitmap bitmap) {
        this(res, bitmap, true);
    }

    public VanGoghDrawable(Resources res, Bitmap bitmap, boolean animating) {
        super(res, bitmap);
        this.animating = animating;
    }

    @Override
    public void draw(Canvas canvas) {
        if (animating && mAlpha < 255) {
            setAlpha(Math.min(mAlpha + 10, 255));
        }
        super.draw(canvas);
    }

    @Override
    public void setAlpha(int alpha) {
        super.setAlpha(alpha);
        mAlpha = alpha;
    }
}
