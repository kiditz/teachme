package com.slerpio.teachme.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.slerpio.teachme.R;
import com.slerpio.teachme.model.Domain;
import com.slerpio.view.CustomViewHolder;
import com.slerpio.view.VideosAdapter;

import java.util.ArrayList;
import java.util.List;

public class VideoAdapter extends VideosAdapter {
    final List<Domain> videos = new ArrayList<>();

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_card, parent, false);
        return new VideoViewHolder(view);
    }

    public void add(String url){
        videos.add(new Domain().put("url", url));
        notifyDataSetChanged();
    }
    @Override
    public int getItemCount() {
        return videos.size();
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        Domain video = videos.get(position);
        holder.setVideoUrl(video.getString("url"));

    }



    class VideoViewHolder extends CustomViewHolder {
        @BindView(R.id.img_vol)
        ImageView imgVol;
        public VideoViewHolder(View x) {
            super(x);
            ButterKnife.bind(this, x);
        }

        @Override
        public void videoStarted() {
            super.videoStarted();
        }

        @Override
        public void pauseVideo() {
            super.pauseVideo();
        }
    }

}
