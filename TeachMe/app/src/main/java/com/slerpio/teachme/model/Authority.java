package com.slerpio.teachme.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Authority extends RealmObject{
    public static final String ADD_SCHOOL = "ADD_SCHOOL";
    public static final String VIEW_PROFILE = "VIEW_PROFILE";
    public static final String EDIT_PROFILE = "EDIT_PROFILE";
    @PrimaryKey
    private int id;
    private String authority;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }
}
