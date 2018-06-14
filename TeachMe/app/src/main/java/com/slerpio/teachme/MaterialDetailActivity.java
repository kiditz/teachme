package com.slerpio.teachme;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.webkit.WebChromeClient;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.slerpio.teachme.helper.BackPressed;
import com.slerpio.teachme.helper.DateUtils;
import com.slerpio.teachme.helper.IntentUtils;
import com.slerpio.teachme.model.Domain;
import com.slerpio.view.AnimationUtils;
import com.slerpio.view.TouchyWebView;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

public class MaterialDetailActivity extends AppCompatActivity {
    @BindView(R.id.clock)
    TextView clock;
    @BindView(R.id.fullname)
    TextView fullName;
    @BindView(R.id.cardView)
    CardView cardView;
    @BindView(R.id.documentWeb)
    TouchyWebView webView;
    @BindView(R.id.descriptionView)
    LinearLayout descriptionView;
    @BindView(R.id.scrollView)
    ScrollView scrollView;
    @BindView(R.id.description)
    TextView description;
    @Inject
    SharedPreferences preferences;

    Domain material = new Domain();
    private boolean isDescriptionShowUp = false;
    ViewGroup.LayoutParams tempParams;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_material_detail);
        ButterKnife.bind(this);
        this.tempParams = descriptionView.getLayoutParams();
        ((App) getApplication()).getNetOauthComponent().inject(this);
        Bundle bundle = getIntent().getExtras();

        if (bundle == null) {
            IntentUtils.moveTo(this, MainActivity.class);
            finish();
            return;
        }

        this.material = new Domain(bundle.getString("material"));
        description.setText(material.getString("description"));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle(material.getString("title"));
        }
        this.clock.setText(DateUtils.printDate(material.getLong("created_at")));
        this.fullName.setText(material.getDomain("user").getString("fullname"));
        String url = getString(R.string.teach_me_url) + "teachme/get_document?id=" + material.getLong("document_id");
        Map<String, String> header = new HashMap<>();
        header.put("Authorization", "Bearer " + preferences.getString("token", ""));
        webView.loadUrl(url, header);
        webView.setWebChromeClient(new WebChromeClient());

    }

    @OnClick(R.id.descriptionButton)
    public void onDescriptionButtonClicked(){
        if(isDescriptionShowUp){
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            descriptionView.setLayoutParams(params);
            isDescriptionShowUp = false;
        }else {
            AnimationUtils.slideDown(descriptionView, 500).setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }
                @Override
                public void onAnimationEnd(Animation animation) {
                    descriptionView.setLayoutParams(tempParams);
                    descriptionView.requestLayout();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });

        }
    }

    @OnClick(R.id.viewDescription)
    public void onDescriptionViewClicked(){
        if(isDescriptionShowUp){
            AnimationUtils.slideDown(descriptionView, 500);
        }else{
            AnimationUtils.slideUp(descriptionView, 500);
        }
        isDescriptionShowUp = !isDescriptionShowUp;
    }



    @Override
    public void onBackPressed() {
        if(isDescriptionShowUp){
            AnimationUtils.slideDown(descriptionView, 500);
            isDescriptionShowUp = !isDescriptionShowUp;
        }else{
            super.onBackPressed();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return BackPressed.home(item, this);
    }
}
