package com.slerpio.teachme.adapter;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.slerpio.teachme.R;
import com.slerpio.teachme.SchoolDetailActivity;
import com.slerpio.teachme.helper.IntentUtils;
import com.slerpio.teachme.model.Domain;
import com.slerpio.teachme.service.ImageService;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class SchoolAdapter extends RecyclerView.Adapter<SchoolAdapter.SchoolViewHolder> {
    private final List<Domain> schools;
    private final AppCompatActivity activity;
    private ImageService imageService;
    public SchoolAdapter(AppCompatActivity activity, List<Domain> activities) {
        this.activity = activity;
        this.schools = activities;
    }

    public void setImageService(ImageService imageService) {
        this.imageService = imageService;
    }

    @Override
    public SchoolViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View schoolView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.school_adapter, parent, false);
        return new SchoolViewHolder(schoolView);
    }

    @Override
    public void onBindViewHolder(SchoolViewHolder holder, int position) {
        Domain school = schools.get(position);
        holder.schoolName.setText(school.getString("name"));
        holder.schoolDescription.setText(StringUtils.abbreviate(school.getString("description"), 60));
        holder.schoolLayout.setOnClickListener(view ->{
            Bundle bundle = new Bundle();
            bundle.putString("school", school.toString());
            IntentUtils.moveTo(activity, SchoolDetailActivity.class, bundle);
        });
        imageService.loadDocument(holder.schoolImage, school.getLong("document_id"));
    }

    @Override
    public int getItemCount() {
        return schools.size();
    }


    class SchoolViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.schoolName)
        TextView schoolName;
        @BindView(R.id.schoolDescription)
        TextView schoolDescription;
        @BindView(R.id.schoolImage)
        ImageView schoolImage;
        @BindView(R.id.schoolLayout)
        RelativeLayout schoolLayout;
        public SchoolViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
