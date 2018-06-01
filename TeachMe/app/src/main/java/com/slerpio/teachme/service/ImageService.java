package com.slerpio.teachme.service;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.slerpio.teachme.R;
import com.slerpio.teachme.helper.StreamUtils;
import com.slerpio.teachme.helper.image.CircleTransform;

import java.io.IOException;
import java.io.InputStream;

public class ImageService {
    private final Context context;
    private final SharedPreferences preferences;
    public static final String TAG = ImageService.class.getName();
    public ImageService(Context context, SharedPreferences preferences) {
        this.context = context;
        this.preferences = preferences;
    }

    public void loadImage(ImageView img, String username){
        String url = context.getString(R.string.teach_me_url);
        String accessToken = preferences.getString("token", "");
        String resourceUrl = url+ "teachme/get_image?access_token="+accessToken+"&username="+ username;
        Log.d(TAG, resourceUrl);
        Glide.with(context).load(resourceUrl).transform(new CircleTransform(context)).diskCacheStrategy(DiskCacheStrategy.ALL).into(new SimpleTarget<GlideDrawable>() {
            @Override
            public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                img.setImageDrawable(resource);
                img.setDrawingCacheEnabled(true);
            }
        });
    }

    public void loadDocument(ImageView img, Long documentId){
        String url = context.getString(R.string.teach_me_url);
        String accessToken = preferences.getString("token", "");
        String resourceUrl = url+ "teachme/get_document?access_token="+accessToken+"&id="+ documentId;
        Log.d(TAG, resourceUrl);
        Glide.with(context).load(resourceUrl).diskCacheStrategy(DiskCacheStrategy.ALL).into(new SimpleTarget<GlideDrawable>() {
            @Override
            public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                img.setImageDrawable(resource);
                img.setDrawingCacheEnabled(true);
            }
        });
    }
    public void loadThumbnails(ImageView img, Long documentId){
        String url = context.getString(R.string.teach_me_url);
        String accessToken = preferences.getString("token", "");
        String resourceUrl = url+ "teachme/get_document?access_token="+accessToken+"&id="+ documentId + "&thumbnails=true";
        Log.d(TAG, resourceUrl);
        Glide.with(context).load(resourceUrl).diskCacheStrategy(DiskCacheStrategy.ALL).into(new SimpleTarget<GlideDrawable>() {
            @Override
            public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                img.setImageDrawable(resource);
                img.setDrawingCacheEnabled(true);
            }
        });
    }
    public String toBase64(InputStream stream) throws IOException {
        byte[] bytes = StreamUtils.copyStreamToBytes(stream);
        return Base64.encodeToString(bytes, Base64.NO_WRAP);
    }

    public String getPathFromURI(Uri contentUri, Activity activity) {
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = activity.getContentResolver().query(contentUri, proj, "", null, "");
        if (cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }

    public void setViewDrawableColor(TextView textView, int color) {
        for (Drawable drawable : textView.getCompoundDrawables()) {
            if (drawable != null) {
                drawable.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN));
            }
        }
    }

    public void setViewDrawableColor(Menu menu, int color) {
        for(int i = 0; i < menu.size(); i++){
            Drawable drawable = menu.getItem(i).getIcon();
            if (drawable != null) {
                drawable.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN));
            }
        }
    }
}
