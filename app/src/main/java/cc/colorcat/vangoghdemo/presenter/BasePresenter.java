package cc.colorcat.vangoghdemo.presenter;

import android.support.annotation.CallSuper;

import cc.colorcat.vangoghdemo.contract.IBase;


/**
 * Created by cxx on 17-8-24.
 * xx.ch@outlook.com
 */
public abstract class BasePresenter<V extends IBase.View> implements IBase.Presenter<V> {

    @CallSuper
    @Override
    public void onDestroy() {

    }
}
