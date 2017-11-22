package cc.colorcat.vangoghdemo.internal;

import android.support.v7.widget.RecyclerView;

import cc.colorcat.vangogh.VanGogh;

/**
 * Created by cxx on 17-11-22.
 * xx.ch@outlook.com
 */
public class VanGoghScrollListener extends RecyclerView.OnScrollListener {
    private static final VanGoghScrollListener INSTANCE = new VanGoghScrollListener();

    public static VanGoghScrollListener get() {
        return INSTANCE;
    }


    private VanGoghScrollListener() {

    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
            VanGogh.with(recyclerView.getContext()).resume();
        } else {
            VanGogh.with(recyclerView.getContext()).pause();
        }
    }
}
