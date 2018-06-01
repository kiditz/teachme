package com.slerpio.teachme.helper;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class MultipartUtils {
    public static RequestBody createValue(String value){
        return RequestBody.create(MultipartBody.FORM, value);
    }

    public static MultipartBody.Part createFile(String key, Intent data, Activity activity) throws IOException {
        Uri uri = data.getData();
        InputStream in = activity.getContentResolver().openInputStream(uri);
        String filename = StreamUtils.getFileNameFromIntentData(activity, uri);
        RequestBody reqFile = RequestBody.create(
                MediaType.parse(activity.getContentResolver().getType(uri)),
                StreamUtils.copyStreamToBytes(in)
        );
        MultipartBody.Part body = MultipartBody.Part.createFormData(key, filename, reqFile);
        return body;
    }
}
