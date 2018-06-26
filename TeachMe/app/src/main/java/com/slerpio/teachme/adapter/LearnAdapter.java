package com.slerpio.teachme.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.support.annotation.NonNull;
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
import com.slerpio.teachme.helper.TeachmeApi;
import com.slerpio.teachme.helper.Translations;
import com.slerpio.teachme.model.Domain;
import com.slerpio.teachme.realm.service.UserRepository;
import com.slerpio.teachme.service.ImageService;
import com.slerpio.teachme.service.MaterialService;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

import java.util.ArrayList;
import java.util.List;

public class LearnAdapter extends RecyclerView.Adapter<LearnAdapter.LearnViewHolder> {
    private final Activity context;
    private List<Domain> topics;
    RecyclerView.RecycledViewPool viewPool;
    private Translations translations;
    private MaterialService materialService;
    private CompositeDisposable disposable;
    private UserRepository userRepository;
    private ImageService imageService;

    public LearnAdapter(final Activity context, List<Domain> topics) {
        this.context = context;
        this.topics = topics;
        this.viewPool = new RecyclerView.RecycledViewPool();

    }

    public void set(UserRepository repository, Translations translations, MaterialService materialService, CompositeDisposable disposable, ImageService imageService) {
        this.translations = translations;
        this.imageService = imageService;
        this.userRepository = repository;
        this.materialService = materialService;
        this.disposable = disposable;
    }

    @NonNull
    @Override
    public LearnViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.learn_adapter, parent, false);

        return new LearnViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull LearnViewHolder holder, @SuppressLint("RecyclerView") int position) {
        final Domain topic = topics.get(position);
        final MaterialAdapter adapter = new MaterialAdapter(context, holder.materialList);
        adapter.setMaterialService(materialService);
        adapter.setDisposable(disposable);
        adapter.setUser(userRepository.findUser());
        final LinearLayoutManager manager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        adapter.setImageService(this.imageService);
        holder.topicName.setText(topic.getString("name"));
        holder.containerBody.setRecycledViewPool(viewPool);
        holder.containerBody.setLayoutManager(manager);
        holder.containerBody.setAdapter(adapter);
        holder.noMaterial.setVisibility(View.GONE);
        holder.materialList.clear();
        getData(holder.currentPage, topic, holder, adapter, position);
        holder.containerBody.addOnScrollListener(new AbstractRecyclerPagination(manager) {
            @Override
            public boolean isLoading() {
                return holder.isLoading;
            }

            @Override
            public boolean isLastPage() {
                return holder.isLastPage;
            }

            @Override
            public int getTotalItemCount() {
                return TeachmeApi.SIZE;
            }

            @Override
            public void loadMoreItems() {
                holder.currentPage = holder.currentPage + 1;
                getData(holder.currentPage, topic, holder, adapter, position);
            }
        });


    }

    private void getData(int page, Domain topic, LearnViewHolder holder, MaterialAdapter adapter, final int position) {
        Domain input = new Domain();
        input.put("topic_id", topic.getLong("id"));
        input.put("page", page);
        input.put("size", TeachmeApi.SIZE);
        holder.isLoading = true;
        holder.isLastPage = false;
        disposable.add(materialService.getMaterialByTopic(input).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(response -> {
            if (TeachmeApi.ok(response)) {
                if (response.containsKey("total_pages")) {
                    int total = response.getInt("total_pages");
                    if (page == total) {
                        holder.isLastPage = true;
                    }
                }
                holder.materialList.addAll(TeachmeApi.payloads(response));
                if(holder.materialList.isEmpty()){
                    try {
                        topics.remove(position);
                        notifyItemRemoved(position);
                    }catch (Exception ignore){

                    }
                }
                holder.isLoading = false;
                adapter.notifyDataSetChanged();
            }
        }, error -> NetworkUtils.errorHandle(userRepository, translations, context, error)));

    }

    @Override
    public int getItemCount() {
        return topics.size();
    }


    class LearnViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.topicName)
        TextView topicName;
        @BindView(R.id.noMaterial)
        TextView noMaterial;
        @BindView(R.id.containerBody)
        RecyclerView containerBody;
        private List<Domain> materialList = new ArrayList<>();
        boolean isLoading = false;
        boolean isLastPage = false;
        int currentPage = 1;

        public LearnViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
