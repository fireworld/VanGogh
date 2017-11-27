package cc.colorcat.vangoghdemo.widget;

import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

/**
 * Created by cxx on 2017/11/27.
 * xx.ch@outlook.com
 */
public abstract class CheckableRvAdapter extends RecyclerView.Adapter<RvHolder> {
    /**
     * Normal list that does not indicate choices
     */
    public static final int CHOICE_MODE_NONE = 0;

    /**
     * The list allows up to one choice
     */
    public static final int CHOICE_MODE_SINGLE = 1;

    /**
     * The list allows multiple choices
     */
    public static final int CHOICE_MODE_MULTIPLE = 2;

    private int mChoiceMode = CHOICE_MODE_NONE;
    private int mSelectedPosition = AdapterView.INVALID_POSITION;
    private OnItemSelectedListener mSelectedListener = new OnItemSelectedListener() {
        @Override
        public void onItemUnselected(int position) {

        }

        @Override
        public void onItemSelected(int position) {

        }
    };

    @Override
    public final RvHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(getLayoutResId(viewType), parent, false);
        RvHolder holder = new RvHolder(itemView);
        holder.getHelper().setViewType(viewType).setPosition(holder.getAdapterPosition());
        return holder;
    }

    @Override
    public final void onBindViewHolder(RvHolder holder, int position) {
        RvHolder.Helper helper = holder.getHelper();
        final int viewType = holder.getItemViewType();
        helper.setViewType(viewType).setPosition(position);
        View itemView = helper.getRoot();
        if (itemView.isSelected()) {
            if (!isCheckedByChoiceMode(position)) {
                itemView.setSelected(false);
                mSelectedListener.onItemUnselected(position);
            }
        } else {
            if (isCheckedByChoiceMode(position)) {
                itemView.setSelected(true);
                mSelectedListener.onItemSelected(position);
            }
        }
        bindView(holder, position);
    }

    public RecyclerView.OnItemTouchListener newOnItemTouchListener() {
        return new OnRvItemClickListener() {
            @Override
            public void onItemClick(RecyclerView.ViewHolder holder) {
                super.onItemClick(holder);
                final int position = holder.getAdapterPosition();
                dispatchChecked(position, !isCheckedByChoiceMode(position));
            }
        };
    }

    public void setOnItemSelectedListener(OnItemSelectedListener listener) {
        mSelectedListener = listener;
    }

    public OnItemSelectedListener getOnItemSelectedListener() {
        return mSelectedListener;
    }

    public void setSelection(int position) {
        if (mChoiceMode == CHOICE_MODE_SINGLE || mChoiceMode == CHOICE_MODE_MULTIPLE) {
            if (position >= 0 && position < getItemCount()) {
                dispatchChecked(position, true);
            }
        }
    }

    public int getSelection() {
        return mSelectedPosition;
    }

    public void setChoiceMode(int choiceMode) {
        mChoiceMode = choiceMode;
    }


    private void dispatchChecked(int position, boolean checked) {
        if (mChoiceMode == CHOICE_MODE_SINGLE) {
            if (checked) {
                if (position != mSelectedPosition) {
                    if (mSelectedPosition >= 0 && mSelectedPosition < getItemCount()) {
                        dispatchChecked(mSelectedPosition, false);
                    }
                    mSelectedPosition = position;
                    setChecked(mSelectedPosition, true);
                    notifyItemChanged(mSelectedPosition);
                }
            } else {
                setChecked(position, false);
                notifyItemChanged(position);
            }
        } else {
            setChecked(position, checked);
            notifyItemChanged(position);
        }
    }

    private boolean isCheckedByChoiceMode(int position) {
        if (mChoiceMode == CHOICE_MODE_SINGLE) {
            return mSelectedPosition == position;
        }
        return isChecked(position);
    }

    @LayoutRes
    public abstract int getLayoutResId(int viewType);

    public abstract void bindView(RvHolder holder, int position);

    public void setChecked(int position, boolean checked) {
    }

    public boolean isChecked(int position) {
        return false;
    }

    public interface OnItemSelectedListener {

        void onItemUnselected(int position);

        void onItemSelected(int position);
    }
}
