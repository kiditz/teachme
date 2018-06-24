package com.slerpio.teachme.adapter;


import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.slerpio.teachme.MaterialDetailActivity;
import com.slerpio.teachme.R;
import com.slerpio.teachme.helper.DateUtils;
import com.slerpio.teachme.helper.IntentUtils;
import com.slerpio.teachme.helper.ViewUtils;
import com.slerpio.teachme.model.Domain;
import com.slerpio.teachme.service.ImageService;

import java.util.List;

public class MySubmissionAdapter extends RecyclerView.Adapter<MySubmissionAdapter.SubmissionViewHolder> {
    private final List<Domain> materials;
    private final Activity activity;
    private ImageService imageService;
    public MySubmissionAdapter(Activity activity, List<Domain> materials) {
        this.activity = activity;
        this.materials = materials;
    }

    public void setImageService(ImageService imageService) {
        this.imageService = imageService;
    }

    @Override
    public SubmissionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View cardActivity = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.my_submission_material_adapter, parent, false);
        return new SubmissionViewHolder(cardActivity);
    }

    @Override
    public void onBindViewHolder(SubmissionViewHolder holder, int position) {
        Domain material = materials.get(position);

        imageService.loadMaterialImage(holder.viewImage, material.getLong("id"));

        holder.title.setText(material.getString("title"));

        holder.active.setGravity(Gravity.CENTER_VERTICAL);
        holder.active.setCompoundDrawablesWithIntrinsicBounds( R.drawable.ic_check,0 , 0, 0);
        ViewUtils.setViewDrawableColor(holder.active, activity.getResources().getColor(R.color.colorAccent));
        boolean active = material.getBoolean("active");
        if(active){

            holder.active.setText("Di setujui");
        }else{
            holder.active.setText("Dalam Proses");
        }

        holder.timeAgo.setText(DateUtils.getTimeAgo(material.getLong("created_at")));
        Bundle bundle = new Bundle();
        bundle.putString("material", material.toString());
        holder.rawView.setOnClickListener(v -> IntentUtils.moveTo(activity, MaterialDetailActivity.class, bundle));
    }

    @Override
    public int getItemCount() {
        return materials.size();
    }


    class SubmissionViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.active)
        TextView active;

        @BindView(R.id.timeAgo)
        TextView timeAgo;
        @BindView(R.id.viewImage)
        ImageView viewImage;

        @BindView(R.id.rawView)
        LinearLayout rawView;
        public SubmissionViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
