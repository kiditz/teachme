package com.slerpio.teachme;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.widget.ProgressBar;
import android.widget.TextView;
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
import org.apache.commons.lang3.StringUtils;
import retrofit2.Retrofit;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.List;

public class MaterialWriteActivity extends AppCompatActivity{
    private static final int RESULT_CODE_IMAGE = 1;
    private static final String TAG = MaterialWriteActivity.class.getName();
    @BindView(R.id.editor)
    RichEditor editor;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.topic)
    TextView topic;
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
    private Domain topicDomain;
    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_material_type_write);
        ButterKnife.bind(this);
        ((App)getApplication()).getNetOauthComponent().inject(this);
        this.documentService  = retrofit.create(DocumentService.class);
        progressBar.setVisibility(View.GONE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            title.setText(bundle.getString("title"));
            this.topicDomain = new Domain(getIntent().getStringExtra("topic"));
            this.topic.setText(topicDomain.getString("name"));
        }
        editor.getSettings().setJavaScriptEnabled(true);
        editor.setWebChromeClient(new WebChromeClient());
        editor.setPlaceholder(getString(R.string.write_material_placeholder));


        editor.setPadding(20, 20, 20, 20);
        editor.setOnTextChangeListener(text -> Log.d(TAG, text));
    }

    @OnClick(R.id.action_undo)
    void undo(){
        editor.undo();
    }

    @OnClick(R.id.action_redo)
    void redo(){
        editor.redo();
    }

    @OnClick(R.id.action_align_left)
    void alignLeft(){
        editor.setAlignLeft();
    }

    @OnClick(R.id.action_align_center)
    void alignCenter(){
        editor.setAlignCenter();
    }

    @OnClick(R.id.action_align_right)
    void alignRight(){
        editor.setAlignRight();
    }

    @OnClick(R.id.action_h1)
    void h1(){
        editor.setHeading(1);
    }

    @OnClick(R.id.action_h2)
    void h2(){
        editor.setHeading(2);
    }

    @OnClick(R.id.action_h3)
    void h3(){
        editor.setHeading(3);
    }

    @OnClick(R.id.action_bold)
    void bold(){
        editor.setBold();
    }

    @OnClick(R.id.action_Italic)
    void italic(){
        editor.setItalic();
    }

    @OnClick(R.id.action_indent)
    void indent(){
        editor.setIndent();
    }

    @OnClick(R.id.action_outdent)
    void outdent(){
        editor.setOutdent();
    }

    @OnClick(R.id.action_bulleted)
    void bullets(){
        editor.setBullets();
    }

    @OnClick(R.id.action_unordered_numbered)
    void numbers(){
        editor.setNumbers();
    }

    @OnClick(R.id.action_insert_image)
    void insertImage(){
        EasyImage.openChooserWithDocuments(this, "Pilih Gambar", RESULT_CODE_IMAGE);
    }

    @OnClick(R.id.action_erase)
    void erase(){
        editor.setHtml("");
    }

    @OnClick(R.id.action_block_quote)
    void blockquote(){
        editor.setBlockquote();
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
            return doUploadMaterial();
        }
        return BackPressed.home(item, this);
    }

    private boolean doUploadMaterial() {
        RequestBody directory = MultipartUtils.createValue("material_type_write");
        if(StringUtils.isBlank(editor.getHtml())){
            Snackbar.make(findViewById(android.R.id.content), getString(R.string.required_value_material), Snackbar.LENGTH_LONG).show();
            return false;
        }

        MultipartBody.Part part = MultipartUtils.createFileHtml("file", editor.getHtml());
        progressBar.setVisibility(View.VISIBLE);
        disposable.add(documentService.addDocument(directory, part).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(response ->{
            progressBar.setVisibility(View.GONE);
            if(TeachmeApi.ok(response)){
                Domain payload = TeachmeApi.payload(response);
                documentRepository.add(payload);
                Bundle bundle = new Bundle();
                bundle.putString("title", title.getText().toString());
                bundle.putString("topic", topicDomain.toString());
                bundle.putString("type", GlobalConstant.MATERIAL_TYPE_WRITE);
                bundle.putString("document", payload.toString());
                IntentUtils.moveTo(MaterialWriteActivity.this, MaterialPreviewActivity.class, bundle);
            }else{
                Snackbar.make(findViewById(android.R.id.content), TeachmeApi.getError(response), Snackbar.LENGTH_LONG).show();
            }
        }, error -> {
            progressBar.setVisibility(View.GONE);
            NetworkUtils.errorHandle(userRepository, translation, MaterialWriteActivity.this, error);
        }));
        return false;
    }

    private void doUploadImage(Intent data) throws IOException {
        MultipartBody.Part part = MultipartUtils.createFile("file", data, this);
        disposable.add(documentService.addDocument(MultipartUtils.createValue("material_type_write"), part, MultipartUtils.createValue("N")).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(response -> {
            if(TeachmeApi.ok(response)){
                Domain payload = TeachmeApi.payload(response);
                String imageUrl = getString(R.string.teach_me_url) + "teachme/get_free_document?id=" + payload.getLong("id");
                editor.insertImage(new URI(imageUrl), "");
            }else{
                Snackbar.make(findViewById(android.R.id.content), TeachmeApi.getError(response), Snackbar.LENGTH_LONG).show();
            }
        }, error -> NetworkUtils.errorHandle(userRepository, translation, this, error)));
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
            public void onImagesPicked(@android.support.annotation.NonNull List<File> imagesFiles, EasyImage.ImageSource source, int type) {
                try {
                    doUploadImage(data);
                } catch (IOException e) {
                    Log.e(TAG, "onImagesPicked: ", e);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposable.clear();
    }
}
