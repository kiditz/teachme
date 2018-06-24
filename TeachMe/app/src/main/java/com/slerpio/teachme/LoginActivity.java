package com.slerpio.teachme;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.slerpio.teachme.helper.IntentUtils;
import com.slerpio.teachme.helper.MetaData;
import com.slerpio.teachme.helper.OauthCredentialGenerator;
import com.slerpio.teachme.helper.Translations;
import com.slerpio.teachme.model.Domain;
import com.slerpio.teachme.realm.service.UserRepository;
import com.slerpio.teachme.service.AccountService;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.HttpException;
import retrofit2.Retrofit;

import javax.inject.Inject;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.username)
    protected EditText username;

    @BindView(R.id.usernameWrapper)
    protected  TextInputLayout usernameWrapper;

    @BindView(R.id.password)
    protected  EditText password;

    @BindView(R.id.passwordWrapper)
    protected  TextInputLayout passwordWrapper;
    @BindView(R.id.loginButton)
    protected  Button loginButton;
    @BindView(R.id.progressBar)
    protected ProgressBar loginProgressBar;
    @Inject
    protected Retrofit retrofit;
    @Inject
    protected UserRepository userService;
    @Inject
    protected Translations translations;
    @Inject
    protected SharedPreferences preferences;

    private AccountService accountService;

    @NonNull
    private CompositeDisposable disposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        ((App)getApplication()).getNetComponent().inject(this);
        if(preferences.contains("username")){
            String username = preferences.getString("username", null);
            if(userService.hasLogin(username)){
                moveToMainActivity();
            }
        }
        this.accountService = retrofit.create(AccountService.class);
        validateLogin();
        doLogin();
    }

    protected  void doLogin() {
        MetaData metaData = new MetaData(this);
        String authHeader = "Basic ".concat(OauthCredentialGenerator.generateCredentials(metaData.getClientId(), metaData.getClientSecret()));

        RxView.clicks(loginButton).subscribe(view -> {
            loginButton.setEnabled(false);
            Domain input = new Domain();
            input.put("username", username.getText().toString());
            input.put("password", password.getText().toString());
            input.put("grant_type", "password");
            loginProgressBar.setVisibility(View.VISIBLE);
            disposable.add(accountService.login(authHeader, input).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(response ->{
                loginButton.setEnabled(true);
                loginProgressBar.setVisibility(View.GONE);
                String accessToken = response.getString("access_token");
                String refreshToken = response.getString("refresh_token");
                Domain decodeJwt = OauthCredentialGenerator.decodeBody(accessToken);
                String username = decodeJwt.getString("user_name");
                preferences.edit().putString("username", username).putString("token", accessToken).apply();
                List<String> authorities = decodeJwt.getListString("authorities");
                int expiresIn = response.getInt("expires_in");
                userService.addUser(accessToken, refreshToken, username, authorities, expiresIn);
                moveToMainActivity();
            }, error ->{
                loginButton.setEnabled(true);
                loginProgressBar.setVisibility(View.GONE);
                if(error instanceof HttpException){
                    HttpException ex = (HttpException) error;
                    if(ex.response().code() == 401 || ex.response().code() == 400){
                        Domain errorBody = new Domain(ex.response().errorBody().string());
                        String errorMessage = translations.get(errorBody.getString("error_description"));
                        Snackbar.make(findViewById(android.R.id.content), errorMessage, Snackbar.LENGTH_LONG).show();
                    }
                }
                cleanText();

            }));

        });
    }

    protected  void validateLogin() {
        Observable<Boolean> usernameObserve =  RxTextView.textChanges(username).skip(1).map(text -> (text.length() == 0)).distinctUntilChanged();
        Observable<Boolean> passwordObserve =  RxTextView.textChanges(password).skip(1).map(text -> (text.length() < 6)).distinctUntilChanged();
        usernameObserve.subscribe(isValid -> {
            usernameWrapper.setError(getString(R.string.required_value_username));
            usernameWrapper.setErrorEnabled(isValid);
        });

        passwordObserve.subscribe(isValid -> {
            passwordWrapper.setError(getString(R.string.required_length_password));
            passwordWrapper.setErrorEnabled(isValid);

        });
        Observable.combineLatest(usernameObserve, passwordObserve, (usernameInvalid, passwordInvalid) -> !usernameInvalid && !passwordInvalid).subscribe(valid -> loginButton.setEnabled(valid));
    }

    private void moveToMainActivity(){
        IntentUtils.moveTo(this, MainActivity.class);
        finish();
    }

    private void cleanText(){
        username.setText("");
        password.setText("");
    }
    @OnClick(R.id.registerTextView)
    public void register(){
        IntentUtils.moveTo(this, RegisterActivity.class);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposable.clear();
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
