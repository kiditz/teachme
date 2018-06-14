package com.slerpio.teachme;

import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.slerpio.teachme.helper.*;
import com.slerpio.teachme.model.Domain;
import com.slerpio.teachme.realm.service.UserRepository;
import com.slerpio.teachme.service.MaterialService;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class AddMaterialActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    //private static final String TAG = AddMaterialActivity.class.getName();
    @BindView(R.id.titleWrapper)
    TextInputLayout titleWrapper;
    @BindView(R.id.topicWrapper)
    TextInputLayout topicWrapper;
    @BindView(R.id.title)
    EditText title;
    @BindView(R.id.topic)
    AutoCompleteTextView topic;
    @BindView(R.id.doneButton)
    Button nextButton;
    @Inject
    Retrofit retrofit;
    @Inject
    UserRepository userRepository;
    @Inject
    Translations translations;
    private MaterialService materialService;

    private ArrayAdapter<String> adapter;
    @NonNull
    private CompositeDisposable disposable = new CompositeDisposable();
    private List<Domain> topicList = new ArrayList<>();
    private Domain topicDomain;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_material);
        ButterKnife.bind(this);
        ((App)getApplication()).getNetOauthComponent().inject(this);
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        this.adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        this.topic.setThreshold(1);
        this.topic.setAdapter(adapter);
        this.materialService = retrofit.create(MaterialService.class);
        fillTopic("");
        this.topic.setOnKeyListener((v, keyCode, event) -> {
            if(event.getAction() == KeyEvent.ACTION_UP){
                fillTopic(topic.getText().toString());
            }
            return false;
        });
        this.topic.setOnItemClickListener(this);
        validateInput();
        RxView.clicks(nextButton).subscribe(view -> {
            if (TextUtils.isEmpty(topic.getText().toString())) {
                topicWrapper.setError(getString(R.string.required_value_topic));
                topicWrapper.setErrorEnabled(true);
                return;
            }
            pushTopic();
            Bundle bundle = new Bundle();
            bundle.putString("topic", topicDomain.toString());
            bundle.putString("title", title.getText().toString());
            IntentUtils.moveTo(this, MaterialWriteActivity.class, bundle);
        });
    }

    private void fillTopic(String text) {
        Domain input = new Domain();
        input.put("page", 1);
        input.put("size", 10);
        input.put("name", text);
        disposable.add(materialService.getMaterialTopicByName(input).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(response->{
            if(TeachmeApi.ok(response)){
                List<Domain> payloads = TeachmeApi.payloads(response);
                Observable.fromIterable(payloads).map(item -> item.getString("name")).toList().subscribe(items -> {
                    adapter.clear();
                    adapter.addAll(items);
                    adapter.notifyDataSetChanged();
                    topicList.clear();
                    topicList.addAll(payloads);
                });
            }
        }, error-> NetworkUtils.errorHandle(userRepository, translations, this, error)));
    }



    private void validateInput() {
        Observable<Boolean> titleObservable =  RxTextView.textChanges(title).map(text -> text.length() == 0).distinctUntilChanged();

        titleObservable.subscribe(isInvalid -> {
            titleWrapper.setError(getString(R.string.required_value_title));
            titleWrapper.setErrorEnabled(isInvalid);
        });

        Observable<Boolean> topicObservable =  RxTextView.textChanges(topic).map(text -> text.length() == 0).distinctUntilChanged();

        topicObservable.subscribe(isInvalid -> {
            topicWrapper.setError(getString(R.string.required_value_topic));
            topicWrapper.setErrorEnabled(isInvalid);
        });

        Observable.combineLatest(titleObservable, topicObservable, (titleInvalid, topicInvalid) -> !titleInvalid && !topicInvalid).subscribe(valid -> nextButton.setEnabled(valid));

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String name = adapter.getItem(position);
        this.topicDomain = getTopicByName(name);
        pushTopic();
    }

    private void pushTopic(){
        if(this.topicDomain == null){
            this.topicDomain = new Domain();
            this.topicDomain.put("name", topic.getText().toString());
        }
    }

    private Domain getTopicByName(String name){
        for (Domain topic : topicList){
            if(topic.getString("name").equals(name)){
                return topic;
            }
        }
        return null;
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposable.clear();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return BackPressed.home(item, this);
    }
}
