package cc.colorcat.vangoghdemo.widget;

import android.support.annotation.IntDef;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Locale;

/**
 * Created by cxx on 2017/8/13.
 * xx.ch@outlook.com
 */
public abstract class RvAdapter extends RecyclerView.Adapter<RvHolder> {
    @ChoiceMode
    private int mChoiceMode = ChoiceMode.NONE;
    private int mSelectedPosition = AdapterView.INVALID_POSITION;
    private OnItemSelectedChangedListener mSelectedListener;
    private SelectHelper mSelectHelper;

    @Override
    public final RvHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(getLayoutResId(viewType), parent, false);
        RvHolder holder = new RvHolder(itemView);
        holder.getHelper().setViewType(viewType).setPosition(holder.getAdapterPosition());
        return holder;
    }

    @Override
    public final void onBindViewHolder(RvHolder holder, int position) {
        final RvHolder.Helper helper = holder.getHelper();
        helper.setViewType(holder.getItemViewType()).setPosition(position);
        if (inChoiceMode()) {
            final View itemView = helper.getRoot();
            if (itemView.isSelected()) {
                if (!isSelectedByChoiceMode(position)) {
                    itemView.setSelected(false);
                    mSelectedListener.onItemSelectedChanged(position, false);
                }
            } else {
                if (isSelectedByChoiceMode(position)) {
                    itemView.setSelected(true);
                    mSelectedListener.onItemSelectedChanged(position, true);
                }
            }
        }
        bindView(holder, position);
    }

    public void setChoiceMode(@ChoiceMode int choiceMode) {
        if (choiceMode == ChoiceMode.NONE
                || choiceMode == ChoiceMode.SINGLE
                || choiceMode == ChoiceMode.MULTIPLE) {
            mChoiceMode = choiceMode;
            return;
        }
        throw new IllegalArgumentException("choiceMode must be in [0, 2]");
    }

    public void attach(RecyclerView recyclerView) {
        if (mSelectHelper == null) {
            mSelectHelper = new SelectHelper();
        }
        recyclerView.addOnItemTouchListener(mSelectHelper);
    }

    public void setOnItemSelectedListener(OnItemSelectedChangedListener listener) {
        mSelectedListener = listener;
    }

    public OnItemSelectedChangedListener getOnItemSelectedChangedListener() {
        return mSelectedListener;
    }

    public void setSelection(int position) {
        if (inChoiceMode() && checkPosition(position) &&
                (mChoiceMode == ChoiceMode.MULTIPLE || position != mSelectedPosition)) {
            dispatchSelect(position, true);
        }
    }

    public int getSelection() {
        return mSelectedPosition;
    }

    public void setSelected(int position, boolean selected) {

    }

    public boolean isSelected(int position) {
        return false;
    }

    @LayoutRes
    public abstract int getLayoutResId(int viewType);

    public abstract void bindView(RvHolder holder, int position);


    private void dispatchSelect(int position, boolean selected) {
        if (mChoiceMode == ChoiceMode.SINGLE) {
            if (selected) {
                final int last = mSelectedPosition;
                mSelectedPosition = position;
                if (checkPosition(last)) {
                    dispatchSelect(last, false);
                }
                setSelected(mSelectedPosition, true);
                notifyItemChanged(mSelectedPosition);
            } else {
                setSelected(position, false);
                notifyItemChanged(position);
            }
        } else {
            setSelected(position, selected);
            notifyItemChanged(position);
        }
    }

    private boolean isSelectedByChoiceMode(int position) {
        if (mChoiceMode == ChoiceMode.SINGLE) {
            return mSelectedPosition == position;
        }
        return RvAdapter.this.isSelected(position);
    }

    private class SelectHelper extends OnRvItemClickListener {
        @Override
        public void onItemClick(RecyclerView.ViewHolder holder) {
            super.onItemClick(holder);
            if (inChoiceMode()) {
                final int position = holder.getAdapterPosition();
                if (mChoiceMode == ChoiceMode.MULTIPLE || mSelectedPosition != position) {
                    dispatchSelect(position, !isSelectedByChoiceMode(position));
                }
            }
        }
    }

    private boolean checkPosition(int position) {
        return position >= 0 && position < getItemCount();
    }

    private boolean inChoiceMode() {
        return mChoiceMode == ChoiceMode.SINGLE || mChoiceMode == ChoiceMode.MULTIPLE;
    }

    public interface OnItemSelectedChangedListener {

        void onItemSelectedChanged(int position, boolean selected);
    }


    @IntDef({ChoiceMode.NONE, ChoiceMode.SINGLE, ChoiceMode.MULTIPLE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ChoiceMode {
        /**
         * Does not indicate choices
         */
        int NONE = 0;

        /**
         * Allows up to one choice
         */
        int SINGLE = 1;

        /**
         * Allows multiple choices
         */
        int MULTIPLE = 2;
    }
}
