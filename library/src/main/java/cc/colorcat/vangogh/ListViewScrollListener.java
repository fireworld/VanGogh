package cc.colorcat.vangogh;

import android.widget.AbsListView;
import android.widget.ListView;

/**
 * Created by cxx on 17-12-7.
 * xx.ch@outlook.com
 */
class ListViewScrollListener implements ListView.OnScrollListener {
    private VanGogh vanGogh;
    private ListView.OnScrollListener listener;

    ListViewScrollListener(VanGogh vanGogh, ListView.OnScrollListener listener) {
        this.vanGogh = vanGogh;
        this.listener = listener;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollState == ListView.OnScrollListener.SCROLL_STATE_IDLE) {
            vanGogh.resume();
        } else {
            vanGogh.pause();
        }
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
}
