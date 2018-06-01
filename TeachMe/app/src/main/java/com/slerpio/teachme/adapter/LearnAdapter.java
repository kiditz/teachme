package com.slerpio.teachme.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.slerpio.teachme.R;
import com.slerpio.teachme.helper.NetworkUtils;
import com.slerpio.teachme.helper.Translations;
import com.slerpio.teachme.model.Domain;
import com.slerpio.teachme.realm.service.UserRepository;
import com.slerpio.teachme.service.ImageService;
import com.slerpio.teachme.service.MaterialService;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

import java.util.ArrayList;
import java.util.List;

public class LearnAdapter extends RecyclerView.Adapter<LearnAdapter.LearnViewHolder>{
    private final Context context;
    private List<Domain> topics;
    RecyclerView.RecycledViewPool viewPool;
    private Translations translations;
    private MaterialService materialService;
    private CompositeDisposable disposable;
    private UserRepository userRepository;
    private ImageService imageService;
    private PaginationOnScrollListener pagination;
    public LearnAdapter(final Context context, List<Domain> topics) {
        this.context = context;
        this.topics = topics;
        this.viewPool = new RecyclerView.RecycledViewPool();

    }

    public void set(UserRepository repository, Translations translations, MaterialService materialService, CompositeDisposable disposable, ImageService imageService){
        this.translations = translations;
        this.imageService = imageService;
        this.userRepository = repository;
        this.materialService = materialService;
        this.disposable = disposable;
    }

    @Override
    public LearnViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate( R.layout.learn_adapter, parent, false);

        return new LearnViewHolder(v);
    }

    @Override
    public void onBindViewHolder(LearnViewHolder holder, int position) {
        Domain topic = topics.get(position);
        holder.topicName.setText(topic.getString("name"));
        holder.containerBody.setRecycledViewPool(viewPool);
        LinearLayoutManager manager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        holder.containerBody.setLayoutManager(manager);
        holder.containerBody.setAdapter(holder.adapter);
        holder.noMaterial.setVisibility(View.GONE);
        this.pagination = new PaginationOnScrollListener(manager, holder.adapter, holder.materialList);
        this.pagination.setPageHandler(new PaginationOnScrollListener.AbstractPageHandler() {
            @Override
            public void onLoad(int page) {
                disposable.add(getData(page, topic).subscribe(pagination::showResponse, error -> NetworkUtils.errorHandle(userRepository, translations, (Activity) context, error)));
            }

            @Override
            public void onEmpty(Domain response) {
                holder.noMaterial.setVisibility(View.VISIBLE);
            }
        });
        holder.materialList.clear();
        this.pagination.loadItems(1);
        holder.containerBody.addOnScrollListener(pagination);

    }
    private Single<Domain> getData(int page, Domain topic){
        Domain input = new Domain();
        input.put("topic_id", topic.getLong("id"));
        input.put("page", page);
        input.put("size", pagination.getTotalItemCount());
        return materialService.getMaterialByTopicId(input).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io());
    }

    @Override
    public int getItemCount() {
        return topics.size();
    }


    class LearnViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.topicName)
        TextView topicName;
        @BindView(R.id.noMaterial)
        TextView noMaterial;
        @BindView(R.id.containerBody)
        RecyclerView containerBody;
        private MaterialAdapter adapter;
        private List<Domain> materialList = new ArrayList<>();
        public LearnViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            adapter = new MaterialAdapter(context, materialList);
            adapter.setImageService(imageService);
        }
    }
}
