package com.slerpio.teachme.helper;

import android.app.Activity;
import android.support.design.widget.Snackbar;
import android.util.Log;
import com.slerpio.teachme.LoginActivity;
import com.slerpio.teachme.R;
import com.slerpio.teachme.model.Domain;
import com.slerpio.teachme.realm.service.UserRepository;
import retrofit2.HttpException;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;

public class NetworkUtils {
    public static void errorHandle(UserRepository userService, Translations translations, Activity activity, Throwable error) {
        try {
            Log.e(activity.getClass().getName(), "Error: ", error);
            if (error instanceof HttpException) {
                HttpException ex = (HttpException) error;
                String errString = ex.response().errorBody().string();
                Log.e(activity.getClass().getName(), "Http: " + errString);
                Domain errorBody = new Domain(errString);
                if (ex.response().code() == 401 || ex.response().code() == 400) {
                    if (errorBody.getString("error").equals("invalid_token")) {
                        IntentUtils.moveTo(activity, LoginActivity.class);
                        activity.finish();
                        userService.removeUser();
                        return;
                    }
                    String errorMessage = translations.get(errorBody.getString("error_description"));
                    Snackbar.make(activity.findViewById(android.R.id.content), errorMessage, Snackbar.LENGTH_LONG).show();
                }else{
                    Snackbar.make(activity.findViewById(android.R.id.content), activity.getString(R.string.system_error), Snackbar.LENGTH_LONG).show();
                }
            } else if (error instanceof ConnectException || error instanceof SocketTimeoutException) {
                Snackbar.make(activity.findViewById(android.R.id.content), translations.get(GlobalConstant.CONNECTION_ERROR), Snackbar.LENGTH_LONG).show();
            }else{
                Snackbar.make(activity.findViewById(android.R.id.content), activity.getString(R.string.system_error), Snackbar.LENGTH_LONG).show();
            }
        } catch (IOException e) {
            Snackbar.make(activity.findViewById(android.R.id.content), activity.getString(R.string.system_error), Snackbar.LENGTH_LONG).show();
        }
    }
}
