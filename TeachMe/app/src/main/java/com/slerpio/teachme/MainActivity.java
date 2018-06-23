package com.slerpio.teachme;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.slerpio.teachme.fragment.ActivityFragment;
import com.slerpio.teachme.fragment.BlankFragment;
import com.slerpio.teachme.fragment.LearnFragment;
import com.slerpio.teachme.fragment.ViewPagerFragmentAdapter;
import com.slerpio.teachme.helper.*;
import com.slerpio.teachme.helper.image.CircleImageView;
import com.slerpio.teachme.model.Authority;
import com.slerpio.teachme.model.Domain;
import com.slerpio.teachme.model.User;
import com.slerpio.teachme.realm.service.UserRepository;
import com.slerpio.teachme.service.ImageService;
import com.slerpio.teachme.service.TeacherService;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

import javax.inject.Inject;

@SuppressWarnings("ALL")
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    private static final String TAG = MainActivity.class.getName();
    @BindView(R.id.toolbar)
    protected Toolbar toolbar;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.nav_view)
    NavigationView navigationView;
    @BindView(R.id.tabs)
    TabLayout tabLayout;

    @BindView(R.id.container)
    protected ViewPager container;
    protected TextView username;
    protected TextView fullname;
    protected CircleImageView profileImage;
    @Inject
    protected UserRepository userRepository;
    @Inject
    protected ImageService imageService;
    private TeacherService teacherService;
    @Inject
    protected Retrofit retrofit;
    @Inject
    Translations translations;
    @io.reactivex.annotations.NonNull
    private CompositeDisposable disposable = new CompositeDisposable();
    protected ViewPagerFragmentAdapter adapter;
    private UserUpdater updateUser;
    private int[] icons = new int[]{
            R.drawable.ic_graduation_cap, R.drawable.ic_dashboard, R.drawable.ic_task
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PermissionChecker.checkAndRequestPermissions(this);
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            if(bundle.containsKey("message")){
                Snackbar.make(findViewById(android.R.id.content), bundle.getString("message"), Snackbar.LENGTH_LONG).show();
            }
        }
        adapter = new ViewPagerFragmentAdapter(getSupportFragmentManager());
        ButterKnife.bind(this);
        ((App) getApplication()).getNetOauthComponent().inject(this);
        setSupportActionBar(toolbar);
        teacherService = retrofit.create(TeacherService.class);
        if (userRepository.findUser() == null) {
            moveToLogin();
        }
        updateUser = payload -> {
            Log.d(TAG, "onCreate: " + payload);
            User user = userRepository.updateUser(payload);
            this.fullname.setText(user.getFullname());

        };


        initDrawer();
        fillAccount();

    }

    private void initViewPager() {
        tabLayout.setupWithViewPager(container);
        container.setAdapter(adapter);
        adapter.clear();
        adapter.addFragment(new LearnFragment(), getString(R.string.learn));
        adapter.addFragment(new ActivityFragment(), getString(R.string.activity));
        adapter.addFragment(new BlankFragment(), getString(R.string.task));
        adapter.notifyDataSetChanged();
        setupCustomTabView();
        toolbar.setTitle(getString(R.string.dashboard));
    }


    private void setupCustomTabView() {
        for (int i = 0; i < adapter.getCount(); i++) {
            @SuppressLint("InflateParams")
            TextView tabText = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
            tabText.setText(adapter.getPageTitle(i));
            tabText.setCompoundDrawablesWithIntrinsicBounds(0, icons[i], 0, 0);
            if (tabLayout.getTabAt(i) != null) {
                ViewUtils.setViewDrawableColor(tabText, getResources().getColor(R.color.colorAccent));
                tabLayout.getTabAt(i).setCustomView(tabText);
            }
        }
        getSupportActionBar().setTitle(adapter.getTitle(0));

    }

    private void initDrawer() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        setMenuItemVisibility();
        View navHeaderView = navigationView.getHeaderView(0);
        this.username = navHeaderView.findViewById(R.id.username);
        this.fullname = navHeaderView.findViewById(R.id.fullname);
        this.profileImage = navHeaderView.findViewById(R.id.profileImage);

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void fillAccount() {

        User user = userRepository.findUser();
        if (user != null) {
            username.setText("@" + user.getUsername());
            disposable.add(teacherService.findTeacherByUsername(user.getUsername()).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(response -> {
                if (TeachmeApi.ok(response)) {
                    Domain payload = TeachmeApi.payload(response);
                    updateUser.update(payload);
                    initViewPager();

                }
            }, error -> {
                try {
                    fullname.setText(user.getFullname());
                } catch (Exception ignore) {
                }
                initViewPager();
                NetworkUtils.errorHandle(userRepository, translations, this, error);
            }));
            imageService.loadUserImage(profileImage, user.getUsername());
        } else {
            moveToLogin();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        return id == R.id.action_settings || super.onOptionsItemSelected(item);

    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        navigationView.setCheckedItem(id);
        switch (id) {
            case R.id.nav_logout:
                userRepository.removeUser();
                moveToLogin();
                return true;
            case R.id.nav_school:
                IntentUtils.moveTo(this, SchoolActivity.class);
                return true;
            case R.id.nav_profile:
                IntentUtils.moveTo(this, ProfileActivity.class);
                return true;
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void moveToLogin() {
        IntentUtils.moveTo(this, LoginActivity.class);
        finish();
    }


    public void setMenuItemVisibility() {
        navigationView.getMenu().findItem(R.id.nav_school).setVisible(userRepository.hasAuthority(Authority.ADD_SCHOOL));
        navigationView.getMenu().findItem(R.id.nav_profile).setVisible(userRepository.hasAuthority(Authority.VIEW_PROFILE));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposable.clear();
    }
}
