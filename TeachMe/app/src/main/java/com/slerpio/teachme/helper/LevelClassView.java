package com.slerpio.teachme.helper;

import android.app.Activity;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import com.slerpio.teachme.model.Domain;
import com.slerpio.teachme.service.SchoolService;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

import java.util.ArrayList;
import java.util.List;

public class LevelClassView {
    private ArrayAdapter<String> classAdapter;
    private ArrayAdapter<String> levelAdapter;
    private SchoolService schoolService;
    private List<String> classList = new ArrayList<>();
    private List<String> levelList = new ArrayList<>();
    private Activity activity;
    private CompositeDisposable disposable = new CompositeDisposable();
    private Spinner spinnerClass;
    private Spinner spinnerLevel;
    private TextView levelAndClass;
    private Translations translations;
    private Long classId;
    private Long levelId;
    public LevelClassView(Activity activity, SchoolService schoolService, Translations translations) {
        this.activity = activity;
        this.translations = translations;
        this.schoolService = schoolService;
        this.levelAdapter = new ArrayAdapter<>(this.activity, android.R.layout.simple_spinner_dropdown_item, levelList);
        this.classAdapter = new ArrayAdapter<>(this.activity, android.R.layout.simple_spinner_dropdown_item, classList);
    }

    public void bind(Spinner spinnerLevel, Spinner spinnerClass, TextView levelAndClass){
        this.spinnerLevel = spinnerLevel;
        this.spinnerClass = spinnerClass;
        spinnerLevel.setVisibility(View.GONE);
        spinnerLevel.setAdapter(levelAdapter);
        spinnerClass.setVisibility(View.GONE);
        spinnerClass.setAdapter(classAdapter);
        this.levelAndClass = levelAndClass;
        this.levelAndClass.setVisibility(View.GONE);
    }

    public void fill(){
        disposable.add(schoolService.getSchoolLevel().observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(response -> {
            this.levelAndClass.setVisibility(View.VISIBLE);
            if(TeachmeApi.ok(response)){
                List<Domain> payloads = TeachmeApi.payloads(response);
                Observable.fromIterable(payloads).map(item -> item.getString("name")).toList().subscribe(items -> {
                    levelList.clear();
                    levelList.addAll(items);
                    levelAdapter.notifyDataSetChanged();
                });
                spinnerLevel.setVisibility(View.VISIBLE);
                spinnerLevel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        Domain payload = payloads.get(position);
                        levelId = payload.getLong("id");
                        disposable.add(schoolService.getSchoolClassByLevelId(levelId).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(response ->{
                            if(TeachmeApi.ok(response)){
                                spinnerClass.setVisibility(View.VISIBLE);
                                List<Domain> classPayloads = TeachmeApi.payloads(response);
                                Observable.fromIterable(classPayloads).map(item -> item.getString("name")).toList().subscribe(items -> {
                                    classList.clear();
                                    classList.addAll(items);
                                    classList.add("Banyak Kelas");
                                    classAdapter.notifyDataSetChanged();
                                });
                                spinnerClass.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                        try {
                                            Domain payload = classPayloads.get(position);
                                            classId = payload.getLong("id");
                                        }catch (IndexOutOfBoundsException ignore){
                                            classId = null;
                                        }
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {

                                    }
                                });
                            }
                        }, error ->{
                            error.printStackTrace();
                            Snackbar.make(activity.findViewById(android.R.id.content), translations.get(GlobalConstant.CONNECTION_ERROR), Snackbar.LENGTH_LONG).show();
                        }));
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }else{
                Snackbar.make(activity.findViewById(android.R.id.content), translations.get(TeachmeApi.getError(response)), Snackbar.LENGTH_LONG).show();
            }
        }, error ->{
            Snackbar.make(activity.findViewById(android.R.id.content), translations.get(GlobalConstant.CONNECTION_ERROR), Snackbar.LENGTH_LONG).show();
        }));
    }

    public void clear(){
        disposable.clear();
    }

    public Long getClassId() {
        return classId;
    }

    public Long getLevelId() {
        return levelId;
    }
}
