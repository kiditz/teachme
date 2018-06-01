package com.slerpio.teachme.adapter;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * Created by kiditz on 12/12/17.
 */

public abstract class AbstractRecyclerPagination extends RecyclerView.OnScrollListener {
    private LinearLayoutManager manager;

    public AbstractRecyclerPagination(LinearLayoutManager manager) {
        this.manager = manager;
    }



    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        int visibleItemCount = manager.getChildCount();
        int totalItemCount = manager.getItemCount();
        int firstVisibleItemPosition = manager.findFirstVisibleItemPosition();
        if (!isLoading() && !isLastPage()) {
            if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount  && firstVisibleItemPosition >= 0 && totalItemCount >= getTotalItemCount()) {
                loadMoreItems();
            }

        }
    }

    public abstract boolean isLoading();
    public abstract boolean isLastPage();
    public abstract int getTotalItemCount();
    public abstract void loadMoreItems();
}
