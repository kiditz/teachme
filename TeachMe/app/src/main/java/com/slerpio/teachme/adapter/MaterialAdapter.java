package com.slerpio.teachme.adapter;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.slerpio.teachme.MaterialDetailActivity;
import com.slerpio.teachme.R;
import com.slerpio.teachme.helper.GlobalConstant;
import com.slerpio.teachme.helper.IntentUtils;
import com.slerpio.teachme.helper.MimeUtils;
import com.slerpio.teachme.model.Domain;
import com.slerpio.teachme.service.ImageService;

import java.io.File;
import java.util.List;

@SuppressWarnings("FieldCanBeLocal")
public class MaterialAdapter extends RecyclerView.Adapter<MaterialAdapter.MaterialViewHolder> {
    private final Activity context;
    private List<Domain> materialList;
    RecyclerView.RecycledViewPool viewPool;
    private ImageService imageService;


    public MaterialAdapter(final Activity context, List<Domain> materialList) {
        this.context = context;
        this.materialList = materialList;
        this.viewPool = new RecyclerView.RecycledViewPool();
    }



    public void setImageService(ImageService imageService) {
        this.imageService = imageService;
    }


    @Override
    public MaterialViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.material_adapter, parent, false);
        return new MaterialViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MaterialViewHolder holder, int position) {
        Domain material = materialList.get(position);
        holder.title.setText(material.getString("title"));
        if (material.getString("type").equals(GlobalConstant.MATERIAL_TYPE_WRITE)) {
            imageService.loadMaterialImage(holder.image, material.getLong("id"));
        } else {
            imageService.loadThumbnails(holder.image, material.getLong("document_id"));
        }
        holder.itemView.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("material", material.toString());
            Log.e(getClass().getName(), "MaterialViewHolder: " + material.toString());
            if (material.getString("type").equals(GlobalConstant.MATERIAL_TYPE_WRITE)) {
                IntentUtils.moveTo(context, MaterialDetailActivity.class, bundle);
            } else {
                //TODO: Render Video
            }
        });

    }

    @Override
    public int getItemCount() {
        return materialList.size();
    }

    class MaterialViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.materialTitle)
        TextView title;
        @BindView(R.id.materialImage)
        ImageView image;
        private View itemView;

        public MaterialViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.itemView = itemView;
        }
    }

    private void readFileFromChooser(File file) {

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file), MimeUtils.guessMimeTypeFromExtension("pdf"));
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        Intent chooser = Intent.createChooser(intent, context.getString(R.string.read_file_via));
        context.startActivity(chooser);
    }
}
