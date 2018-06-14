package com.slerpio.teachme;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.slerpio.teachme.adapter.MaterialTypeAdapter;
import com.slerpio.teachme.helper.*;
import com.slerpio.teachme.helper.picker.Constants;
import com.slerpio.teachme.model.Domain;
import com.slerpio.teachme.realm.service.DocumentRepository;
import com.slerpio.teachme.realm.service.UserRepository;
import com.slerpio.teachme.service.DocumentService;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Retrofit;

import javax.inject.Inject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.slerpio.teachme.helper.GlobalConstant.*;
public class MaterialTypeActivity extends AppCompatActivity {
    @BindView(R.id.recycler)
    protected RecyclerView recycler;
    @BindView(R.id.progressBar)
    protected ProgressBar progressBar;
    @BindView(R.id.toolbar)
    protected Toolbar toolbar;

    private MaterialTypeAdapter adapter;
    private List<Domain> typeList = new ArrayList<>();
    private DocumentService documentService;

    @Inject
    protected Retrofit retrofit;
    @Inject
    protected Translations translation;
    @Inject
    protected UserRepository userRepository;
    @Inject
    protected DocumentRepository documentRepository;

    @NonNull
    private CompositeDisposable disposable = new CompositeDisposable();
    private MenuItem actionCheck;
    private Bundle bundle = new Bundle();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_material_type);
        ButterKnife.bind(this);
        ((App)getApplication()).getNetOauthComponent().inject(this);
        this.documentService = retrofit.create(DocumentService.class);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        adapter = new MaterialTypeAdapter(this, typeList);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        recycler.setLayoutManager(gridLayoutManager);
        recycler.setItemAnimator(new DefaultItemAnimator());
        recycler.setAdapter(adapter);
        fillMaterialType();
    }

    private void fillMaterialType() {
        // For make sure this only have 3 data
        typeList.clear();
        // Add new material type
        typeList.add(new Domain().put("name", getString(R.string.material_type_write)).put("resourceId", R.mipmap.material_type_write).put("type", MATERIAL_TYPE_WRITE));
        typeList.add(new Domain().put("name", getString(R.string.material_type_video)).put("resourceId", R.mipmap.material_type_video).put("type", MATERIAL_TYPE_VIDEO));
        //typeList.add(new Domain().put("name", getString(R.string.material_type_pdf)).put("resourceId", R.mipmap.material_type_pdf).put("type", MATERIAL_TYPE_PDF));
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_check){

            IntentUtils.moveTo(MaterialTypeActivity.this, AddMaterialActivity.class, bundle);
        }
        return BackPressed.home(item, this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Constants.RequestCodes.PICK_PDF_FROM_DOCUMENTS){
            if(resultCode == Activity.RESULT_OK){
                doUploadMaterial(data);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_done, menu);
        ViewUtils.setViewDrawableColor(menu, getResources().getColor(R.color.colorAccent));
        this.actionCheck = menu.findItem(R.id.action_check).setVisible(false);
        return true;
    }

    private void doUploadMaterial(Intent data) {
        try {
            RequestBody directory = MultipartUtils.createValue("material_type_pdf");
            MultipartBody.Part part = MultipartUtils.createFile("file", data, this);
            progressBar.setVisibility(View.VISIBLE);
            disposable.add(documentService.addDocument(directory, part).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(response ->{
                progressBar.setVisibility(View.GONE);
                if(TeachmeApi.ok(response)){
                    Domain payload = TeachmeApi.payload(response);
                    documentRepository.add(payload);
                    bundle.putString("type", GlobalConstant.MATERIAL_TYPE_PDF);
                    bundle.putString("document", payload.toString());
                    this.actionCheck.setVisible(true);
                }else{
                    Snackbar.make(findViewById(android.R.id.content), TeachmeApi.getError(response), Snackbar.LENGTH_LONG).show();
                }
            }, error -> {
                progressBar.setVisibility(View.GONE);
                NetworkUtils.errorHandle(userRepository, translation, this, error);
            }));
        }catch (IOException ignore){

        }
    }
}
