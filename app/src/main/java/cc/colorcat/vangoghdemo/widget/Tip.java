package cc.colorcat.vangoghdemo.widget;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by cxx on 2018/2/24.
 * xx.ch@outlook.com
 */
public class Tip {
    public static Tip from(@NonNull Activity activity, @LayoutRes int tipLayout, @Nullable Tip.Listener listener) {
        ViewGroup parent = activity.findViewById(android.R.id.content);
        return new Tip(parent, parent.getChildAt(0), tipLayout, listener);
    }

    public static Tip from(Fragment fragment, @LayoutRes int tipLayout, @Nullable Tip.Listener listener) {
        View view = fragment.getView();
        if (view == null) {
            throw new NullPointerException("fragment.getView() == null");
        }
        ViewGroup parent = (ViewGroup) view.getParent();
        return new Tip(parent, parent.getChildAt(0), tipLayout, listener);
    }

    public static Tip from(android.support.v4.app.Fragment fragment, @LayoutRes int tipLayout, @Nullable Tip.Listener listener) {
        View view = fragment.getView();
        if (view == null) {
            throw new NullPointerException("fragment.getView() == null");
        }
        ViewGroup parent = (ViewGroup) view.getParent();
        return new Tip(parent, parent.getChildAt(0), tipLayout, listener);
    }

    public static Tip from(DialogFragment dialogFragment, @LayoutRes int tipLayout, @Nullable Tip.Listener listener) {
        View view = dialogFragment.getView();
        if (view == null) {
            throw new NullPointerException("dialogFragment.getView() == null");
        }
        ViewGroup parent = (ViewGroup) view.getParent();
        return new Tip(parent, parent.getChildAt(0), tipLayout, listener);
    }

    public static Tip from(android.support.v4.app.DialogFragment dialogFragment, @LayoutRes int tipLayout, @Nullable Tip.Listener listener) {
        View view = dialogFragment.getView();
        if (view == null) {
            throw new NullPointerException("dialogFragment.getView() == null");
        }
        ViewGroup parent = (ViewGroup) view.getParent();
        return new Tip(parent, parent.getChildAt(0), tipLayout, listener);
    }

    public static Tip from(View view, @LayoutRes int tipLayout, @Nullable Tip.Listener listener) {
        ViewGroup parent = (ViewGroup) view.getParent();
        if (parent == null) {
            throw new NullPointerException("The specified view must have a parent");
        }
        return new Tip(parent, view, tipLayout, listener);
    }

    private final ViewGroup mParentView;
    private View mContentView;
    private int mContentIndex;
    private View mTipView;
    @LayoutRes
    private int mTipLayout;
    private Listener mListener;

    private Tip(ViewGroup parent, View contentView, @LayoutRes int tipLayout, Listener listener) {
        mParentView = parent;
        mContentView = contentView;
        mTipLayout = tipLayout;
        mListener = listener;
    }

    public final void showTip() {
        final View tip = getTipView();
        if (tip.getParent() == null) {
            mContentIndex = mParentView.indexOfChild(mContentView);
            mParentView.removeViewAt(mContentIndex);
            mParentView.addView(tip, mContentIndex, mContentView.getLayoutParams());
        }
    }

    public final void hideTip() {
        if (mTipView != null && mContentView.getParent() == null) {
            mParentView.removeView(mTipView);
            mParentView.addView(mContentView, mContentIndex);
        }
    }

    public void setListener(@Nullable Tip.Listener listener) {
        mListener = listener;
    }

    private View getTipView() {
        if (mTipView == null) {
            Context ctx = mParentView.getContext();
            mTipView = LayoutInflater.from(ctx).inflate(mTipLayout, mParentView, false);
            int id = ctx.getResources().getIdentifier("tip", "id", ctx.getPackageName());
            View tip = mTipView.findViewById(id);
            if (tip != null) {
                tip.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        hideTip();
                        if (mListener != null) {
                            mListener.onTipClick();
                        }
                    }
                });
            }
        }
        return mTipView;
    }

    public interface Listener {
        void onTipClick();
    }
}
