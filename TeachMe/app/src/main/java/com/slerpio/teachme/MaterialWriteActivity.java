package com.slerpio.teachme;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.slerpio.teachme.helper.*;
import com.slerpio.teachme.helper.picker.DefaultCallback;
import com.slerpio.teachme.helper.picker.EasyImage;
import com.slerpio.teachme.model.Domain;
import com.slerpio.teachme.realm.service.DocumentRepository;
import com.slerpio.teachme.realm.service.UserRepository;
import com.slerpio.teachme.service.DocumentService;
import com.slerpio.view.editor.RichEditor;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Retrofit;

import javax.inject.Inject;
import java.io.File;
import java.util.List;

public class MaterialWriteActivity extends AppCompatActivity{
    private static final int RESULT_CODE_IMAGE = 1;
    @BindView(R.id.editor)
    RichEditor editor;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @Inject
    Retrofit retrofit;
    @Inject
    Translations translation;
    @Inject
    UserRepository userRepository;
    @Inject
    DocumentRepository documentRepository;
    private DocumentService documentService;

    @NonNull
    private CompositeDisposable disposable = new CompositeDisposable();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_material_write);
        ButterKnife.bind(this);
        ((App)getApplication()).getNetOauthComponent().inject(this);
        this.documentService  = retrofit.create(DocumentService.class);
        progressBar.setVisibility(View.GONE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        editor.setPadding(10, 10, 10, 10);
        editor.setPlaceholder(getString(R.string.write_material_placeholder));
    }

    @OnClick(R.id.action_undo)
    public void undo(){
        editor.undo();
    }

    @OnClick(R.id.action_redo)
    public void redo(){
        editor.redo();
    }

    @OnClick(R.id.action_align_left)
    public void alignLeft(){
        editor.setAlignLeft();
    }

    @OnClick(R.id.action_align_center)
    public void alignCenter(){
        editor.setAlignCenter();
    }

    @OnClick(R.id.action_align_right)
    public void alignRight(){
        editor.setAlignRight();
    }

    @OnClick(R.id.action_h1)
    public void h1(){
        editor.setHeading(1);
    }

    @OnClick(R.id.action_h2)
    public void h2(){
        editor.setHeading(2);
    }

    @OnClick(R.id.action_h3)
    public void h3(){
        editor.setHeading(3);
    }

    @OnClick(R.id.action_bold)
    public void bold(){
        editor.setBold();
    }

    @OnClick(R.id.action_Italic)
    public void italic(){
        editor.setItalic();
    }

    @OnClick(R.id.action_indent)
    public void indent(){
        editor.setIndent();
    }

    @OnClick(R.id.action_outdent)
    public void outdent(){
        editor.setOutdent();
    }

    @OnClick(R.id.action_bulleted)
    public void bullets(){
        editor.setBullets();
    }

    @OnClick(R.id.action_unordered_numbered)
    public void numbers(){
        editor.setNumbers();
    }

    @OnClick(R.id.action_insert_image)
    public void insertImage(){
        EasyImage.openChooserWithGallery(this, "Pilih Gambar", RESULT_CODE_IMAGE);
    }

    @OnClick(R.id.action_erase)
    public void erase(){
        editor.setHtml("");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_done, menu);
        ViewUtils.setViewDrawableColor(menu, getResources().getColor(R.color.colorAccent));
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_check){
            doUploadMaterial();
            return true;
        }
        return BackPressed.home(item, this);
    }

    private void doUploadMaterial() {
        RequestBody directory = MultipartUtils.createValue("material_type_write");
        MultipartBody.Part part = MultipartUtils.createFileHtml("file", editor.getHtml());
        progressBar.setVisibility(View.VISIBLE);
        disposable.add(documentService.addDocument(directory, part).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(response ->{
            progressBar.setVisibility(View.GONE);
            if(TeachmeApi.ok(response)){
                Domain payload = TeachmeApi.payload(response);
                documentRepository.add(payload);
            }else{
                Snackbar.make(findViewById(android.R.id.content), TeachmeApi.getError(response), Snackbar.LENGTH_LONG).show();
            }
        }, error -> {
            progressBar.setVisibility(View.GONE);
            NetworkUtils.errorHandle(userRepository, translation, MaterialWriteActivity.this, error);
        }));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        EasyImage.handleActivityResult(requestCode, resultCode, data, this, new DefaultCallback() {
            @Override
            public void onImagePickerError(Exception e, EasyImage.ImageSource source, int type) {
                //TODO: Some error handling
            }

            @Override
            public void onImagesPicked(List<File> imagesFiles, EasyImage.ImageSource source, int type) {
                //TODO: Handle the images
            }
        });
    }


}
