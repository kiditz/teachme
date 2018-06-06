package com.slerpio.teachme;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
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
import com.slerpio.teachme.model.User;
import com.slerpio.teachme.realm.service.UserRepository;
import com.slerpio.teachme.service.DocumentService;
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
    private static final String TAG = AddMaterialActivity.class.getName();
    private String type;
    @BindView(R.id.titleWrapper)
    TextInputLayout titleWrapper;
    @BindView(R.id.descriptionWrapper)
    TextInputLayout descriptionWrapper;
    @BindView(R.id.title)
    EditText title;
    @BindView(R.id.description)
    EditText description;
    @BindView(R.id.topic)
    AutoCompleteTextView topic;
    @BindView(R.id.doneButton)
    Button doneButton;
    @Inject
    Retrofit retrofit;
    @Inject
    UserRepository userRepository;
    @Inject
    Translations translations;
    private MaterialService materialService;
    private DocumentService documentService;
    private ArrayAdapter<String> adapter;
    @NonNull
    private CompositeDisposable disposable = new CompositeDisposable();
    private List<Domain> topicList = new ArrayList<>();
    private long topicId = -1L;
    private long documentId = -1L;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_material);
        ButterKnife.bind(this);
        ((App)getApplication()).getNetOauthComponent().inject(this);
        Bundle bundle = getIntent().getExtras();
        this.type = bundle.getString("type");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Domain document = new Domain(bundle.getString("document"));
        this.documentId = document.getLong("id");
        this.adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        this.topic.setThreshold(1);
        this.topic.setAdapter(adapter);
        this.materialService = retrofit.create(MaterialService.class);
        this.documentService = retrofit.create(DocumentService.class);
        this.topic.setOnKeyListener((v, keyCode, event) -> {
            if(event.getAction() == KeyEvent.ACTION_UP){
                fillTopic();
            }
            return false;
        });
        this.topic.setOnItemClickListener(this);
        validateInput();
        RxView.clicks(doneButton).subscribe(view -> doAddMaterial());
    }

    private void fillTopic() {
        Log.i(TAG, "fillTopic: " + topic.getText().toString());
        Domain input = new Domain();
        input.put("page", 1);
        input.put("size", 10);
        input.put("name", topic.getText().toString());
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

    private void doAddMaterial(){
        if(TextUtils.isEmpty(topic.getText().toString())){
            Snackbar.make(findViewById(android.R.id.content), getString(R.string.required_value_topic), Snackbar.LENGTH_LONG).show();
            return;
        }
        Domain input = new Domain();
        if(topicId == -1){
            input.put("name", topic.getText().toString());
        }else{
            input.put("topic_id", topicId);
        }
        input.put("title", title.getText().toString());
        input.put("description", description.getText().toString());
        User user = userRepository.findUser();
        if(user != null){
            input.put("user_id", user.getUser_id());
        }
        input.put("price", 0L);
        input.put("type", type);
        input.put("document_id", documentId);
        disposable.add(materialService.addMaterial(input).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(response ->{
            if(TeachmeApi.ok(response)){
                Snackbar.make(findViewById(android.R.id.content), String.format(getString(R.string.add_material_success), title.getText().toString()), Snackbar.LENGTH_LONG).setAction(android.R.string.ok, v -> {
                    IntentUtils.moveTo(AddMaterialActivity.this, MainActivity.class);
                    finish();
                }).show();
            }else{
                Snackbar.make(findViewById(android.R.id.content), TeachmeApi.getError(response), Snackbar.LENGTH_LONG).show();
            }
        }, error -> NetworkUtils.errorHandle(userRepository, translations, this, error)));
    }

    private void validateInput() {
        Observable<Boolean> titleObservable =  RxTextView.textChanges(title).map(text -> text.length() == 0).distinctUntilChanged();
        Observable<Boolean> descriptionObservable =  RxTextView.textChanges(description).map(text -> text.length() == 0).distinctUntilChanged();
        titleObservable.subscribe(isValid -> {
            titleWrapper.setError(getString(R.string.required_value_title));
            titleWrapper.setErrorEnabled(isValid);
        });

        descriptionObservable.subscribe(isValid -> {
            descriptionWrapper.setError(getString(R.string.required_value_description));
            descriptionWrapper.setErrorEnabled(isValid);
        });

        Observable.combineLatest(titleObservable, descriptionObservable, (titleInvalid, descriptionInvalid) -> !titleInvalid && !descriptionInvalid).subscribe(valid -> doneButton.setEnabled(valid));


    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String name = adapter.getItem(position);
        Domain topic = getTopicByName(name);
        if(topic != null){
            this.topicId = topic.getLong("id");
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
        disposable.add(documentService.deleteDocument(documentId).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(response ->{
            Log.d(TAG, "onBackPressed: "+ response.toString());
        }, error -> NetworkUtils.errorHandle(userRepository, translations, this, error)));
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
