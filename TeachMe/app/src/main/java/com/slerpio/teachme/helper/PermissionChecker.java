package com.slerpio.teachme.helper;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;

import static android.Manifest.permission.*;

public class PermissionChecker {
    public static final String[] PERMISSIONS = new String[]{
            READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE, INTERNET, ACCESS_NETWORK_STATE, ACCESS_WIFI_STATE
    };
    public static void checkAndRequestPermissions(Activity context){
        if(!hasPermissions(context)){
            ActivityCompat.requestPermissions(context, PERMISSIONS, 1);
        }
    }
    public static boolean hasPermissions(Context context) {
      return hasPermissions(context, PERMISSIONS);
    }
    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
}
