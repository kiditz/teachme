package com.slerpio.teachme.helper;

import com.slerpio.lib.core.Domain;

import java.util.List;

public class TeachmeApi {
    static public final int SIZE = 10;
    public static boolean ok(Domain domain) {
        return domain.getString("status") != null && domain.getString("status").equals("OK");
    }

    public static boolean fail(Domain domain) {
        return domain.getString("status") != null && domain.getString("status").equals("FAIL");
    }

    public static Domain payload(Domain domain) {
        return domain.getDomain("payload");
    }
    public static List<Domain> payloads(Domain domain) {
        return domain.getList("payload");
    }

    public static String getError(Domain response){
        String message = response.getString("message");
        if(response.containsKey("key")){
            message += "." + response.getString("key");
        }
        return message;
    }


}
