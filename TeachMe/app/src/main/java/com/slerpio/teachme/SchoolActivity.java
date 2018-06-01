package com.slerpio.teachme;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.slerpio.teachme.adapter.PaginationOnScrollListener;
import com.slerpio.teachme.adapter.SchoolAdapter;
import com.slerpio.teachme.helper.*;
import com.slerpio.teachme.model.Domain;
import com.slerpio.teachme.realm.service.SchoolRepository;
import com.slerpio.teachme.realm.service.UserRepository;
import com.slerpio.teachme.service.ImageService;
import com.slerpio.teachme.service.SchoolService;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class SchoolActivity extends AppCompatActivity implements PaginationOnScrollListener.PageHandler{
    public static final String TAG = SchoolActivity.class.getName();
    //Dagger 2
    @Inject
    ImageService imageService;
    @Inject
    Retrofit retrofit;
    @Inject
    UserRepository userRepository;
    @Inject
    SchoolRepository schoolRepository;
    @Inject
    Translations translations;
    SchoolService schoolService;
    //ButterKnife
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.search_view)
    MaterialSearchView searchView;
    @BindView(R.id.recycler)
    RecyclerView recycler;
    List<Domain> schools = new ArrayList<>();
    private String name = "";
    private PaginationOnScrollListener pagination;
    private  SchoolAdapter adapter;
    @NonNull
    private CompositeDisposable disposable = new CompositeDisposable();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_school);
        ((App)getApplication()).getNetOauthComponent().inject(this);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        initService();
        initRecyclerView();
        initSearchView();
    }

    private void initSearchView() {
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                schools.clear();
                SchoolActivity.this.name = query;
                pagination.loadItems(1);
                searchView.closeSearch();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    private void initService() {
        schoolService = retrofit.create(SchoolService.class);

    }

    private void initRecyclerView() {
        LinearLayoutManager manager = new LinearLayoutManager(this);

        adapter = new SchoolAdapter(this, schools);
        adapter.setImageService(imageService);
        recycler.setLayoutManager(manager);
        recycler.setAdapter(adapter);

        pagination = new PaginationOnScrollListener(manager, adapter, schools);
        pagination.setPageHandler(this);
        pagination.loadItems(1);
        recycler.addOnScrollListener(pagination);

    }

    private Single<Domain> getData(int page){
        Domain input = new Domain();
        input.put("name", name);
        input.put("page", page);
        input.put("size", pagination.getTotalItemCount());
        return schoolService.getSchoolByName(input).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io());
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_school, menu);
        imageService.setViewDrawableColor(menu, getResources().getColor(R.color.colorAccent));
        searchView.setMenuItem(menu.findItem(R.id.action_search));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_add_school){
            IntentUtils.moveTo(this, AddSchoolActivity.class);
            return true;
        }
        return BackPressed.home(item, this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposable.clear();
    }

    @Override
    public void onEmpty(Domain response) {

    }

    @Override
    public void onSuccess(Domain response) {
        schoolRepository.add(TeachmeApi.payloads(response).toString());
    }

    @Override
    public void onFail(Domain response) {
        Snackbar.make(findViewById(android.R.id.content), TeachmeApi.getError(response),Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onLoad(int page) {
        disposable.add(getData(page).subscribe(pagination::showResponse, error ->{
            NetworkUtils.errorHandle(userRepository, translations, SchoolActivity.this, error);
            schools.clear();
            schools.addAll(schoolRepository.findAll());
            adapter.notifyDataSetChanged();
        }));
    }


}
