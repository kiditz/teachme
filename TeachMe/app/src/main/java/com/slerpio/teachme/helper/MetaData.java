package com.slerpio.teachme.helper;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

public class MetaData {
    private Bundle bundle;
    public MetaData(final Activity activity) {
        try {
            ApplicationInfo applicationInfo = activity.getPackageManager().getApplicationInfo(activity.getPackageName(), PackageManager.GET_META_DATA);
            this.bundle = applicationInfo.metaData;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    public String getClientId(){
        return bundle.getString("client_id");
    }
    public String getClientSecret(){
        return bundle.getString("client_secret");
    }

}
