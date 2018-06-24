package com.slerpio.teachme.adapter;


import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import com.slerpio.teachme.helper.*;
import com.slerpio.teachme.helper.image.CircleImageView;
import com.slerpio.teachme.model.Domain;
import com.slerpio.teachme.service.ImageService;
import com.slerpio.teachme.service.MaterialService;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class ActivityAdapter extends RecyclerView.Adapter<ActivityAdapter.ActivityViewHolder> {
    private final List<Domain> activities;
    private final Activity activity;
    private ImageService imageService;
    private MaterialService materialService;
    public ActivityAdapter(Activity activity, List<Domain> activities) {
        this.activity = activity;
        this.activities = activities;
    }

    public void setImageService(ImageService imageService) {
        this.imageService = imageService;
    }

    public void setMaterialService(MaterialService materialService) {
        this.materialService = materialService;
    }

    @Override
    public ActivityViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View cardActivity = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_adapter, parent, false);
        return new ActivityViewHolder(cardActivity);
    }

    @Override
    public void onBindViewHolder(ActivityViewHolder holder, int position) {
        Domain activity = activities.get(position);
        Domain user = activity.getDomain("user");

        holder.username.setText(user.getString("username"));
        Long createdAt = activity.getLong("created_at");
        Log.d(getClass().getName(), "onBindViewHolder: " + createdAt);
        holder.timeAgo.setText(DateUtils.getTimeAgo(createdAt));
        holder.message.setText(activity.getString("message"));
        imageService.loadUserImage(holder.profileImage, user.getString("username"));
        if(!StringUtils.isEmpty(activity.getString("raw"))){
            holder.rawView.setVisibility(View.VISIBLE);
            Domain raw = new Domain(activity.getString("raw"));
            if(activity.containsKey("doc_type")){
                if(activity.getString("doc_type").equals(GlobalConstant.ACTIVITY_TYPE_MATERIAL)){
                    imageService.loadMaterialImage(holder.viewImage, raw.getLong("id"));
                    materialService.findMaterial(raw).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(response->{
                        if(TeachmeApi.ok(response)){
                            Domain payload = TeachmeApi.payload(response);
                            holder.title.setText(payload.getString("title"));
                            holder.view.setGravity(Gravity.CENTER_VERTICAL);
                            boolean active = payload.getBoolean("active");
                            holder.view.setCompoundDrawablesWithIntrinsicBounds( R.drawable.ic_check,0 , 0, 0);
                            ViewUtils.setViewDrawableColor(holder.view, ActivityAdapter.this.activity.getResources().getColor(R.color.colorAccent));
                            if(active){
                                holder.view.setText("Di setujui");
                            }else{
                                holder.view.setText("Dalam Proses");
                            }
                            holder.rawView.setOnClickListener(v -> {
                                Bundle bundle = new Bundle();
                                bundle.putString("material", payload.toString());
                                IntentUtils.moveTo(ActivityAdapter.this.activity, MaterialDetailActivity.class, bundle);
                            });

                        }
                    });
                }
            }
        }else{
            holder.rawView.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return activities.size();
    }


    class ActivityViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.username)
        TextView username;
        @BindView(R.id.profileImage)
        CircleImageView profileImage;
        @BindView(R.id.timeAgo)
        TextView timeAgo;
        @BindView(R.id.view)
        TextView view;
        @BindView(R.id.message)
        TextView message;
        @BindView(R.id.rawView)
        LinearLayout rawView;
        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.viewImage)
        ImageView viewImage;
        public ActivityViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
