package cc.colorcat.vangoghdemo.internal;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.widget.AbsListView;
import android.widget.ListView;

import cc.colorcat.vangogh.VanGogh;

/**
 * Created by cxx on 17-11-22.
 * xx.ch@outlook.com
 */
public class VanGoghScrollListener extends RecyclerView.OnScrollListener implements ListView.OnScrollListener {
    private static final VanGoghScrollListener INSTANCE = new VanGoghScrollListener(null);

    public static VanGoghScrollListener get() {
        return INSTANCE;
    }

    public static VanGoghScrollListener newVanGoghScrollListener(ListView.OnScrollListener listener) {
        return new VanGoghScrollListener(listener);
    }


    private ListView.OnScrollListener listener;

    private VanGoghScrollListener(ListView.OnScrollListener listener) {
        this.listener = listener;
    }

    // RecyclerView
    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
        updateVanGoghStatus(recyclerView.getContext(), newState == RecyclerView.SCROLL_STATE_IDLE);
    }

    // ListView
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        updateVanGoghStatus(view.getContext(), scrollState == ListView.OnScrollListener.SCROLL_STATE_IDLE);
        if (listener != null) {
            listener.onScrollStateChanged(view, scrollState);
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (listener != null) {
            listener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
        }
    }

    private void updateVanGoghStatus(Context context, boolean idle) {
        if (idle) {
            VanGogh.with(context).resume();
        } else {
            VanGogh.with(context).pause();
        }
    }
}
