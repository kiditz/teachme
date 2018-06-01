package com.slerpio.teachme.helper;

import android.app.Activity;
import android.view.MenuItem;

public class BackPressed {
    public static boolean home(MenuItem item, Activity activity){
        if (item.getItemId() == android.R.id.home){
            activity.onBackPressed();
            return true;
        }
        return false;
    }
}
