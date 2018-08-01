package com.slerpio.teachme;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.slerpio.lib.core.Domain;
import com.slerpio.lib.messaging.Stomp;
import com.slerpio.lib.messaging.Subscription;
import com.slerpio.teachme.adapter.AbstractRecyclerPagination;
import com.slerpio.teachme.adapter.MaterialCommentAdapter;
import com.slerpio.teachme.helper.NetworkUtils;
import com.slerpio.teachme.helper.TeachmeApi;
import com.slerpio.teachme.helper.Translations;
import com.slerpio.teachme.model.User;
import com.slerpio.teachme.realm.service.UserRepository;
import com.slerpio.teachme.service.ImageService;
import com.slerpio.teachme.service.MaterialService;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import org.apache.commons.lang3.StringUtils;
import retrofit2.Retrofit;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CommentMaterialActivity extends AppCompatActivity {
    public static final String TAG = CommentMaterialActivity.class.getName();
    @BindView(R.id.message)
    TextView message;
    @BindView(R.id.recycler)
    RecyclerView recyclerView;
    @Inject
    UserRepository userRepository;
    @Inject
    Translations translations;
    @Inject
    Retrofit retrofit;
    @Inject
    Stomp stomp;
    @Inject
    ImageService imageService;

    private Domain material;
    private User user;
    private List<Domain> comments = new ArrayList<>();
    private CompositeDisposable disposable = new CompositeDisposable();
    private MaterialCommentAdapter adapter;
    private MaterialService materialService;
    private boolean isLoading;
    private boolean isLastPage;
    private int currentPage = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_material);
        ButterKnife.bind(this);
        ((App)getApplication()).getNetOauthComponent().inject(this);
        this.user = userRepository.findUser();
        if(user == null)
            return;

        Bundle bundle = getIntent().getExtras();
        if(bundle == null)
            return;
        this.materialService = retrofit.create(MaterialService.class);
        material = new Domain(bundle.getString("material"));
        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new MaterialCommentAdapter(this, comments);
        adapter.setImageService(imageService);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(adapter);
        comments.clear();
        getData(currentPage);
        recyclerView.addOnScrollListener(new AbstractRecyclerPagination(manager) {
            @Override
            public boolean isLoading() {
                return isLoading;
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public int getTotalItemCount() {
                return TeachmeApi.SIZE;
            }

            @Override
            public void loadMoreItems() {
                currentPage = currentPage + 1;
                getData(currentPage);
            }
        });


        stomp.subscribe(new Subscription("/topic/comment.material." + material.getLong("id"), new Subscription.ListenerSubscriptionAdapter() {
            @Override
            public void onMessage(Map<String, String> headers, Domain body) {
                comments.set(0, body);
                adapter.notifyItemChanged(0);
            }
        }));
    }

    @OnClick(R.id.send)
    public void send(){
        if(StringUtils.isEmpty(message.getText().toString()) || user == null || material == null)
            return;
        String msgStr = message.getText().toString();
        Domain input = new Domain();
        input.put("message", msgStr);
        input.put("sender_user_id", user.getUser_id());
        input.put("material_id", material.getLong("id"));
        stomp.send("/app/comment.material/add", input);
        message.setText(StringUtils.EMPTY);
    }

    private void getData(int page){

        Domain input = new Domain();
        input.put("material_id", material.getLong("id"));
        input.put("page", page);
        input.put("size", 10);
        isLoading = true;
        disposable.add(materialService.getMaterialComment(input).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(response ->{
            isLoading = false;
            if (TeachmeApi.ok(response)) {
                if (response.containsKey("total_pages")) {
                    int total = response.getInt("total_pages");
                    if (page == total) {
                        isLastPage = true;
                    }
                }
                comments.addAll(TeachmeApi.payloads(response));
                isLoading = false;
                adapter.notifyDataSetChanged();
            }
        }, error -> NetworkUtils.errorHandle(userRepository, translations, this, error)));
    }
}
