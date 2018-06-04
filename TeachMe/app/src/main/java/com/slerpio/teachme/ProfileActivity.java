package com.slerpio.teachme;

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
import com.slerpio.teachme.adapter.ActivityAdapter;
import com.slerpio.teachme.helper.IntentUtils;
import com.slerpio.teachme.helper.image.CircleImageView;
import com.slerpio.teachme.model.Authority;
import com.slerpio.teachme.model.Domain;
import com.slerpio.teachme.model.User;
import com.slerpio.teachme.realm.service.UserRepository;
import com.slerpio.teachme.service.ImageService;

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
    UserRepository userService;
    @Inject
    ImageService imageService;
    @Inject
    SharedPreferences preferences;
    ActivityAdapter adapter;
    @BindView(R.id.profileImage)
    CircleImageView profileImage;
    @BindView(R.id.editProfile)
    Button editProfile;
    private List<Domain> activities = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);
        ((App) getApplication()).getNetOauthComponent().inject(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        User user = userService.findUser();
        fullname.setText(user.getFullname());
        username.setText("@" + user.getUsername());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new ActivityAdapter(this, activities);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(adapter);
        imageService.loadUserImage(profileImage, user.getUsername());
        for (int i = 0; i < 10; i++) {
            Domain activity = new Domain();
            activity.put("title", "Test" + i);
            activity.put("hours", i + "Jam lalu");
            activity.put("date", "2018-05-23");
            activity.put("message", "This is test " + i + " roger.");
            activities.add(activity);
        }
        adapter.notifyDataSetChanged();
        editProfile.setVisibility(userService.hasAuthority(Authority.EDIT_PROFILE) ? View.VISIBLE : View.GONE);
        editProfileClick();
    }

    public void editProfileClick() {
        RxView.clicks(editProfile).subscribe(view -> {
            IntentUtils.moveTo(this, EditProfileActivity.class);
        });
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


}
