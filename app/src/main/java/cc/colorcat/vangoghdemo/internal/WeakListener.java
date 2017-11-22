package cc.colorcat.vangoghdemo.internal;

import android.support.annotation.CallSuper;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

import cc.colorcat.netbird3.HttpStatus;
import cc.colorcat.netbird3.MRequest;
import cc.colorcat.vangoghdemo.contract.IBase;

/**
 * Created by cxx on 17-11-22.
 * xx.ch@outlook.com
 */
public abstract class WeakListener<R, V extends IBase.View> implements MRequest.Listener<R> {
    private Reference<V> reference;

    public WeakListener(V v) {
        if (v == null) throw new NullPointerException("v == null");
        reference = new WeakReference<>(v);
    }

    @Override
    public final void onStart() {
        V v = reference.get();
        if (v != null && v.isActive()) {
            onStart(v);
        }
    }

    @Override
    public final void onSuccess(R data) {
        V v = reference.get();
        if (v != null && v.isActive()) {
            onSuccess(v, data);
        }
    }

    @Override
    public final void onFailure(int code, String msg) {
        V v = reference.get();
        if (v != null && v.isActive()) {
            onFailure(v, code, msg);
        }
    }

    @Override
    public final void onFinish() {
        V v = reference.get();
        if (v != null && v.isActive()) {
            onFinish(v);
            reference.clear();
        }
    }

    public void onStart(V view) {

    }

    public abstract void onSuccess(V view, R data);

    @CallSuper
    public void onFailure(V view, int code, String msg) {
        switch (code) {
            case HttpStatus.CODE_CONNECT_ERROR:
                view.toast(cc.colorcat.vangoghdemo.R.string.connect_error);
                break;
            default:
                break;
        }
    }

    public void onFinish(V view) {

    }
}
