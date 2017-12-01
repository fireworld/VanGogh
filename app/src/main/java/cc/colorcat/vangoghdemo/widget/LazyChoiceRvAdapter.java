package cc.colorcat.vangoghdemo.widget;

import android.support.v7.widget.RecyclerView;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by cxx on 2017/11/30.
 * xx.ch@outlook.com
 */
public abstract class LazyChoiceRvAdapter extends ChoiceRvAdapter {
    private List<Boolean> mRecord = new LinkedList<>();

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        registerAdapterDataObserver(mObserver);
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        unregisterAdapterDataObserver(mObserver);
    }

    @Override
    protected boolean isSelected(int position) {
        return super.isSelected(position) || mRecord.get(position);
    }

    @Override
    protected void updateItem(int position, boolean selected) {
        super.updateItem(position, selected);
        mRecord.set(position, selected);
    }

    private RecyclerView.AdapterDataObserver mObserver = new RecyclerView.AdapterDataObserver() {
        @Override
        public void onChanged() {
            super.onChanged();
            mRecord.clear();
            mRecord.addAll(create(Boolean.FALSE, getItemCount()));
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            super.onItemRangeChanged(positionStart, itemCount);
            for (int i = positionStart, end = positionStart + itemCount; i < end; i++) {
                mRecord.set(i, Boolean.FALSE);
            }
        }

//        @Override
//        public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
//            super.onItemRangeChanged(positionStart, itemCount, payload);
//        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            super.onItemRangeInserted(positionStart, itemCount);
            mRecord.addAll(positionStart, create(Boolean.FALSE, itemCount));
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            super.onItemRangeRemoved(positionStart, itemCount);
            removeRange(positionStart, itemCount);
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            super.onItemRangeMoved(fromPosition, toPosition, itemCount);
            List<Boolean> subList = mRecord.subList(fromPosition, fromPosition + itemCount);
            removeRange(fromPosition, itemCount);
            mRecord.addAll(toPosition, subList);
        }

        private List<Boolean> create(Boolean value, int size) {
            Boolean[] booleans = new Boolean[size];
            Arrays.fill(booleans, value);
            return Arrays.asList(booleans);
        }

        private void removeRange(int start, int count) {
            for (int i = start, end = start + count; i < end; i++) {
                mRecord.remove(i);
            }
        }
    };
}
