package com.slerpio.teachme.adapter;


import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.slerpio.teachme.R;
import com.slerpio.teachme.model.Domain;

import java.util.List;

public class ActivityAdapter extends RecyclerView.Adapter<ActivityAdapter.ActivityViewHolder> {
    private final List<Domain> activities;
    private final AppCompatActivity activity;
    public ActivityAdapter(AppCompatActivity activity, List<Domain> activities) {
        this.activity = activity;
        this.activities = activities;
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
        holder.title.setText(activity.getString("title"));
        holder.date.setText(activity.getString("date"));
        holder.hours.setText(activity.getString("hours"));
        holder.message.setText(activity.getString("message"));
    }

    @Override
    public int getItemCount() {
        return activities.size();
    }


    class ActivityViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.date)
        TextView date;
        @BindView(R.id.hours)
        TextView hours;
        @BindView(R.id.message)
        TextView message;
        public ActivityViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
