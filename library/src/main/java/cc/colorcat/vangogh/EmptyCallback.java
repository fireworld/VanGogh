package cc.colorcat.vangogh;

import android.graphics.Bitmap;

/**
 * Created by cxx on 2017/12/14.
 * xx.ch@outlook.com
 */
class EmptyCallback implements Callback {
    final static Callback EMPTY = new EmptyCallback();

    private EmptyCallback() {

    }

    @Override
    public void onSuccess(Bitmap bitmap) {

    }

    @Override
    public void onError(Exception cause) {

    }
}
