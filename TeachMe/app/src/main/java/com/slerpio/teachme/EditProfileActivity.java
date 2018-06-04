package com.slerpio.teachme;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.slerpio.teachme.helper.*;
import com.slerpio.teachme.helper.image.CircleImageView;
import com.slerpio.teachme.model.Domain;
import com.slerpio.teachme.model.User;
import com.slerpio.teachme.realm.service.UserRepository;
import com.slerpio.teachme.service.AccountService;
import com.slerpio.teachme.service.ImageService;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

import javax.inject.Inject;
import java.util.regex.Pattern;

public class EditProfileActivity extends AppCompatActivity {

    @BindView(R.id.phoneNumberWrapper)
    protected TextInputLayout phoneNumberWrapper;
    @BindView(R.id.fullnameWrapper)
    protected TextInputLayout fullnameWrapper;
    @BindView(R.id.usernameWrapper)
    protected  TextInputLayout usernameWrapper;
    @BindView(R.id.passwordWrapper)
    protected  TextInputLayout passwordWrapper;
    @BindView(R.id.retypePasswordWrapper)
    protected  TextInputLayout retypePasswordWrapper;

    @BindView(R.id.phoneNumber)
    protected EditText phoneNumber;
    @BindView(R.id.fullname)
    protected EditText fullname;
    @BindView(R.id.genderRadioGroup)
    protected RadioGroup genderRadioGroup;
    @BindView(R.id.username)
    protected EditText username;
    @BindView(R.id.password)
    protected EditText password;
    @BindView(R.id.retypePassword)
    protected EditText retypePassword;
    @BindView(R.id.fullnameHeader)
    TextView fullnameHeader;
    @BindView(R.id.usernameHeader)
    TextView usernameHeader;
    @Inject
    UserRepository userService;
    @Inject
    ImageService imageService;
    @Inject
    Retrofit retrofit;
    @Inject
    SharedPreferences preferences;
    @Inject
    Translations translations;
    private AccountService accountService;
    @BindView(R.id.profileImage)
    CircleImageView profileImage;

    @BindView(R.id.editProfileButton)
    Button editProfileButton;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    private String gender;
    private UserUpdater updater;
    private CharSequence passwordTemp;
    @NonNull
    private CompositeDisposable disposable = new CompositeDisposable();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        ButterKnife.bind(this);
        ((App)getApplication()).getNetOauthComponent().inject(this);
        accountService = retrofit.create(AccountService.class);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        this.updater = payload -> {
            payload.put("id", 1);
            User user = userService.updateUser(payload.toString());
            preferences.edit().putString("username", user.getUsername()).apply();
            updateHeader(payload);
        };
        initData();
        initGender();
        validateUser();

        doUpdateProfile();
    }

    private void doUpdateProfile() {
        editProfileButton.setOnClickListener(view ->{
            if(!TextUtils.isEmpty(password.getText().toString()) && !retypePassword.getText().toString().equals(password.getText().toString())){
                retypePasswordWrapper.setError(getString(R.string.retype_password_fail));
                retypePasswordWrapper.setErrorEnabled(true);
                return;
            }
            editProfileButton.setEnabled(false);
            progressBar.setVisibility(View.VISIBLE);
            Domain input = new Domain();
            boolean isPasswordEquals = password.getText().toString().equals(retypePassword.getText().toString());
            if(!TextUtils.isEmpty(password.getText().toString()) && isPasswordEquals){
                input.put("password", password.getText().toString());
            }
            input.put("username", username.getText().toString());
            input.put("fullname", fullname.getText().toString());
            input.put("gender", gender);
            input.put("phone_number", phoneNumber.getText().toString());
            input.put("id", userService.findUser().getUser_id());
            disposable.add(accountService.editUser(input).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(response ->{
                progressBar.setVisibility(View.GONE);
                editProfileButton.setEnabled(true);
                if (TeachmeApi.ok(response)) {
                    updater.update(new Domain(input));
                    Snackbar.make(findViewById(android.R.id.content), getString(R.string.edit_user_success), Snackbar.LENGTH_LONG).setAction(android.R.string.ok, v -> {
                        IntentUtils.moveTo(this, MainActivity.class);
                        finish();
                    }).show();
                }else{
                    String errorMessage = translations.get(TeachmeApi.getError(response));
                    Snackbar.make(findViewById(android.R.id.content), errorMessage, Snackbar.LENGTH_LONG).show();
                }
            }, error ->{
                progressBar.setVisibility(View.GONE);
                editProfileButton.setEnabled(true);
                NetworkUtils.errorHandle(userService, translations, this, error);
            }));
        });
    }

    private void validateUser() {
        Observable<Boolean> usernameObserve =  RxTextView.textChanges(username).map(text -> (text.length() == 0)).distinctUntilChanged();
        Observable<Boolean> usernameRegexObserve =  RxTextView.textChanges(username).map(text -> (!Pattern.compile("^[a-z0-9_-]{3,15}$").matcher(text).matches())).distinctUntilChanged();

        Observable<Boolean> fullnameObserve =  RxTextView.textChanges(fullname).map(text -> (text.length() == 0)).distinctUntilChanged();
        Observable<Boolean> phoneNumberRegexObserve =  RxTextView.textChanges(phoneNumber).map(text -> (!Patterns.PHONE.matcher(text).matches())).distinctUntilChanged();

        Observable<Boolean> phoneNumberObserve =  RxTextView.textChanges(phoneNumber).map(text -> (text.length() == 0)).distinctUntilChanged();

        phoneNumberRegexObserve.subscribe(isValid -> {
            phoneNumberWrapper.setError(getString(R.string.invalid_phone_number));
            phoneNumberWrapper.setErrorEnabled(isValid);
        });

        phoneNumberObserve.subscribe(isValid -> {
            phoneNumberWrapper.setError(getString(R.string.required_value_phone));
            phoneNumberWrapper.setErrorEnabled(isValid);
        });

        usernameObserve.subscribe(isValid -> {
            usernameWrapper.setError(getString(R.string.required_value_username));
            usernameWrapper.setErrorEnabled(isValid);
        });
        usernameRegexObserve.subscribe(isValid -> {
            usernameWrapper.setError(getString(R.string.invalid_username));
            usernameWrapper.setErrorEnabled(isValid);
        });
        fullnameObserve.subscribe(isValid -> {
            fullnameWrapper.setError(getString(R.string.required_value_fullname));
            fullnameWrapper.setErrorEnabled(isValid);
        });
        Observable<Boolean> passwordObserve =  RxTextView.textChanges(password).skip(1).map(text -> {
            passwordTemp = text;
            return (text.length() == 0);
        });

        passwordObserve.subscribe(isValid -> {
            passwordWrapper.setError(getString(R.string.required_length_password));
            passwordWrapper.setErrorEnabled(isValid);
            retypePasswordWrapper.setError(getString(R.string.required_value_retype_password));
            retypePasswordWrapper.setErrorEnabled(isValid);
        });
        Observable<Boolean> retypePasswordObserve =  RxTextView.textChanges(retypePassword).skip(1).map(text -> (text.length() == 0));
        Observable<Boolean> retypePasswordEqualsObserve =  RxTextView.textChanges(retypePassword).skip(1).map(text -> !text.toString().equals(passwordTemp.toString()));

        retypePasswordObserve.subscribe(isValid -> {
            retypePasswordWrapper.setError(getString(R.string.required_value_retype_password));
            retypePasswordWrapper.setErrorEnabled(isValid);
        });
        retypePasswordEqualsObserve.subscribe(isValid -> {
            retypePasswordWrapper.setError(getString(R.string.retype_password_fail));
            retypePasswordWrapper.setErrorEnabled(isValid);
        });

        Observable.combineLatest(usernameObserve, usernameRegexObserve, fullnameObserve, phoneNumberObserve, phoneNumberRegexObserve, (usernameInvalid, userRegexInvalid,fullnameInvalid, phoneNumberInvalid, phoneNumberRegexInvalid) -> !phoneNumberInvalid && !phoneNumberRegexInvalid && !usernameInvalid && !userRegexInvalid && !fullnameInvalid ).subscribe(valid -> editProfileButton.setVisibility(valid ? View.VISIBLE : View.GONE));
    }

    private void initData() {
        User user = userService.findUser();
        fullname.setText(user.getFullname());
        username.setText(user.getUsername());
        fullnameHeader.setText(user.getFullname());
        usernameHeader.setText("@" + user.getUsername());
        phoneNumber.setText(user.getPhone_number());
        this.gender = user.getGender();
        if(user.getGender().equalsIgnoreCase(GenderConstant.MAN)){
            genderRadioGroup.check(R.id.man);
        }else{
            genderRadioGroup.check(R.id.woman);
        }
        imageService.loadUserImage(profileImage, user.getUsername());

    }


    private void initGender() {
        genderRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if(checkedId == R.id.man){
                gender = GenderConstant.MAN;
            }else{
                gender = GenderConstant.WOMAN;
            }
        });

    }


    private void updateHeader(Domain input){
        usernameHeader.setText("@" + input.getString("username"));
        fullnameHeader.setText(input.getString("fullname"));
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposable.clear();
    }
}
