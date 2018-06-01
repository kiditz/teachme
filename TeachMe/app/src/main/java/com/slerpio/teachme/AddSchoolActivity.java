package com.slerpio.teachme;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.slerpio.teachme.helper.*;
import com.slerpio.teachme.helper.picker.DefaultCallback;
import com.slerpio.teachme.helper.picker.EasyImage;
import com.slerpio.teachme.model.Domain;
import com.slerpio.teachme.realm.service.UserRepository;
import com.slerpio.teachme.service.DocumentService;
import com.slerpio.teachme.service.ImageService;
import com.slerpio.teachme.service.SchoolService;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MultipartBody;
import retrofit2.Retrofit;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class AddSchoolActivity extends AppCompatActivity {
    private static final String TAG = AddSchoolActivity.class.getName();
    private static final int PICK_IMAGE = 1;

    @BindView(R.id.schoolName)
    EditText schoolName;
    @BindView(R.id.schoolAddress)
    EditText schoolAddress;
    @BindView(R.id.webAddress)
    EditText webAddress;
    @BindView(R.id.schoolDescription)
    EditText schoolDescription;

    @Inject
    ImageService imageService;
    @Inject
    UserRepository userService;
    @Inject
    Retrofit retrofit;
    @Inject
    Translations translation;

    //Wrapper
    @BindView(R.id.schoolNameWrapper)
    TextInputLayout schoolNameWrapper;
    @BindView(R.id.schoolAddressWrapper)
    TextInputLayout schoolAddressWrapper;
    @BindView(R.id.schoolDescriptionWrapper)
    TextInputLayout schoolDescriptionWrapper;
    @BindView(R.id.webAddressWrapper)
    TextInputLayout webAddressWrapper;
    @BindView(R.id.sendButton)
    Button sendButton;

    @BindView(R.id.logoBackground)
    ImageView logoBackground;

    private SchoolService schoolService;
    private DocumentService documentService;
    @NonNull
    CompositeDisposable disposable = new CompositeDisposable();

    private long documentId = -1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_school);
        ButterKnife.bind(this);
        ((App)getApplication()).getNetOauthComponent().inject(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        validateAddSchool();
        doAddSchool();
        schoolService = retrofit.create(SchoolService.class);
        documentService = retrofit.create(DocumentService.class);

    }

    private void validateAddSchool() {
        Observable<Boolean> schoolNameObserve =  RxTextView.textChanges(schoolName).map(text -> (text.length() == 0));
        Observable<Boolean> schoolAddressObserver =  RxTextView.textChanges(schoolAddress).map(text -> (text.length() == 0));
        Observable<Boolean> schoolDescriptionObserver =  RxTextView.textChanges(schoolDescription).map(text -> (text.length() == 0));

        schoolNameObserve.subscribe(isValid -> {
            schoolNameWrapper.setError(getString(R.string.required_value_school_name));
            schoolNameWrapper.setErrorEnabled(isValid);
        });

        schoolDescriptionObserver.subscribe(isValid -> {
            schoolDescriptionWrapper.setError(getString(R.string.required_value_description));
            schoolDescriptionWrapper.setErrorEnabled(isValid);
        });
        schoolAddressObserver.subscribe(isValid -> {
            schoolAddressWrapper.setError(getString(R.string.required_value_school_address));
            schoolAddressWrapper.setErrorEnabled(isValid);
        });
        Observable.combineLatest(schoolNameObserve, schoolDescriptionObserver, schoolAddressObserver, (schoolNameInvalid, schoolAddressInvalid, schoolDescriptionInvalid) -> !schoolNameInvalid && !schoolAddressInvalid && !schoolAddressInvalid).subscribe(valid -> sendButton.setEnabled(valid));
    }

    public void doAddSchool(){
        RxView.clicks(sendButton).subscribe(view -> {
            if(documentId == -1){
                Snackbar.make(findViewById(android.R.id.content), getString(R.string.required_value_image), Snackbar.LENGTH_LONG).show();
                return;
            }
            Domain input = new Domain();
            input.put("name", schoolName.getText().toString());
            input.put("description", schoolDescription.getText().toString());
            input.put("document_id", documentId);
            input.put("url", webAddress.getText().toString());
            input.put("user_id", userService.findUser().getUser_id());
            input.put("address", schoolAddress.getText().toString());
            disposable.add(schoolService.addSchool(input).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(response ->{
                if (TeachmeApi.ok(response)) {
                    Snackbar.make(findViewById(android.R.id.content), String.format(getString(R.string.add_school_success), schoolName.getText().toString()), Snackbar.LENGTH_INDEFINITE)
                            .setAction(android.R.string.ok, v -> {
                                IntentUtils.moveTo(AddSchoolActivity.this, MainActivity.class);
                                finish();
                            }).show();
                }else{
                    Snackbar.make(findViewById(android.R.id.content), TeachmeApi.getError(response), Snackbar.LENGTH_LONG).show();
                }
            }, error -> NetworkUtils.errorHandle(userService, translation, this, error)));
        });
    }
    @OnClick(R.id.chooseImage)
    public void chooseImage(){
        EasyImage.openChooserWithGallery(this, getString(R.string.choose_image), PICK_IMAGE);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposable.clear();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        EasyImage.handleActivityResult(requestCode, resultCode, data, this, new DefaultCallback() {
            @Override
            public void onImagePickerError(Exception e, EasyImage.ImageSource source, int type) {
                //Some error handling
            }

            @Override
            public void onImagesPicked(@android.support.annotation.NonNull List<File> imagesFiles, EasyImage.ImageSource source, int type) {
                try {
                    doUpload(imagesFiles.get(0), data);
                } catch (IOException e) {
                    Log.e(TAG, "onImagesPicked: ", e);
                }
            }
        });
    }

    private void doUpload(File file, Intent data) throws IOException {
        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
        logoBackground.setImageBitmap(bitmap);
        MultipartBody.Part part = MultipartUtils.createFile("file", data, AddSchoolActivity.this);
        disposable.add(documentService.addDocument(MultipartUtils.createValue("school"), part).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(response ->{
            if(TeachmeApi.ok(response)){
                Domain payload = TeachmeApi.payload(response);
                documentId = payload.getLong("id");
            }else{
                Snackbar.make(findViewById(android.R.id.content), TeachmeApi.getError(response), Snackbar.LENGTH_LONG).show();
            }
        }, error -> NetworkUtils.errorHandle(userService, translation, AddSchoolActivity.this, error)));
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
