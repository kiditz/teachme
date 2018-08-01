package com.slerpio.teachme.realm.service;

import com.slerpio.teachme.model.Authority;
import com.slerpio.lib.core.Domain;
import com.slerpio.teachme.model.User;
import io.realm.Realm;
import io.realm.RealmList;

import java.util.List;

public class UserRepository {
    private final Realm realm;

    public UserRepository(final Realm realm) {
        this.realm = realm;
    }

    public void addUser(String accessToken, String refreshToken, String username, List<String> authorities, int expires){
        realm.beginTransaction();
        User user = realm.where(User.class).findFirst();
        if(user == null){
            user = realm.createObject(User.class, 1);
        }
        user.setUsername(username);
        user.setAccessToken(accessToken);
        user.setRefreshToken(refreshToken);
        user.setExpiresIn(expires);
        RealmList<Authority> authoritiesObjects = new RealmList<>();
        for (String authorityStr : authorities) {
            Authority authority = realm.createObject(Authority.class, realm.where(Authority.class).findAll().size());
            authority.setAuthority(authorityStr);
            authoritiesObjects.add(authority);
        }
        user.setAuthorities(authoritiesObjects);
        realm.commitTransaction();
    }

    public User findUser(){
        return realm.where(User.class).findFirst();
    }

    public User updateUser(Domain teacherDomain){
        Domain userDomain = teacherDomain.getDomain("user");
        userDomain.put("id", 1L);
        realm.beginTransaction();
        User user = realm.createOrUpdateObjectFromJson(User.class, userDomain.toString());
        if(teacherDomain.containsKey("school_id"))
            user.setSchool_id(teacherDomain.getLong("school_id"));
        if(teacherDomain.containsKey("level_id"))
            user.setLevel_id(teacherDomain.getLong("level_id"));
        if(teacherDomain.containsKey("class_id"))
            user.setClass_id(teacherDomain.getLong("class_id"));
        user.setUser_id(teacherDomain.getLong("user_id"));
        realm.commitTransaction();
        return user;
    }
    public User updateUser(String json){
        realm.beginTransaction();
        User user = realm.createOrUpdateObjectFromJson(User.class, json);
        user.setUser_id((long)user.getId());
        realm.commitTransaction();
        return user;
    }
    public Boolean hasLogin(String username){
        return realm.where(User.class).equalTo("username", username).findFirst() != null;
    }

    public User findUser(String username){
        return realm.where(User.class).equalTo("username", username).findFirst();
    }

    public void removeUser(){
        realm.beginTransaction();
        User user = realm.where(User.class).findFirst();
        if(user != null){
            user.deleteFromRealm();
        }
        realm.commitTransaction();
    }

    public boolean hasAuthority(String authority){
        User user = findUser();
        for (Authority item: user.getAuthorities()) {
            if(item.getAuthority().equals(authority)){
                return true;
            }
        }
        return false;
    }
}
