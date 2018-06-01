package com.slerpio.teachme.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.slerpio.teachme.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class BlankFragment extends Fragment {

    @BindView(R.id.todoTextView)
    protected TextView toDoTextView;

    public BlankFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_blank, container, false);
        ButterKnife.bind(this, v);
        if(getArguments() != null && getArguments().containsKey("title")){
            toDoTextView.setText(getArguments().getString("title"));
        }

        return v;
    }

}
