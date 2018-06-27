package com.slerpio.lib;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SchoolView extends LinearLayout {
    ImageView schoolImage;
    TextView schoolName;

    public SchoolView(Context context) {
        super(context);
    }

    public SchoolView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.SchoolView, 0, 0);
        setOrientation(LinearLayout.VERTICAL);
        LayoutInflater.from(context).inflate(R.layout.school_header, this, true);
        init(array);
        array.recycle();

    }

    private void init(TypedArray array) {
        this.schoolImage = findViewById(R.id.schoolImage);
        this.schoolName = findViewById(R.id.schoolName);
        this.schoolImage.setImageDrawable(array.getDrawable(R.styleable.SchoolView_schoolImage));
        this.schoolName.setText(array.getString(R.styleable.SchoolView_schoolName));
    }

    public void setSchoolName(CharSequence schoolName){
        this.schoolName.setText(schoolName);
    }

    public void setSchoolImage(Bitmap bitmap){
        this.schoolImage.setImageBitmap(bitmap);
    }

    public void setSchoolImage(Drawable bitmap){
        this.schoolImage.setImageDrawable(bitmap);
    }

    public ImageView getSchoolImage() {
        return schoolImage;
    }

    public TextView getSchoolName() {
        return schoolName;
    }
}
