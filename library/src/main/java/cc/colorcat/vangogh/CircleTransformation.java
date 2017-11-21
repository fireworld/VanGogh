package cc.colorcat.vangogh;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Xfermode;
import android.support.annotation.ColorInt;

/**
 * Created by cxx on 2017/8/8.
 * xx.ch@outlook.com
 */
public class CircleTransformation implements Transformation {
    private final float borderWidth;
    @ColorInt
    private final int color;
    private final boolean hasBorder;

    public CircleTransformation() {
        this(0F, Color.WHITE);
    }

    public CircleTransformation(float borderWidth, @ColorInt int borderColor) {
        this.borderWidth = borderWidth > 0F ? borderWidth : 0F;
        this.color = borderColor;
        this.hasBorder = this.borderWidth > 0F;
    }

    @Override
    public Bitmap transform(Bitmap source) {
        int width = source.getWidth(), height = source.getHeight();
        int side = Math.min(width, height);
        Bitmap.Config config = source.getConfig();
        if (config == null || config == Bitmap.Config.RGB_565) {
            config = Bitmap.Config.ARGB_8888;
        }
        Bitmap out = Bitmap.createBitmap(side, side, config);
        Canvas canvas = new Canvas(out);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(color);
        float cxy = side / 2.0F; // 圆心坐标，x 轴与 y 轴相同，位于正中间。
        float radius = cxy - borderWidth; // 圆半径
        canvas.drawCircle(cxy, cxy, radius, paint);
        Xfermode back = paint.getXfermode();
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(source, (side - width) / 2.0F, (side - height) / 2.0F, paint);
        if (hasBorder) {
            paint.setXfermode(back);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(borderWidth);
            canvas.drawCircle(cxy, cxy, radius, paint);
        }
        return out;
    }
}
