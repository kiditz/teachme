package com.slerpio.teachme;

import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import butterknife.BindView;
import butterknife.ButterKnife;

import java.util.ArrayList;
import java.util.List;

public class AddMaterialActivity extends AppCompatActivity{
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
    private ArrayAdapter<String> adapter;
    private List<String> topicList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_material);
        ButterKnife.bind(this);
        this.type = getIntent().getStringExtra("type");
        this.adapter =new ArrayAdapter<>(this, android.R.layout.select_dialog_item, topicList);
        this.topic.setThreshold(1);
        this.topic.setAdapter(adapter);
        fillTopic();
        validateInput();
    }

    private void fillTopic() {

    }

    private void validateInput() {

    }
}
