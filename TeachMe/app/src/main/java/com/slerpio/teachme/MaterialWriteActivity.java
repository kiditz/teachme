package com.slerpio.teachme;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.slerpio.teachme.helper.BackPressed;
import com.slerpio.view.editor.RichEditor;

public class MaterialWriteActivity extends AppCompatActivity{
    @BindView(R.id.editor)
    RichEditor editor;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_material_write);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        editor.setPadding(10, 10, 10, 10);
        editor.setPlaceholder(getString(R.string.write_material_placeholder));
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
