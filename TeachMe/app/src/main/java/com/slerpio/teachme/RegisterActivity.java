package com.slerpio.teachme;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.*;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxRadioGroup;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.slerpio.teachme.helper.*;
import com.slerpio.teachme.model.Domain;
import com.slerpio.teachme.service.AccountService;
import com.slerpio.teachme.service.SchoolService;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

import javax.inject.Inject;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = RegisterActivity.class.getName();
    //Wrapper
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
    @BindView(R.id.registerButton)
    protected Button registerButton;
    @BindView(R.id.progressBar)
    protected  ProgressBar progressBar;
    @BindView(R.id.spinnerLevel)
    protected Spinner spinnerLevel;
    @BindView(R.id.spinnerClass)
    protected Spinner spinnerClass;
    @BindView(R.id.levelAndClass)
    protected TextView levelAndClass;
    private String gender = GenderConstant.MAN;
    private CharSequence passwordTemp;
    @NonNull
    private CompositeDisposable disposable = new CompositeDisposable();
    @Inject
    protected Retrofit retrofit;
    @Inject
    protected Translations translations;

    private AccountService accountService;
    private LevelClassView levelClassView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        ((App)getApplication()).getNetComponent().inject(this);
        validateRegister();
        handleGender();
        doRegister();
        accountService = retrofit.create(AccountService.class);
        SchoolService schoolService = retrofit.create(SchoolService.class);
        this.levelClassView = new LevelClassView(this, schoolService, translations);
        this.levelClassView.bind(spinnerLevel, spinnerClass, levelAndClass);
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.levelClassView.fill();
    }


    private void doRegister() {
        RxView.clicks(registerButton).subscribe(view ->{
            progressBar.setVisibility(View.VISIBLE);
            registerButton.setEnabled(false);
            Domain input = new Domain();
            input.put("phone_number", phoneNumber.getText().toString());
            input.put("username", username.getText().toString());
            input.put("fullname", fullname.getText().toString());
            input.put("gender", gender);
            input.put("level_id", levelClassView.getLevelId());
            input.put("class_id", levelClassView.getClassId());
            input.put("password", password.getText().toString());
            disposable.add(accountService.registerTeacher(input).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(response ->{
                progressBar.setVisibility(View.GONE);
                registerButton.setEnabled(true);
                if(TeachmeApi.ok(response)){
                    Snackbar.make(findViewById(android.R.id.content), getString(R.string.registration_success), Snackbar.LENGTH_LONG)
                            .setAction(R.string.login, v -> moveToLogin()).show();
                }else{
                    Snackbar.make(findViewById(android.R.id.content), translations.get(TeachmeApi.getError(response)), Snackbar.LENGTH_LONG).show();
                }
            }, error -> {
                Log.e(TAG, "doRegister: ", error);
                registerButton.setEnabled(true);
                progressBar.setVisibility(View.GONE);
                Snackbar.make(findViewById(android.R.id.content), translations.get(GlobalConstant.CONNECTION_ERROR), Snackbar.LENGTH_LONG).show();
            }));
        });
    }

    private void handleGender() {
        RxRadioGroup.checkedChanges(genderRadioGroup).subscribe(id -> {
            switch (id){
                case R.id.man:
                    this.gender = GenderConstant.MAN;
                    break;
                case R.id.woman:
                    this.gender = GenderConstant.WOMAN;
                    break;
            }
        });
    }

    private void validateRegister() {
        Observable<Boolean> phoneNumberRegexObserve =  RxTextView.textChanges(phoneNumber).map(text -> (!Patterns.PHONE.matcher(text).matches())).skip(1).distinctUntilChanged();
        Observable<Boolean> phoneNumberObserve =  RxTextView.textChanges(phoneNumber).map(text -> (text.length() == 0)).skip(1).distinctUntilChanged();

        Observable<Boolean> fullnameObserve =  RxTextView.textChanges(fullname).map(text -> (text.length() == 0)).skip(1).distinctUntilChanged();

        Observable<Boolean> usernameObserve =  RxTextView.textChanges(username).map(text -> (text.length() == 0)).skip(1).distinctUntilChanged();
        Observable<Boolean> usernameRegexObserve =  RxTextView.textChanges(username).map(text -> (!Pattern.compile("^[a-z0-9_-]{3,15}$").matcher(text).matches())).skip(1).distinctUntilChanged();

        Observable<Boolean> passwordObserve =  RxTextView.textChanges(password).map(text -> {
            passwordTemp = text;
            return (text.length() < 6);
        }).skip(1).distinctUntilChanged();

        Observable<Boolean> retypePasswordObserve =  RxTextView.textChanges(retypePassword).map(text -> (text.length() == 0)).skip(1).distinctUntilChanged();
        Observable<Boolean> retypePasswordEqualsObserve =  RxTextView.textChanges(retypePassword).map(text -> !text.toString().equals(passwordTemp.toString())).skip(1).distinctUntilChanged();

        usernameRegexObserve.subscribe(isValid -> {
            usernameWrapper.setError(getString(R.string.invalid_username));
            usernameWrapper.setErrorEnabled(isValid);
        });

        phoneNumberRegexObserve.subscribe(isValid -> {
            phoneNumberWrapper.setError(getString(R.string.invalid_phone_number));
            phoneNumberWrapper.setErrorEnabled(isValid);
        });

        fullnameObserve.subscribe(isValid -> {
            fullnameWrapper.setError(getString(R.string.required_value_fullname));
            fullnameWrapper.setErrorEnabled(isValid);
        });

        phoneNumberObserve.subscribe(isValid -> {
            phoneNumberWrapper.setError(getString(R.string.required_value_phone));
            phoneNumberWrapper.setErrorEnabled(isValid);
        });
        usernameObserve.subscribe(isValid -> {
            usernameWrapper.setError(getString(R.string.required_value_username));
            usernameWrapper.setErrorEnabled(isValid);
        });
        passwordObserve.subscribe(isValid -> {
            passwordWrapper.setError(getString(R.string.required_length_password));
            passwordWrapper.setErrorEnabled(isValid);

        });
        retypePasswordObserve.subscribe(isValid -> {
            retypePasswordWrapper.setError(getString(R.string.required_value_retype_password));
            retypePasswordWrapper.setErrorEnabled(isValid);
        });
        retypePasswordEqualsObserve.subscribe(isValid -> {
            retypePasswordWrapper.setError(getString(R.string.retype_password_fail));
            retypePasswordWrapper.setErrorEnabled(isValid);
        });
        Observable.combineLatest(fullnameObserve, phoneNumberRegexObserve, phoneNumberObserve, usernameRegexObserve, usernameObserve, passwordObserve,retypePasswordObserve,retypePasswordEqualsObserve, (fullnameInvalid, phoneNumberRegexInvalid, phoneNumberInvalid, usernameRegexInvalid, usernameInvalid, passwordInvalid,retypeInvalid ,retypePasswordEqualsInvalid) -> !fullnameInvalid && !phoneNumberRegexInvalid && !phoneNumberInvalid && !usernameRegexInvalid && !usernameInvalid && !passwordInvalid && !retypeInvalid && !retypePasswordEqualsInvalid).subscribe(valid -> registerButton.setEnabled(valid));
    }

    private void moveToLogin(){
        IntentUtils.moveTo(this, LoginActivity.class);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.levelClassView.clear();
        disposable.clear();
    }
}
