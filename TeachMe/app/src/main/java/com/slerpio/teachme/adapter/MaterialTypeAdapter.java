package com.slerpio.teachme.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.bumptech.glide.Glide;
import com.slerpio.teachme.R;
import com.slerpio.teachme.model.Domain;

import java.util.List;

public class MaterialTypeAdapter extends RecyclerView.Adapter<MaterialTypeAdapter.MaterialTypeViewHolder> {
    private List<Domain> materialTypeList;
    private Activity context;
    public MaterialTypeAdapter(Activity context,List<Domain> materialTypeList) {
        this.materialTypeList = materialTypeList;
        this.context = context;
    }

    @Override
    public MaterialTypeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.material_type_adapter, parent, false);
        return new MaterialTypeViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MaterialTypeViewHolder holder, int position) {
        Domain type = materialTypeList.get(position);
        int resourceId = type.getInt("resourceId");
        Glide.with(context).load("").placeholder(resourceId).into(holder.materialTypeImage);
        holder.materialTypeName.setText(type.getString("name"));
    }

    @Override
    public int getItemCount() {
        return materialTypeList.size();
    }

    class MaterialTypeViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.materialTypeImage)
        ImageView materialTypeImage;
        @BindView(R.id.materialTypeName)
        TextView materialTypeName;
        public MaterialTypeViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
