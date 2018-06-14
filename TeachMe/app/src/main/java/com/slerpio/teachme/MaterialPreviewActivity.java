package com.slerpio.teachme;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebChromeClient;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.slerpio.teachme.helper.*;
import com.slerpio.teachme.model.Domain;
import com.slerpio.teachme.model.User;
import com.slerpio.teachme.realm.service.UserRepository;
import com.slerpio.teachme.service.MaterialService;
import com.slerpio.view.TouchyWebView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

public class MaterialPreviewActivity extends AppCompatActivity {
    @BindView(R.id.clock)
    TextView clock;
    @BindView(R.id.fullname)
    TextView fullName;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.documentWeb)
    TouchyWebView webView;
    @Inject
    UserRepository userRepository;
    @Inject
    Translations translations;

    @Inject
    SharedPreferences preferences;

    @NonNull
    CompositeDisposable disposable = new CompositeDisposable();
    @Inject
    Retrofit retrofit;
    private MaterialService materialService;
    private Domain documentDomain;
    private Domain topicDomain;
    private User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_material_preview);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        ButterKnife.bind(this);
        ((App) getApplication()).getNetOauthComponent().inject(this);
        this.materialService = retrofit.create(MaterialService.class);
        Bundle bundle = getIntent().getExtras();
        this.documentDomain = new Domain(bundle.getString("document"));
        this.topicDomain = new Domain(bundle.getString("topic"));
        this.user = userRepository.findUser();
        this.fullName.setText(user.getFullname());
        this.clock.setText(DateUtils.printDate(System.currentTimeMillis()));
        this.title.setText(bundle.getString("title"));
        String url = getString(R.string.teach_me_url) + "teachme/get_document?id=" + documentDomain.getLong("id");
        Map<String, String> header = new HashMap<>();
        header.put("Authorization", "Bearer " + preferences.getString("token", ""));
        webView.loadUrl(url, header);
        webView.setWebChromeClient(new WebChromeClient());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_send, menu);
        ViewUtils.setViewDrawableColor(menu, getResources().getColor(R.color.colorAccent));
        return true;
    }



    private void doAddMaterial(){

        Domain input = new Domain();
        if(!topicDomain.containsKey("id")){
            input.put("name", topicDomain.getString("name"));
        }else{
            input.put("topic_id", topicDomain.getLong("id"));
        }
        input.put("title", title.getText().toString());
        input.put("description", title.getText().toString());
        if(user != null){
            input.put("user_id", this.user.getUser_id());
        }

        input.put("price", 0L);
        input.put("type", getIntent().getStringExtra("type"));
        input.put("document_id", documentDomain.getLong("id"));
        disposable.add(materialService.addMaterial(input).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(response ->{
            if(TeachmeApi.ok(response)){
                Bundle bundle = new Bundle();
                bundle.putString("message", getString(R.string.add_material_success));
                IntentUtils.moveTo(MaterialPreviewActivity.this, MainActivity.class, bundle);
                finish();
            }else{
                Snackbar.make(findViewById(android.R.id.content), TeachmeApi.getError(response), Snackbar.LENGTH_LONG).show();
            }
        }, error -> NetworkUtils.errorHandle(userRepository, translations, this, error)));
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_send){
            doAddMaterial();
            return true;
        }
        return BackPressed.home(item, this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
