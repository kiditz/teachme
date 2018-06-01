package com.slerpio.teachme.adapter;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import com.slerpio.teachme.helper.TeachmeApi;
import com.slerpio.teachme.model.Domain;
import io.reactivex.Single;

import java.util.List;

public  class PaginationOnScrollListener extends AbstractRecyclerPagination{
    private boolean isLoading = false;
    private boolean isLastPage = false;
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

    public void setData(Single<Domain> data) {
        this.data = data;
        loadItems(currentPage);
    }

    @Override
    public void loadMoreItems() {
        this.currentPage = currentPage + 1;
        loadItems(this.currentPage);
    }

    public void loadItems(int page){
        this.isLoading = true;

        pageHandler.onLoad(page);

    }

    public void showResponse(Domain response){
        this.isLoading = false;
        if (TeachmeApi.ok(response)) {
            if(pageHandler != null){
                pageHandler.onSuccess(response);
            }
            if(response.containsKey("total_pages")){
                int total = response.getInt("total_pages");
                if (this.currentPage == total) {
                    isLastPage = true;
                }
            }
            this.items.addAll(TeachmeApi.payloads(response));
            if(items.size() < 0 || items.isEmpty()){
                if(pageHandler != null){
                    pageHandler.onEmpty(response);
                }
                //Make sure notifyDataSetChange is not called if empty
                return;
            }
            adapter.notifyDataSetChanged();
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
