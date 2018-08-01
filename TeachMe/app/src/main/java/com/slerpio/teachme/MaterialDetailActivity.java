package com.slerpio.teachme;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.widget.*;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.slerpio.lib.AnimationUtils;
import com.slerpio.lib.TouchyWebView;
import com.slerpio.lib.core.Domain;
import com.slerpio.lib.messaging.Stomp;
import com.slerpio.lib.messaging.Subscription;
import com.slerpio.teachme.helper.BackPressed;
import com.slerpio.lib.core.DateUtils;
import com.slerpio.teachme.helper.IntentUtils;
import com.slerpio.teachme.helper.ViewUtils;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

public class MaterialDetailActivity extends AppCompatActivity {
    private static final String TAG = MaterialDetailActivity.class.getName();
    @BindView(R.id.clock)
    TextView clock;
    @BindView(R.id.title)
    TextView title;
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
    @BindView(R.id.searchView)
    MaterialSearchView searchView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    private static final CharSequence[] TEXT_SIZE = new CharSequence[]{
            "Sangat Kecil", "Kecil", "Normal", "Besar", "Sangat Besar"
    };
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

        setSupportActionBar(toolbar);
        /*stomp.subscribe(new Subscription("/app/comment.material/get_comment/51/0/10", new Subscription.ListenerSubscriptionAdapter() {
            @Override
            public void onMessage(Map<String, String> headers, Domain body) {
                Toast.makeText(MaterialDetailActivity.this, body.toString(), Toast.LENGTH_SHORT).show();
            }
        }));*/

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle(StringUtils.EMPTY);
        }
        this.title.setText(material.getString("title"));
        this.clock.setText(DateUtils.printDate(material.getLong("created_at")));
        this.fullName.setText(material.getDomain("user").getString("fullname"));
        String url = getString(R.string.teach_me_url) + "teachme/get_document?id=" + material.getLong("document_id");
        Map<String, String> header = new HashMap<>();
        header.put("Authorization", "Bearer " + preferences.getString("token", ""));
        webView.loadUrl(url, header);
        webView.setWebChromeClient(new WebChromeClient());
        searchView.setHint(getString(R.string.search_hint_words));
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                webView.findAllAsync(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
            }

            @Override
            public void onSearchViewClosed() {
            }
        });


    }

    @OnClick(R.id.comment)
    public void onCommentClick() {
        Bundle bundle = getIntent().getExtras();
        if (bundle == null)
            return;
        IntentUtils.moveTo(this, CommentMaterialActivity.class, bundle);
    }

    @OnClick(R.id.descriptionButton)
    public void onDescriptionButtonClicked() {
        if (isDescriptionShowUp) {
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            descriptionView.setLayoutParams(params);
            isDescriptionShowUp = false;
        } else {
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
    public void onDescriptionViewClicked() {
        if (isDescriptionShowUp) {
            AnimationUtils.slideDown(descriptionView, 500);
        } else {
            AnimationUtils.slideUp(descriptionView, 500);
        }
        isDescriptionShowUp = !isDescriptionShowUp;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_material_detail, menu);
        ViewUtils.setViewDrawableColor(menu, getResources().getColor(R.color.colorAccent));
        searchView.setMenuItem(menu.findItem(R.id.action_search));

        return true;
    }

    @Override
    public void onBackPressed() {
        if (isDescriptionShowUp) {
            AnimationUtils.slideDown(descriptionView, 500);
            isDescriptionShowUp = !isDescriptionShowUp;
        } else {
            super.onBackPressed();
        }

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_text_height:
                doSetFontSize();
                return true;
        }
        return BackPressed.home(item, this);
    }

    private void doSetFontSize() {
        final WebSettings webSettings = webView.getSettings();
        AlertDialog dialog = new AlertDialog.Builder(this).setItems(TEXT_SIZE, (dialog1, which) -> {
            switch (which) {
                case 0:
                    webSettings.setTextSize(WebSettings.TextSize.SMALLEST);
                    break;
                case 1:
                    webSettings.setTextSize(WebSettings.TextSize.SMALLER);
                    break;
                case 2:
                    webSettings.setTextSize(WebSettings.TextSize.NORMAL);
                    break;
                case 3:
                    webSettings.setTextSize(WebSettings.TextSize.LARGER);
                    break;
                case 4:
                    webSettings.setTextSize(WebSettings.TextSize.LARGEST);
                    break;
            }
        }).create();
        dialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
