package cc.colorcat.vangogh;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;

/**
 * Created by cxx on 2017/8/8.
 * xx.ch@outlook.com
 */

public class OvalTransformation implements Transformation {

    @Override
    public Bitmap transform(Bitmap source) {
        final int width = source.getWidth(), height = source.getHeight();
        Bitmap.Config config = source.getConfig();
        if (config == null) {
            config = Bitmap.Config.ARGB_8888;
        }
        Bitmap out = Bitmap.createBitmap(width, height, config);
        Canvas canvas = new Canvas(out);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.WHITE);
        RectF rf = new RectF(0, 0, width, height);
        canvas.drawRoundRect(rf, width >> 1, height >> 1, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(source, 0, 0, paint);
        return out;
    }
}
