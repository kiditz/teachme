package com.slerpio.teachme.fragment;


import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.jakewharton.rxbinding2.view.RxView;
import com.slerpio.teachme.AddMaterialActivity;
import com.slerpio.teachme.App;
import com.slerpio.teachme.R;
import com.slerpio.teachme.adapter.AbstractRecyclerPagination;
import com.slerpio.teachme.adapter.LearnAdapter;
import com.slerpio.teachme.helper.IntentUtils;
import com.slerpio.teachme.helper.NetworkUtils;
import com.slerpio.teachme.helper.TeachmeApi;
import com.slerpio.teachme.helper.Translations;
import com.slerpio.teachme.model.Domain;
import com.slerpio.teachme.model.User;
import com.slerpio.teachme.realm.service.UserRepository;
import com.slerpio.teachme.service.ImageService;
import com.slerpio.teachme.service.MaterialService;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class LearnFragment extends Fragment {


    @BindView(R.id.recycler)
    protected RecyclerView recycler;
    @BindView(R.id.createMaterial)
    protected FloatingActionButton createMaterial;
    @Inject
    protected Retrofit retrofit;
    @Inject
    protected UserRepository userRepository;
    @Inject
    protected Translations translations;

    @Inject
    ImageService imageService;
    private MaterialService materialService;
    private List<Domain> topics = new ArrayList<>();
    private LearnAdapter adapter;
    @NonNull
    private CompositeDisposable disposable;
    boolean isLoading = false;
    boolean isLastPage = false;
    int currentPage = 1;

    public LearnFragment() {
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((App)getActivity().getApplication()).getNetOauthComponent().inject(this);
        this.disposable  = new CompositeDisposable();
        this.adapter = new LearnAdapter(getActivity(), topics);
        this.materialService = retrofit.create(MaterialService.class);
    }

    @Override
    public View onCreateView(@android.support.annotation.NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_learn, container, false);
        ButterKnife.bind(this, v);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        recycler.setLayoutManager(manager);
        recycler.setNestedScrollingEnabled(false);
        recycler.setItemAnimator(new DefaultItemAnimator());
        adapter.set(userRepository, translations, materialService, disposable, imageService);
        recycler.setAdapter(adapter);

        this.topics.clear();
        getData(currentPage);
        recycler.addOnScrollListener(new AbstractRecyclerPagination(manager) {
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
                return 10;
            }

            @Override
            public void loadMoreItems() {
                currentPage = currentPage + 1;
                getData(currentPage);
            }
        });
        RxView.clicks(createMaterial).subscribe(view -> IntentUtils.moveTo(getActivity(), AddMaterialActivity.class));
        RxView.longClicks(createMaterial).subscribe(view -> Snackbar.make(v, R.string.title_add_material, Snackbar.LENGTH_LONG).show());
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void getData(int page) {
        User user = userRepository.findUser();
        if(user == null)
            return;
        Domain input = new Domain();
        input.put("page", page);
        input.put("size", 10);
        input.put("level_id", user.getLevel_id());
        input.put("user_id", user.getUser_id());
        input.put("name", "");
        isLoading = true;
        isLastPage = false;
        disposable.add(materialService.getMaterialTopic(input).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(response -> {
            if (TeachmeApi.ok(response)) {
                if (response.containsKey("total_pages")) {
                    int total = response.getInt("total_pages");
                    if (currentPage == total) {
                        isLastPage = true;
                    }
                }
                topics.addAll(TeachmeApi.payloads(response));
                isLoading = false;
                adapter.notifyDataSetChanged();
            }
        }, error-> NetworkUtils.errorHandle(userRepository, translations, getActivity(), error)));
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        disposable.clear();
    }
}
