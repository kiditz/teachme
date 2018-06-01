package com.slerpio.teachme.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.slerpio.teachme.R;
import com.slerpio.teachme.model.Domain;

/**
 * A simple {@link Fragment} subclass.
 */
public class SchoolDetailFragment extends Fragment {
    private Domain school;
    public SchoolDetailFragment() {
        // Required empty public constructor
    }
    @BindView(R.id.schoolAddress)
    TextView schoolAddress;
    @BindView(R.id.schoolPhoneNumber)
    TextView schoolPhoneNumber;
    @BindView(R.id.schoolWebUrl)
    TextView schoolWebUrl;
    @BindView(R.id.schoolDescription)
    TextView schoolDescription;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        school = new Domain(getArguments().getString("school"));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_school_detail, container, false);
        ButterKnife.bind(this, v);

        schoolAddress.setText(school.getDomain("address").getString("address"));
        schoolDescription.setText(school.getString("description"));
        schoolWebUrl.setText(school.getString("url"));
        schoolPhoneNumber.setText("087788044378");
        return v;
    }

}
