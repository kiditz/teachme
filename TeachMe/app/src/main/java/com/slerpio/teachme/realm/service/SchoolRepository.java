package com.slerpio.teachme.realm.service;

import com.slerpio.teachme.model.Address;
import com.slerpio.teachme.model.Domain;
import com.slerpio.teachme.model.School;
import io.realm.Realm;
import io.realm.RealmResults;

import java.util.ArrayList;
import java.util.List;

public class SchoolRepository {
    private final Realm realm;

    public SchoolRepository(Realm realm) {
        this.realm = realm;
    }

    public void add(String json){
        realm.beginTransaction();
        realm.delete(Address.class);
        realm.delete(School.class);
        realm.createAllFromJson(School.class, json);
        realm.commitTransaction();
    }

    public List<Domain> findAll(){
        RealmResults<School> schools = realm.where(School.class).findAll();
        List<School> schoolList = realm.copyFromRealm(schools);
        List<Domain> schoolListDomain = new ArrayList<>();
        for (School school : schoolList) {
            schoolListDomain.add(new Domain(school));
        }
        return schoolListDomain;
    }
}
