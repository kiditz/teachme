package com.slerpio.teachme.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.bumptech.glide.Glide;
import com.jakewharton.rxbinding2.view.RxView;
import com.slerpio.teachme.MaterialTypeWriteActivity;
import com.slerpio.teachme.R;
import static com.slerpio.teachme.helper.GlobalConstant.MATERIAL_TYPE_PDF;
import static com.slerpio.teachme.helper.GlobalConstant.MATERIAL_TYPE_WRITE;
import static com.slerpio.teachme.helper.GlobalConstant.MATERIAL_TYPE_VIDEO;

import com.slerpio.teachme.helper.IntentUtils;
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
        RxView.clicks(holder.materialTypeLayout).subscribe(view -> {
            if(type.getString("type").equals(MATERIAL_TYPE_WRITE)){
                IntentUtils.moveTo(context, MaterialTypeWriteActivity.class);
            }else if(type.getString("type").equals(MATERIAL_TYPE_VIDEO)){
                //TODO Handle type video
            }else if(type.getString("type").equals(MATERIAL_TYPE_PDF)){
                //TODO Handle type pdf
            }
        });

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
        @BindView(R.id.materialTypeLayout)
        LinearLayout materialTypeLayout;
        public MaterialTypeViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
