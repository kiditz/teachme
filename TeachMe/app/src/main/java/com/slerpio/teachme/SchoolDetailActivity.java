package com.slerpio.teachme;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.slerpio.teachme.fragment.SchoolDetailFragment;
import com.slerpio.teachme.fragment.ViewPagerFragmentAdapter;
import com.slerpio.teachme.helper.BackPressed;
import com.slerpio.lib.core.Domain;
import com.slerpio.teachme.service.ImageService;

import javax.inject.Inject;

public class SchoolDetailActivity extends AppCompatActivity {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.schoolImage)
    ImageView schoolImage;
    @BindView(R.id.tabs)
    TabLayout tabs;
    @BindView(R.id.viewPager)
    ViewPager viewPager;
    ViewPagerFragmentAdapter adapter;
    @Inject
    ImageService imageService;

    private Domain school;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_school_detail);
        ButterKnife.bind(this);
        ((App)getApplication()).getNetOauthComponent().inject(this);
        Bundle bundle = getIntent().getExtras();
        this.school = new Domain(bundle.getString("school"));
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(school.getString("name"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        imageService.loadDocument(schoolImage, school.getLong("document_id"));
        setupFragment();
    }

    private void setupFragment() {
        adapter = new ViewPagerFragmentAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        tabs.setupWithViewPager(viewPager);
        SchoolDetailFragment schoolDetailFragment = new SchoolDetailFragment();
        schoolDetailFragment.setArguments(getIntent().getExtras());
        adapter.addFragment(schoolDetailFragment, "Detail");
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return BackPressed.home(item, this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
