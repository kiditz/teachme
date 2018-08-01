package com.slerpio.teachme.helper;

import android.util.Base64;
import com.slerpio.lib.core.Domain;

import java.nio.charset.Charset;

public class OauthCredentialGenerator {

    public static String generateCredentials(String username, String password){
        byte[] data = username.concat(":").concat(password).getBytes();
        return Base64.encodeToString(data, Base64.NO_WRAP);
    }
    public static Domain decodeBody(String token){
        String[] split = token.split("\\.");
        byte[] resBody = Base64.decode(split[1],Base64.NO_WRAP);
        return new Domain(new String(resBody, Charset.forName("UTF-8")));
    }
}