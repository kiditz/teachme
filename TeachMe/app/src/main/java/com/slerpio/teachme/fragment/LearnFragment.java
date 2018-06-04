package com.slerpio.teachme.fragment;


import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.*;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.slerpio.teachme.App;
import com.slerpio.teachme.MaterialTypeActivity;
import com.slerpio.teachme.R;
import com.slerpio.teachme.adapter.LearnAdapter;
import com.slerpio.teachme.adapter.PaginationOnScrollListener;
import com.slerpio.teachme.helper.IntentUtils;
import com.slerpio.teachme.helper.NetworkUtils;
import com.slerpio.teachme.helper.TeachmeApi;
import com.slerpio.teachme.helper.Translations;
import com.slerpio.teachme.model.Domain;
import com.slerpio.teachme.realm.service.UserRepository;
import com.slerpio.teachme.service.ImageService;
import com.slerpio.teachme.service.MaterialService;
import io.reactivex.Single;
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
public class LearnFragment extends Fragment implements PaginationOnScrollListener.PageHandler{


    @BindView(R.id.recycler)
    protected RecyclerView recycler;

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
    private PaginationOnScrollListener pagination;

    public LearnFragment() {
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        ((App)getActivity().getApplication()).getNetOauthComponent().inject(this);
        this.disposable  = new CompositeDisposable();
        this.adapter = new LearnAdapter(getContext(), topics);
        this.materialService = retrofit.create(MaterialService.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_learn, container, false);
        ButterKnife.bind(this, v);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        recycler.setLayoutManager(manager);
        recycler.setNestedScrollingEnabled(false);
        recycler.setItemAnimator(new DefaultItemAnimator());
        adapter.set(userRepository, translations, materialService, disposable, imageService);
        recycler.setAdapter(adapter);

        pagination = new PaginationOnScrollListener(manager, adapter, topics);
        pagination.setPageHandler(this);

        recycler.addOnScrollListener(pagination);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        this.topics.clear();
        pagination.loadItems(1);
    }

    private Single<Domain> getData(int page){
        Domain input = new Domain();
        input.put("page", page);
        input.put("size", pagination.getTotalItemCount());
        try {
            input.put("level_id", userRepository.findUser().getLevel_id());
            return materialService.getMaterialTopicByLevel(input).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io());
        }catch (Exception ignore){
            return Single.just(new Domain().put("payload", new ArrayList<>()).put("total", 0).put("total_pages", 0));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        disposable.clear();
    }

    @Override
    public void onEmpty(Domain response) {

    }

    @Override
    public void onSuccess(Domain response) {
        //TODO: Save response realm into realm
    }

    @Override
    public void onFail(Domain response) {
        Snackbar.make(getView(), TeachmeApi.getError(response),Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onLoad(int page) {
        disposable.add(getData(page).subscribe(pagination::showResponse, error -> NetworkUtils.errorHandle(userRepository, translations, getActivity(), error)));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.learn_fragment_menu, menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_create_material){
            IntentUtils.moveTo(getActivity(), MaterialTypeActivity.class);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
