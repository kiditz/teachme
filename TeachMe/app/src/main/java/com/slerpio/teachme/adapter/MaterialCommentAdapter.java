package com.slerpio.teachme.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.*;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.slerpio.lib.core.DateUtils;
import com.slerpio.lib.core.Domain;
import com.slerpio.teachme.R;
import com.slerpio.teachme.service.ImageService;

import java.util.List;

public class MaterialCommentAdapter extends RecyclerView.Adapter<MaterialCommentAdapter.MaterialCommentViewHolder> {
    private List<Domain> comments;
    private Activity context;
    private ImageService imageService;
    public MaterialCommentAdapter(Activity context, List<Domain> comments) {
        this.comments = comments;
        this.context = context;
    }

    public void setImageService(ImageService imageService) {
        this.imageService = imageService;
    }

    @NonNull
    @Override
    public MaterialCommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_material_adapter, parent, false);
        return new MaterialCommentViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MaterialCommentViewHolder holder, int position) {
        Domain comment = comments.get(position);
        final String username = comment.getDomain("user").getString("username");
        Spannable spannable = (Spannable)Html.fromHtml(getMessage(username, comment.getString("message")));
        holder.time.setText(DateUtils.getTimeAgo(comment.getLong("created_at")));
        int start = spannable.toString().indexOf("@");
        int end = spannable.toString().indexOf(" ");

        if(imageService != null){
            imageService.loadUserImage(holder.profileImage, username);
        }

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Toast.makeText(context, "Clicked", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        };

        spannable.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        holder.message.setMovementMethod(LinkMovementMethod.getInstance());
        holder.message.setClickable(true);
        holder.message.setText(spannable);

    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    class MaterialCommentViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.time)
        TextView time;
        @BindView(R.id.message)
        TextView message;
        @BindView(R.id.profileImage)
        ImageView profileImage;
        public MaterialCommentViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    private String getMessage(String username, String message){
        String color = "#" + Integer.toHexString(ContextCompat.getColor(context, R.color.colorAccent) & 0x00ffffff);
        StringBuilder builder = new StringBuilder();
        builder.append("<font color=\"").append(color).append("\">")
                .append("@").append(username)
                .append("</font>")
                .append(" ")
                .append(message);
        return builder.toString();
    }

}
