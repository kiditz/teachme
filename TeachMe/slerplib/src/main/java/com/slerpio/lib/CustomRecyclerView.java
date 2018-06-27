package com.slerpio.lib;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static com.slerpio.lib.VideoUtils.getString;

/**
 * Created by krupenghetiya on 16/12/16.
 */

public class CustomRecyclerView extends RecyclerView {

    private Activity activity;
    private boolean playOnlyFirstVideo = false;
    private boolean downloadVideos = false;
    private boolean checkForMp4 = true;
    private float visiblePercent = 100.0f;
    private String downloadPath = Environment.getExternalStorageDirectory() + "/Video";

    public CustomRecyclerView(Context context) {
        super(context);
    }

    public CustomRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);


    }

    public CustomRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void setAdapter(Adapter adapter) {
        super.setAdapter(adapter);
        addCustomOnScrollListener();

    }

    private void addCustomOnScrollListener() {
        this.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(final RecyclerView recyclerView, final int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                playAvailableVideos(newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    public void playAvailableVideos(int newState) {
        HandlerThread handlerThread = new HandlerThread("DO_NOT_GIVE_UP", android.os.Process.THREAD_PRIORITY_BACKGROUND + android.os.Process.THREAD_PRIORITY_MORE_FAVORABLE);
        handlerThread.start();
        Looper looper = handlerThread.getLooper();
        Handler handler = new Handler(looper);
        List<Runnable> runnables = new ArrayList<>();
        if (newState == 0) {
            int firstVisiblePosition = ((LinearLayoutManager) getLayoutManager()).findFirstVisibleItemPosition();
            int lastVisiblePosition = ((LinearLayoutManager) getLayoutManager()).findLastVisibleItemPosition();
            if (firstVisiblePosition >= 0) {
                Rect rect_parent = new Rect();
                getGlobalVisibleRect(rect_parent);
                if (playOnlyFirstVideo) {
                    boolean foundFirstVideo = false;
                    for (int i = firstVisiblePosition; i <= lastVisiblePosition; i++) {
                        final RecyclerView.ViewHolder holder = findViewHolderForAdapterPosition(i);
                        try {
                            CustomViewHolder cvh = (CustomViewHolder) holder;
                            if (i >= 0 && cvh != null && cvh.getVideoUrl() != null && !cvh.getVideoUrl().equalsIgnoreCase("null") && (cvh.getVideoUrl().endsWith(".mp4") || !checkForMp4)) {
                                int[] location = new int[2];
                                cvh.getAah_vi().getLocationOnScreen(location);
                                Rect rect_child = new Rect(location[0], location[1], location[0] + cvh.getAah_vi().getWidth(), location[1] + cvh.getAah_vi().getHeight());
                                float rect_parent_area = (rect_child.right - rect_child.left) * (rect_child.bottom - rect_child.top);
                                float x_overlap = Math.max(0, Math.min(rect_child.right, rect_parent.right) - Math.max(rect_child.left, rect_parent.left));
                                float y_overlap = Math.max(0, Math.min(rect_child.bottom, rect_parent.bottom) - Math.max(rect_child.top, rect_parent.top));
                                float overlapArea = x_overlap * y_overlap;
                                float percent = (overlapArea / rect_parent_area) * 100.0f;
                                if (!foundFirstVideo && percent >= visiblePercent) {
                                    foundFirstVideo = true;
                                    if (getString(activity, cvh.getVideoUrl()) != null && new File(getString(activity, cvh.getVideoUrl())).exists()) {
                                        ((CustomViewHolder) holder).initVideoView(getString(activity, cvh.getVideoUrl()), activity);
                                    } else {
                                        ((CustomViewHolder) holder).initVideoView(cvh.getVideoUrl(), activity);
                                    }
                                    if (downloadVideos) {
                                        startDownloadInBackground(cvh.getVideoUrl());
                                    }
                                    Runnable myRunnable = new Runnable() {
                                        @Override
                                        public void run() {
                                            if (!((CustomViewHolder) holder).isPaused())
                                                ((CustomViewHolder) holder).playVideo();
                                        }
                                    };
                                    handler.post(myRunnable);
                                    runnables.add(myRunnable);
                                } else {
                                    ((CustomViewHolder) holder).pauseVideo();
                                }
                            }
                        } catch (Exception e) {
                            //Ignore
                        }
                    }
                } else {
                    for (int i = firstVisiblePosition; i <= lastVisiblePosition; i++) {
                        final RecyclerView.ViewHolder holder = findViewHolderForAdapterPosition(i);
                        try {
                            CustomViewHolder cvh = (CustomViewHolder) holder;
                            if (i >= 0 && cvh != null && (cvh.getVideoUrl().endsWith(".mp4") || !checkForMp4)) {
                                int[] location = new int[2];
                                cvh.getAah_vi().getLocationOnScreen(location);
                                Rect rect_child = new Rect(location[0], location[1], location[0] + cvh.getAah_vi().getWidth(), location[1] + cvh.getAah_vi().getHeight());
                                float rect_parent_area = (rect_child.right - rect_child.left) * (rect_child.bottom - rect_child.top);
                                float x_overlap = Math.max(0, Math.min(rect_child.right, rect_parent.right) - Math.max(rect_child.left, rect_parent.left));
                                float y_overlap = Math.max(0, Math.min(rect_child.bottom, rect_parent.bottom) - Math.max(rect_child.top, rect_parent.top));
                                float overlapArea = x_overlap * y_overlap;
                                float percent = (overlapArea / rect_parent_area) * 100.0f;
                                if (percent >= visiblePercent) {
                                    if (getString(activity, cvh.getVideoUrl()) != null && new File(getString(activity, cvh.getVideoUrl())).exists()) {
                                        ((CustomViewHolder) holder).initVideoView(getString(activity, cvh.getVideoUrl()), activity);
                                    } else {
                                        ((CustomViewHolder) holder).initVideoView(cvh.getVideoUrl(), activity);
                                    }
                                    if (downloadVideos) {
                                        startDownloadInBackground(cvh.getVideoUrl());
                                    }
                                    Runnable myRunnable = new Runnable() {
                                        @Override
                                        public void run() {
                                            if (!((CustomViewHolder) holder).isPaused())
                                                ((CustomViewHolder) holder).playVideo();
                                        }
                                    };
                                    handler.post(myRunnable);
                                    runnables.add(myRunnable);
                                } else {
                                    ((CustomViewHolder) holder).pauseVideo();
                                }
                            }
                        } catch (Exception ignored) {

                        }

                    }
                }
            }
        } else if (runnables.size() > 0) {
            for (Runnable t : runnables) {
                handler.removeCallbacksAndMessages(t);
            }
            runnables.clear();
            handlerThread.quit();
        }
    }


    public void setPlayOnlyFirstVideo(boolean playOnlyFirstVideo) {
        this.playOnlyFirstVideo = playOnlyFirstVideo;
    }

    @Override
    public boolean getGlobalVisibleRect(Rect r, Point globalOffset) {
        return super.getGlobalVisibleRect(r, globalOffset);
    }

    public void startDownloadInBackground(String url) {
        if (!VideoUtils.isConnected(activity)) return;
        /* Starting Download Service */
        if ((VideoUtils.getString(activity, url) == null || !(new File(getString(activity, url)).exists())) && url != null && !url.equalsIgnoreCase("null")) {
            Intent intent = new Intent(Intent.ACTION_SYNC, null, activity, DownloadService.class);
            intent.putExtra("url", url);
            intent.putExtra("path", downloadPath);
            intent.putExtra("requestId", 101);
            activity.startService(intent);
        }
    }

    public void setDownloadVideos(boolean downloadVideos) {
        this.downloadVideos = downloadVideos;
    }

    public void setDownloadPath(String downloadPath) {
        this.downloadPath = downloadPath;
    }

    public void preDownload(List<String> urls) {
        if (!VideoUtils.isConnected(activity)) return;
        HashSet<String> hashSet = new HashSet<>();
        hashSet.addAll(urls);
        urls.clear();
        urls.addAll(hashSet);
        for (String url : urls) {
            if ((VideoUtils.getString(activity, url) == null || !(new File(getString(activity, url)).exists())) && url != null && !url.equalsIgnoreCase("null")) {
                Intent intent = new Intent(Intent.ACTION_SYNC, null, activity, DownloadService.class);
                intent.putExtra("url", url);
                intent.putExtra("path", downloadPath);
                intent.putExtra("requestId", 101);
                activity.startService(intent);
            }
        }
    }

    public void setCheckForMp4(boolean checkForMp4) {
        this.checkForMp4 = checkForMp4;
    }


    public void stopVideos() {
        for (int i = 0; i < getChildCount(); i++) {
            if (findViewHolderForAdapterPosition(i) instanceof CustomViewHolder) {
                final CustomViewHolder cvh = (CustomViewHolder) findViewHolderForAdapterPosition(i);
                if (cvh != null && cvh.getVideoUrl() != null && !cvh.getVideoUrl().equalsIgnoreCase("null") && !cvh.getVideoUrl().isEmpty() && (cvh.getVideoUrl().endsWith(".mp4") || !checkForMp4)) {
                    cvh.pauseVideo();
                }
            }
        }
    }

    public void setVisiblePercent(float visiblePercent) {
        this.visiblePercent = visiblePercent;
    }
}