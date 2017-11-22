package cc.colorcat.vangoghdemo.view;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cc.colorcat.vangoghdemo.contract.IBase;
import cc.colorcat.vangoghdemo.internal.PermissionListener;

/**
 * Created by cxx on 2017/8/15.
 * xx.ch@outlook.com
 */
public abstract class BaseActivity extends AppCompatActivity implements IBase.View {
    private static final int CODE_REQUEST_PERMISSION = 0x9234;

    private PermissionListener mPermissionListener;

    protected final void requestPermissions(String[] permissions, @Nullable PermissionListener listener) {
        mPermissionListener = listener;
        List<String> denied = null;
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                if (denied == null) denied = new ArrayList<>(permissions.length);
                denied.add(permission);
            }
        }
        if (denied != null) {
            ActivityCompat.requestPermissions(this, denied.toArray(new String[denied.size()]), CODE_REQUEST_PERMISSION);
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

    protected void setStatusBarColor(@ColorInt int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(color);
        }
    }

    @ColorInt
    protected final int obtainColor(@ColorRes int id) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return getResources().getColor(id, getTheme());
        } else {
            //noinspection deprecation
            return getResources().getColor(id);
        }
    }

    protected final Drawable obtainDrawable(@DrawableRes int id) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return getResources().getDrawable(id, getTheme());
        } else {
            //noinspection deprecation
            return getResources().getDrawable(id);
        }
    }

    protected final ColorStateList obtainColorStateList(@ColorRes int id) throws Resources.NotFoundException {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return getResources().getColorStateList(id, getTheme());
        } else {
            //noinspection deprecation
            return getResources().getColorStateList(id);
        }
    }

    @Override
    public final boolean isActive() {
        return !isFinishing();
    }

    @Override
    public void toast(@StringRes int resId) {
        toast(getText(resId));
    }

    @Override
    public void toast(CharSequence text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    }

    protected final void navigateTo(Class<? extends BaseActivity> clz) {
        navigateTo(clz, false);
    }

    protected final void navigateTo(Class<? extends BaseActivity> clz, boolean finish) {
        startActivity(new Intent(this, clz));
        if (finish) {
            finish();
        }
    }
}
