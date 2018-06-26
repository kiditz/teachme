package com.slerpio.teachme.fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.slerpio.teachme.App;
import com.slerpio.teachme.R;
import com.slerpio.teachme.adapter.AbstractRecyclerPagination;
import com.slerpio.teachme.adapter.ActivityAdapter;
import com.slerpio.teachme.helper.NetworkUtils;
import com.slerpio.teachme.helper.TeachmeApi;
import com.slerpio.teachme.helper.Translations;
import com.slerpio.teachme.model.Domain;
import com.slerpio.teachme.model.User;
import com.slerpio.teachme.realm.service.UserRepository;
import com.slerpio.teachme.service.ActivityService;
import com.slerpio.teachme.service.ImageService;
import com.slerpio.teachme.service.MaterialService;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * Fragment for showing all activity
 * @author kiditz
 */
public class FragmentActivity extends Fragment {
    //private static final String TAG = FragmentActivity.class.getName();
    @BindView(R.id.recycler)
    RecyclerView recyclerView;
    @Inject
    UserRepository userRepository;
    @Inject
    Translations translations;
    @Inject
    Retrofit retrofit;

    @Inject
    ImageService imageService;
    ActivityAdapter adapter;
    ActivityService activityService;


    private CompositeDisposable disposable = new CompositeDisposable();
    private List<Domain> activities = new ArrayList<>();
    private User user;
    private MaterialService materialService;
    private boolean isLoading;
    private boolean isLastPage;
    private int currentPage = 1;
    public FragmentActivity() {
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((App)getActivity().getApplication()).getNetOauthComponent().inject(this);
        this.activityService = retrofit.create(ActivityService.class);
        this.materialService = retrofit.create(MaterialService.class);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_activity, container, false);
        this.user = userRepository.findUser();
        if(user == null)
            return v;
        ButterKnife.bind(this, v);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(manager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new ActivityAdapter(getActivity(), activities);
        adapter.setImageService(imageService);
        adapter.setMaterialService(materialService);
        adapter.setDisposable(disposable);
        adapter.setUser(user);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(adapter);
        this.activities.clear();
        getData(currentPage);
        recyclerView.addOnScrollListener(new AbstractRecyclerPagination(manager) {
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
                return TeachmeApi.SIZE;
            }

            @Override
            public void loadMoreItems() {
                currentPage = currentPage + 1;
                getData(currentPage);
            }
        });
        return v;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        disposable.clear();
    }
    private void getData(int page){

        Domain input = new Domain();
        input.put("user_id", user.getUser_id());
        input.put("page", page);
        input.put("size", 10);
        isLoading = true;
        disposable.add(activityService.getActivity(input).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(response ->{
            isLoading = false;
            if (TeachmeApi.ok(response)) {
                if (response.containsKey("total_pages")) {
                    int total = response.getInt("total_pages");
                    if (page == total) {
                        isLastPage = true;
                    }
                }
                activities.addAll(TeachmeApi.payloads(response));
                isLoading = false;
                adapter.notifyDataSetChanged();
            }
        }, error -> NetworkUtils.errorHandle(userRepository, translations, getActivity(), error)));
    }
}
