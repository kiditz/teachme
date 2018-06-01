package com.slerpio.teachme.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.slerpio.teachme.R;
import com.slerpio.teachme.model.Domain;
import com.slerpio.teachme.service.ImageService;

import java.util.List;

public class MaterialAdapter extends RecyclerView.Adapter<MaterialAdapter.MaterialViewHolder> {
    private final Context context;
    private List<Domain> materialList;
    RecyclerView.RecycledViewPool viewPool;
    private ImageService imageService;
    public MaterialAdapter(final Context context, List<Domain> materialList) {
        this.context = context;
        this.materialList = materialList;
        this.viewPool = new RecyclerView.RecycledViewPool();
    }

    public void setImageService(ImageService imageService) {
        this.imageService = imageService;
    }

    @Override
    public MaterialViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate( R.layout.material_adapter, parent, false);
        return new MaterialViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MaterialViewHolder holder, int position) {
        Domain material = materialList.get(position);
        holder.title.setText(material.getString("title"));
        imageService.loadThumbnails(holder.image, material.getLong("document_id"));
    }

    @Override
    public int getItemCount() {
        return materialList.size();
    }

    class MaterialViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.materialTitle)
        TextView title;
        @BindView(R.id.materialImage)
        ImageView image;
        public MaterialViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
