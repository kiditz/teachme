package com.slerpio.teachme.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.slerpio.teachme.App;
import com.slerpio.teachme.R;
import com.slerpio.teachme.helper.NetworkUtils;
import com.slerpio.teachme.helper.TeachmeApi;
import com.slerpio.teachme.helper.Translations;
import com.slerpio.teachme.model.Domain;
import com.slerpio.teachme.model.User;
import com.slerpio.teachme.realm.service.UserRepository;
import com.slerpio.teachme.service.ImageService;
import com.slerpio.teachme.service.SchoolService;
import com.slerpio.view.SchoolView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

import javax.inject.Inject;

/**
 * Fragment for showing all activity and school
 * @author kiditz
 */
public class ActivityFragment extends Fragment {
    public static final String TAG = ActivityFragment.class.getName();
    @BindView(R.id.schoolView)
    SchoolView schoolView;
    @Inject
    UserRepository userRepository;
    @Inject
    ImageService imageService;
    @Inject
    Retrofit retrofit;
    @Inject
    Translations translations;

    @NonNull
    private CompositeDisposable disposable = new CompositeDisposable();
    SchoolService schoolService;

    private User user;
    public ActivityFragment() {
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((App)getActivity().getApplication()).getNetOauthComponent().inject(this);
        this.user = userRepository.findUser();
        this.schoolService = retrofit.create(SchoolService.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_activity, container, false);
        ButterKnife.bind(this, v);
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        fillSchool();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        disposable.clear();
    }

    private void fillSchool() {
        Log.d(TAG, "schoolId: " + user.getSchool_id());
        if(user.getSchool_id() == null){
     //       schoolView.setVisibility(View.GONE);
        }else{
            Log.d(TAG, "fillSchool: ");
            Long schoolId = user.getSchool_id();
            disposable.add(schoolService.findSchoolById(schoolId).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(response ->{
                if(TeachmeApi.ok(response)){
                    Domain payload = TeachmeApi.payload(response);
                    schoolView.setSchoolName(payload.getString("name"));
                    imageService.loadDocument(schoolView.getSchoolImage(), payload.getLong("document_id"));
                }else{
                    Log.e(getClass().getName(), "error: " + response.toString());
                }
            }, error -> NetworkUtils.errorHandle(userRepository, translations, getActivity(), error)));
        }
    }
}
