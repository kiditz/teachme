package com.slerpio.teachme.adapter;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import com.slerpio.teachme.helper.TeachmeApi;
import com.slerpio.lib.core.Domain;
import io.reactivex.Single;

import java.util.List;

public  class PaginationOnScrollListener extends AbstractRecyclerPagination{
    private boolean isLoading;
    private boolean isLastPage;
    private int currentPage = 1;
    private int totalPage;
    private final List<Domain> items;
    private PageHandler pageHandler;
    private Single<Domain> data;

    private RecyclerView.Adapter<? extends RecyclerView.ViewHolder> adapter;
    public PaginationOnScrollListener(LinearLayoutManager manager, RecyclerView.Adapter<? extends RecyclerView.ViewHolder> adapter, List<Domain> items) {
        this(manager, adapter, items, 10);
    }

    public PaginationOnScrollListener(LinearLayoutManager manager, RecyclerView.Adapter<? extends RecyclerView.ViewHolder> adapter, List<Domain> items, int totalPage) {
        super(manager);
        this.items = items;
        this.totalPage = totalPage;
        this.adapter = adapter;
    }


    public void setLoading(boolean loading) {
        isLoading = loading;
    }

    public void setLastPage(boolean lastPage) {
        isLastPage = lastPage;
    }

    @Override
    public boolean isLoading() {
        return isLoading;
    }

    @Override
    public boolean isLastPage() {
        return isLastPage;
    }

    @Override
    public int getTotalItemCount() {
        return totalPage;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setPageHandler(PageHandler pageHandler) {
        this.pageHandler = pageHandler;
    }

    @Override
    public void loadMoreItems() {
        if(!isLastPage){
            currentPage = currentPage + 1;
            Log.d(getClass().getName(), "loadMoreItems: " + currentPage);
            loadItems(currentPage);
        }
    }

    public void loadItems(int page){
        pageHandler.onLoad(page);
    }

    public void showResponse(Domain response){
        if (TeachmeApi.ok(response)) {
            if(response.containsKey("total_pages")){
                int total = response.getInt("total_pages");
                Log.d(getClass().getName(), "showResponse: " + total);
                if (this.currentPage == total) {
                    this.isLastPage = true;
                    this.currentPage = 1;
                }
            }
            this.items.addAll(TeachmeApi.payloads(response));
            adapter.notifyDataSetChanged();
            if(items.isEmpty()){
                if(pageHandler != null){
                    pageHandler.onEmpty(response);
                }
                //Make sure notifyDataSetChange is not called if empty
            }
            if(pageHandler != null){
                pageHandler.onSuccess(response);
            }
            this.isLoading = false;
        }else{
            if(pageHandler != null){
                pageHandler.onFail(response);
            }
        }
    }

    public interface PageHandler{
        void onEmpty(Domain response);
        void onSuccess(Domain response);
        void onFail(Domain response);
        //void onError(Throwable throwable);
        void onLoad(int page);
    }

    public static abstract  class AbstractPageHandler implements PageHandler{

        @Override
        public void onEmpty(Domain response) {

        }

        @Override
        public void onSuccess(Domain response) {

        }

        @Override
        public void onFail(Domain response) {

        }

    }
}
