package cc.colorcat.vangogh;

import android.graphics.Bitmap;

/**
 * Created by cxx on 17-12-7.
 * xx.ch@outlook.com
 */
public interface Callback {

    void onSuccess(Bitmap bitmap);

    void onError(Exception cause);
}
