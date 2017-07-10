package cc.colorcat.vangogh;

/**
 * Created by cxx on 2017/7/11.
 * xx.ch@outlook.com
 */

public interface Callback {

    void onStart(Request request);

    void onResult(Request request, Result result);

    void onFailure(Request request, Exception e);

    void onFinish(Request request);
}
