package cc.colorcat.vangogh;

/**
 * Created by cxx on 2017/7/11.
 * xx.ch@outlook.com
 */

public interface Callback {

    void onStart(BitmapHunter hunter);

    void onResult(BitmapHunter hunter, Result result);

    void onFailure(BitmapHunter hunter, Exception e);

    void onFinish(BitmapHunter hunter);
}
