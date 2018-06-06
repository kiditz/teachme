package com.slerpio.teachme;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.slerpio.teachme.helper.BackPressed;
import com.slerpio.teachme.helper.IntentUtils;
import com.slerpio.teachme.model.Domain;

import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MaterialDetailActivity extends AppCompatActivity {
    @BindView(R.id.materialDescription)
    TextView materialDescription;
    @BindView(R.id.clock)
    TextView clock;
    @BindView(R.id.fullname)
    TextView fullName;
    @BindView(R.id.documentWeb)
    WebView webView;
    private SimpleDateFormat format = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
    @Inject
    SharedPreferences preferences;

    Domain material = new Domain();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_material_detail);
        ButterKnife.bind(this);
        ((App)getApplication()).getNetOauthComponent().inject(this);
        Bundle bundle = getIntent().getExtras();

        if(bundle == null){
            IntentUtils.moveTo(this, MainActivity.class);
            finish();
            return;
        }

        this.material = new Domain(bundle.getString("material"));
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle(material.getString("title"));
        }
        this.clock.setText(format.format(new Date(material.getLong("created_at"))));
        this.materialDescription.setText(material.getString("description"));
        this.fullName.setText(material.getDomain("user").getString("fullname"));
        String url = getString(R.string.teach_me_url) + "teachme/get_document?id=" +material.getLong("document_id");
        Map<String, String> header = new HashMap<>();
        header.put("Authorization", "Bearer " + preferences.getString("token", ""));
        webView.loadUrl(url, header);
        webView.setHorizontalScrollBarEnabled(false);
        webView.setVerticalScrollBarEnabled(true);
        webView.setWebChromeClient(new WebChromeClient());

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
