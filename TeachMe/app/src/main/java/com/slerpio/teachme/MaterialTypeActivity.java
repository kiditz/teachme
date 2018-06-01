package com.slerpio.teachme;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.slerpio.teachme.adapter.MaterialTypeAdapter;
import com.slerpio.teachme.helper.BackPressed;
import com.slerpio.teachme.model.Domain;

import java.util.ArrayList;
import java.util.List;

public class MaterialTypeActivity extends AppCompatActivity {
    @BindView(R.id.recycler)
    protected RecyclerView recycler;
    private MaterialTypeAdapter adapter;
    private List<Domain> typeList = new ArrayList<>();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_material_type);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        adapter = new MaterialTypeAdapter(this, typeList);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        recycler.setLayoutManager(gridLayoutManager);
        recycler.setItemAnimator(new DefaultItemAnimator());
        recycler.setAdapter(adapter);
        fillMaterialType();


    }

    private void fillMaterialType() {
        typeList.add(new Domain().put("name", "Tulis").put("resourceId", R.mipmap.material_type_write));
        typeList.add(new Domain().put("name", "Video").put("resourceId", R.mipmap.material_type_video));
        typeList.add(new Domain().put("name", "PDF").put("resourceId", R.mipmap.material_type_pdf));
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return BackPressed.home(item, this);
    }
}
