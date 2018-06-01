package com.slerpio.teachme.helper;

import com.slerpio.teachme.model.Domain;

import java.util.List;

public class TeachmeApi {

    public static boolean ok(Domain domain) {
        return domain.getString("status").equals("OK");
    }

    public static boolean fail(Domain domain) {
        return domain.getString("status").equals("FAIL");
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
