package cc.colorcat.vangoghdemo.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cc.colorcat.vangoghdemo.contract.IBase;
import cc.colorcat.vangoghdemo.internal.PermissionListener;

/**
 * Created by cxx on 17-5-18.
 * xx.ch@outlook.com
 */
public abstract class BaseFragment extends Fragment implements IBase.View {
    private static final int CODE_REQUEST_PERMISSION = 0x7623;

    private PermissionListener mPermissionListener;
    private boolean mActive = false;

    protected final void requestPermissions(String[] permissions, @Nullable PermissionListener listener) {
        Activity act = getActivity();
        if (act == null) return;
        mPermissionListener = listener;
        List<String> denied = null;
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(act, permission) != PackageManager.PERMISSION_GRANTED) {
                if (denied == null) denied = new ArrayList<>(permissions.length);
                denied.add(permission);
            }
        }
        if (denied != null) {
            requestPermissions(denied.toArray(new String[denied.size()]), CODE_REQUEST_PERMISSION);
        } else if (mPermissionListener != null) {
            mPermissionListener.onAllGranted();
            mPermissionListener = null;
        }
    }

    @Override
    public final void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (mPermissionListener == null) return;
        final int size = grantResults.length;
        if (size > 0 && requestCode == CODE_REQUEST_PERMISSION) {
            List<String> denied = null;
            for (int i = 0; i < size; ++i) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    if (denied == null) denied = new ArrayList<>(size);
                    denied.add(permissions[i]);
                }
            }
            if (denied == null) {
                mPermissionListener.onAllGranted();
            } else {
                mPermissionListener.onDenied(denied.toArray(new String[denied.size()]));
            }
        }
        mPermissionListener = null;
    }

    @CallSuper
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mActive = true;
    }

    @CallSuper
    @Override
    public void onDestroyView() {
        mActive = false;
        super.onDestroyView();
    }

    @ColorInt
    protected final int obtainColor(@ColorRes int id) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return getResources().getColor(id, getActivity().getTheme());
        } else {
            //noinspection deprecation
            return getResources().getColor(id);
        }
    }

    protected final Drawable obtainDrawable(@DrawableRes int id) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return getResources().getDrawable(id, getActivity().getTheme());
        } else {
            //noinspection deprecation
            return getResources().getDrawable(id);
        }
    }

    protected final ColorStateList obtainColorStateList(@ColorRes int id) throws Resources.NotFoundException {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return getResources().getColorStateList(id, getActivity().getTheme());
        } else {
            //noinspection deprecation
            return getResources().getColorStateList(id);
        }
    }

    @Override
    public final boolean isActive() {
        return mActive;
    }

    @Override
    public void toast(@StringRes int resId) {
        toast(getText(resId));
    }

    @Override
    public void toast(CharSequence text) {
        if (isActive()) {
            if (isActive()) {
                Context ctx = getContext();
                if (ctx != null) {
                    Toast.makeText(ctx, text, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    protected final void navigateTo(Class<? extends BaseActivity> clz) {
        Activity act = getActivity();
        if (act != null) {
            startActivity(new Intent(act, clz));
        }
    }
}
