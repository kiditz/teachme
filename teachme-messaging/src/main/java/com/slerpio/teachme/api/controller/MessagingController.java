package com.slerpio.teachme.api.controller;

import org.slerp.core.Domain;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public class MessagingController {
    /**
     * {
     *     "deviceId": "?",
     *     "model": "",
     *     "manufactured: ""
     * }
     * */
    @PostMapping
    public void registerDevice(@RequestBody Domain input){

    }


}
