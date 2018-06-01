package com.slerpio.teachme.helper;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import com.slerpio.teachme.model.Domain;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

/**
 * Created by kiditz on 22/10/17.
 */

public class Translations {
    private final Context context;
    private Domain domain;

    public Translations(Context context) {
        this(context, getCurrentLocale(context).getLanguage());
    }

    public Translations(Context context, String language) {
        this.context = context;
        if(language == null){
            language = "";
        }
        String filename ="translations_"+ language +".json";
        Log.i("Sys lang >>> ", language);
        try {
            InputStream in = context.getAssets().open(filename);
            String data = StreamUtils.copyStreamToString(in);
            this.domain =new Domain(data);
        } catch (Exception e) {
            try {
                InputStream in = context.getAssets().open("translations.json");
                String data = StreamUtils.copyStreamToString(in);
                this.domain =new Domain(data);
            } catch (IOException e1) {
                e1.printStackTrace();
                throw new RuntimeException("Please set "+filename+" in assets");
            }
        }
    }

    public String get(String key){
        return this.domain.containsKey(key) ? domain.get(key).toString() : key;
    }
    public boolean has(String key){
        return this.domain.containsKey(key);
    }

    public Domain getDomain() {
        return domain;
    }

    public static final Locale getCurrentLocale(Context context){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            return context.getResources().getConfiguration().getLocales().get(0);
        } else{
            //noinspection deprecation
            return context.getResources().getConfiguration().locale;
        }
    }
}
