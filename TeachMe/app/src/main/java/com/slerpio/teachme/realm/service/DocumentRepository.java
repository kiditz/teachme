package com.slerpio.teachme.realm.service;

import com.slerpio.teachme.model.Document;
import com.slerpio.lib.core.Domain;
import io.realm.Realm;

public class DocumentRepository {
    private final Realm realm;

    public DocumentRepository(Realm realm) {
        this.realm = realm;
    }

    public Document add(Domain payload){
        realm.beginTransaction();
        Document document = realm.createObjectFromJson(Document.class, payload.toString());
        realm.commitTransaction();
        return realm.copyFromRealm(document);
    }
}
