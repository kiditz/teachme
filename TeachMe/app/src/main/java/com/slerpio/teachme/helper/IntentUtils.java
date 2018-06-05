package com.slerpio.teachme.helper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by kiditz on 28/10/17.
 */

public class IntentUtils {
    public static void moveTo(Context src, Class<? extends Activity> destination){
        moveTo(src, destination, null);
    }
    public static void moveTo(Context src, Class<? extends Activity> destination, Bundle bundle){
        Intent i = new Intent(src, destination);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if(bundle != null)
            i.putExtras(bundle);
        src.startActivity(i);
    }
    public static void moveTo(Context src, Intent intent){
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        src.startActivity(intent);
    }

    public static void openDocument(Activity activity, int code, String mimeType){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType(mimeType);
        activity.startActivityForResult(intent, code);
    }
}
