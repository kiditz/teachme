package com.slerpio.teachme.helper;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import com.slerpio.teachme.R;

/**
 * Created by kiditz on 06/11/17.
 */

public class FragmentUtils {
    public static void moveTo(Fragment fragment, Context context){
        FragmentManager manager = ((AppCompatActivity) context ).getSupportFragmentManager();
        FragmentTransaction tr = manager.beginTransaction();
        tr.replace(R.id.containerBody, fragment);
        tr.commit();
    }

}
