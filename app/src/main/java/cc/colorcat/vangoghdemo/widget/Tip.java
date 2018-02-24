package cc.colorcat.vangoghdemo.widget;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cc.colorcat.vangoghdemo.R;

/**
 * Created by cxx on 2018/2/24.
 * xx.ch@outlook.com
 */
public class Tip {
    public static Tip from(@NonNull Activity activity, @LayoutRes int tipLayout, @Nullable Tip.Listener listener) {
        View content = activity.findViewById(android.R.id.content);
        ViewGroup root = (ViewGroup) content.getParent();
//        ViewGroup root = activity.findViewById(android.R.id.content);
//        View content = root.getChildAt(0);
        return new Tip(root, content, tipLayout, listener);
    }

    public static Tip from(Fragment fragment, @LayoutRes int tipLayout, @Nullable Tip.Listener listener) {
        View content = fragment.getView();
        if (content == null) {
            throw new NullPointerException("fragment.getView() == null");
        }
        ViewGroup root = (ViewGroup) content.getParent();
        return new Tip(root, content, tipLayout, listener);
    }

    public static Tip from(android.support.v4.app.Fragment fragment, @LayoutRes int tipLayout, @Nullable Tip.Listener listener) {
        View content = fragment.getView();
        if (content == null) {
            throw new NullPointerException("fragment.getView() == null");
        }
        ViewGroup root = (ViewGroup) content.getParent();
        return new Tip(root, content, tipLayout, listener);
    }

    public static Tip from(DialogFragment dialogFragment, @LayoutRes int tipLayout, @Nullable Tip.Listener listener) {
        View content = dialogFragment.getView();
        if (content == null) {
            throw new NullPointerException("dialogFragment.getView() == null");
        }
        ViewGroup root = (ViewGroup) content.getParent();
        return new Tip(root, content, tipLayout, listener);
    }

    public static Tip from(android.support.v4.app.DialogFragment dialogFragment, @LayoutRes int tipLayout, @Nullable Tip.Listener listener) {
        View content = dialogFragment.getView();
        if (content == null) {
            throw new NullPointerException("dialogFragment.getView() == null");
        }
        ViewGroup root = (ViewGroup) content.getParent();
        return new Tip(root, content, tipLayout, listener);
    }

    public static Tip from(View view, @LayoutRes int tipLayout, @Nullable Tip.Listener listener) {
        ViewGroup root = (ViewGroup) view.getParent();
        if (root == null) {
            throw new NullPointerException("The specified view must have a parent");
        }
        return new Tip(root, view, tipLayout, listener);
    }

    private final ViewGroup mRoot;
    private View mContentView;
    private int mContentViewIndex = -1;
    private View mTipView;
    @LayoutRes
    private int mTipLayout;
    private Listener mListener;

    private Tip(ViewGroup root, View contentView, @LayoutRes int tipLayout, Listener listener) {
        mRoot = root;
        mContentView = contentView;
        mTipLayout = tipLayout;
        mListener = listener;
    }

    public final void showTip() {
//        mRoot.removeView(mContentView);
        removeContentView();
        final View tipView = getTipView();
        if (tipView.getParent() == null) {
            mRoot.addView(tipView, mContentViewIndex);
        }
    }

    private void removeContentView() {
        if (mContentViewIndex == -1) {
            mContentViewIndex = mRoot.indexOfChild(mContentView);
        }
        mRoot.removeView(mContentView);
    }

    public final void hideTip() {
        if (mTipView != null) {
            mRoot.removeView(mTipView);
            if (mContentView.getParent() == null) {
                mRoot.addView(mContentView, mContentViewIndex);
            }
        }
    }

    public void setListener(@Nullable Tip.Listener listener) {
        mListener = listener;
    }

    private View getTipView() {
        if (mTipView == null) {
            mTipView = LayoutInflater.from(mRoot.getContext()).inflate(mTipLayout, mRoot, false);
            View tip = mTipView.findViewById(R.id.tip);
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