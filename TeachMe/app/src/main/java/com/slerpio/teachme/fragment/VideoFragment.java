package com.slerpio.teachme.fragment;


import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import com.slerpio.teachme.App;
import com.slerpio.teachme.R;
import com.slerpio.teachme.adapter.VideoAdapter;
import com.slerpio.lib.CustomRecyclerView;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class VideoFragment extends Fragment {

    @Inject
    SharedPreferences preferences;
    public VideoFragment() {
        // Required empty public constructor
    }

    CustomRecyclerView recycler;
    VideoAdapter adapter;

    @Override
    public void onAttach(Activity context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((App)getActivity().getApplicationContext()).getNetOauthComponent().inject(this);
        adapter = new VideoAdapter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_video, container, false);
        ButterKnife.bind(this, v);
        recycler = v.findViewById(R.id.rv_home);
        recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        recycler.setItemAnimator(new DefaultItemAnimator());
        recycler.setActivity(getActivity());
        recycler.setPlayOnlyFirstVideo(true); // false by default
        recycler.setCheckForMp4(false); //true by default
        recycler.setDownloadPath(Environment.getExternalStorageDirectory() + "/MyVideo"); // (Environment.getExternalStorageDirectory() + "/Video") by default
        recycler.setDownloadVideos(true);
        recycler.setVisiblePercent(50);
        String accessToken = preferences.getString("token", "");
        String resourceUrl = getString(R.string.teach_me_url) + "teachme/find_document_by_id?access_token="+accessToken+"&id="+ 1;
        recycler.setAdapter(adapter);
        adapter.add(resourceUrl);
        adapter.add("http://res.cloudinary.com/krupen/video/upload/w_300,h_150,c_crop,q_70/v1481795675/3_yqeudi.mp4");
        List<String> list = new ArrayList<>();
        list.add(resourceUrl);
        list.add("http://res.cloudinary.com/krupen/video/upload/w_300,h_150,c_crop,q_70/v1481795675/3_yqeudi.mp4");
        recycler.preDownload(list);
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
