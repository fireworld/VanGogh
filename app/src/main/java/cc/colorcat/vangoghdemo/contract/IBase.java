package cc.colorcat.vangoghdemo.contract;

import android.app.Activity;
import android.support.annotation.StringRes;

/**
 * Created by cxx on 17-5-18.
 * xx.ch@outlook.com
 */
public interface IBase {

    interface View {

        /**
         * @return 当前 View 可用（未被销毁，可正确设置数据）返回 true，否则返回 false.
         * @see Activity#isFinishing()
         */
        boolean isActive();

        void toast(@StringRes int resId);

        void toast(CharSequence text);

        void showNetworkError();

        void hideNetworkError();
    }

    interface Presenter<V extends View> {

        /**
         * 初始化 {@link Presenter}，在 {@link View} 初始化后调用
         *
         * @param v {@link View}
         */
        void onCreate(V v);

        /**
         * {@link View} 销毁时调用
         */
        void onDestroy();
    }
}
