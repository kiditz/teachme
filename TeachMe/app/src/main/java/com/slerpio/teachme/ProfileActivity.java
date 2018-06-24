package com.slerpio.teachme;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.jakewharton.rxbinding2.view.RxView;
import com.slerpio.teachme.adapter.AbstractRecyclerPagination;
import com.slerpio.teachme.adapter.ActivityAdapter;
import com.slerpio.teachme.helper.IntentUtils;
import com.slerpio.teachme.helper.NetworkUtils;
import com.slerpio.teachme.helper.TeachmeApi;
import com.slerpio.teachme.helper.Translations;
import com.slerpio.teachme.helper.image.CircleImageView;
import com.slerpio.teachme.model.Authority;
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

public class ProfileActivity extends AppCompatActivity {
    @BindView(R.id.recycler)
    RecyclerView recyclerView;
    @BindView(R.id.fullname)
    TextView fullname;
    @BindView(R.id.username)
    TextView username;
    @Inject
    Retrofit retrofit;
    @Inject
    UserRepository userService;
    @Inject
    ImageService imageService;
    @Inject
    SharedPreferences preferences;
    @Inject
    Translations translations;
    ActivityAdapter adapter;

    @BindView(R.id.profileImage)
    CircleImageView profileImage;
    @BindView(R.id.editProfile)
    Button editProfile;
    private MaterialService materialService;
    private ActivityService activityService;
    private CompositeDisposable disposable = new CompositeDisposable();
    private List<Domain> activities = new ArrayList<>();
    private User user;
    private boolean isLoading;
    private boolean isLastPage;
    private int currentPage = 1;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);
        ((App) getApplication()).getNetOauthComponent().inject(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        this.activityService = retrofit.create(ActivityService.class);
        this.materialService = retrofit.create(MaterialService.class);
        user = userService.findUser();
        if(user == null)
            return;

        fullname.setText(user.getFullname());
        username.setText("@" + user.getUsername());
        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new ActivityAdapter(this, activities);
        adapter.setImageService(imageService);
        adapter.setMaterialService(materialService);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(adapter);
        imageService.loadUserImage(profileImage, user.getUsername());
        activities.clear();
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
        editProfile.setVisibility(userService.hasAuthority(Authority.EDIT_PROFILE) ? View.VISIBLE : View.GONE);
        editProfileClick();
    }

    public void editProfileClick() {
        RxView.clicks(editProfile).subscribe(view -> IntentUtils.moveTo(this, EditProfileActivity.class));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getData(int page){

        Domain input = new Domain();
        input.put("user_id", user.getUser_id());
        input.put("page", page);
        input.put("size", 10);
        isLoading = true;
        disposable.add(activityService.getActivityByUserId(input).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(response ->{
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
        }, error -> NetworkUtils.errorHandle(userService, translations, this, error)));
    }
}
