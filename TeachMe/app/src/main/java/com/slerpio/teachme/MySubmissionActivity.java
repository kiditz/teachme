package com.slerpio.teachme;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.slerpio.teachme.adapter.AbstractRecyclerPagination;
import com.slerpio.teachme.adapter.MySubmissionAdapter;
import com.slerpio.teachme.helper.*;
import com.slerpio.teachme.model.Domain;
import com.slerpio.teachme.model.User;
import com.slerpio.teachme.realm.service.UserRepository;
import com.slerpio.teachme.service.ImageService;
import com.slerpio.teachme.service.MaterialService;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import org.apache.commons.lang3.StringUtils;
import retrofit2.Retrofit;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class MySubmissionActivity extends AppCompatActivity {
    @BindView(R.id.recycler)
    RecyclerView recyclerView;
    @BindView(R.id.emptyLayout)
    LinearLayout emptyLayout;
    @BindView(R.id.searchView)
    MaterialSearchView searchView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.emptyText)
    TextView emptyText;
    @BindView(R.id.emptyButton)
    Button emptyButton;
    @Inject
    UserRepository userRepository;
    @Inject
    Translations translations;
    @Inject
    Retrofit retrofit;
    @Inject
    ImageService imageService;
    private User user;
    private List<Domain> materials = new ArrayList<>();
    private MaterialService materialService;
    private MySubmissionAdapter adapter;

    private CompositeDisposable disposable = new CompositeDisposable();
    private boolean isLoading;
    private boolean isLastPage;
    private int currentPage = 1;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_submission);
        ButterKnife.bind(this);
        ((App) getApplication()).getNetOauthComponent().inject(this);
        user = userRepository.findUser();
        if(user == null){
            return;
        }
        this.materialService = retrofit.create(MaterialService.class);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }


        searchView.setHint(getString(R.string.search_hint_words));
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                materials.clear();
                getData(currentPage, query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {

            }

            @Override
            public void onSearchViewClosed() {

            }
        });
        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new MySubmissionAdapter(this, this.materials);
        adapter.setImageService(imageService);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(adapter);

        this.materials.clear();
        getData(currentPage, StringUtils.EMPTY);
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
                getData(currentPage, StringUtils.EMPTY);
            }
        });
        emptyButton.setOnClickListener(v -> {
            IntentUtils.moveTo(this, AddMaterialActivity.class);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void getData(int page, String title){

        Domain input = new Domain();
        input.put("user_id", user.getUser_id());
        input.put("page", page);
        input.put("size", 10);
        input.put("title", title);
        isLoading = true;
        disposable.add(materialService.getMaterialByUser(input).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(response ->{
            isLoading = false;
            if (TeachmeApi.ok(response)) {
                if (response.containsKey("total_pages")) {
                    int total = response.getInt("total_pages");
                    if (page == total) {
                        isLastPage = true;
                    }
                }
                materials.addAll(TeachmeApi.payloads(response));
                if(materials.isEmpty()){
                    emptyLayout.setVisibility(View.VISIBLE);
                    emptyText.setText(R.string.no_material);
                    emptyButton.setText(R.string.create_material);
                }else{
                    emptyLayout.setVisibility(View.GONE);
                }

                isLoading = false;
                adapter.notifyDataSetChanged();
            }
        }, error -> NetworkUtils.errorHandle(userRepository, translations, this, error)));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_my_submission, menu);
        ViewUtils.setViewDrawableColor(menu, getResources().getColor(R.color.colorAccent));
        searchView.setMenuItem(menu.findItem(R.id.action_search_submission));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.action_add_material:
                IntentUtils.moveTo(this, AddMaterialActivity.class);
                return true;
        }

        return BackPressed.home(item, this);
    }
}
