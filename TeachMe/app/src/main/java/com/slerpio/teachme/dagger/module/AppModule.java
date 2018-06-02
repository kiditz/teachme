package com.slerpio.teachme.dagger.module;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.slerpio.teachme.helper.Translations;
import com.slerpio.teachme.realm.service.DocumentRepository;
import com.slerpio.teachme.realm.service.SchoolRepository;
import com.slerpio.teachme.realm.service.UserRepository;
import com.slerpio.teachme.service.ImageService;
import dagger.Module;
import dagger.Provides;
import io.realm.Realm;

import javax.inject.Singleton;

@Module
public class AppModule {
    Application application;

    public AppModule(Application application) {
        this.application = application;
    }

    @Provides
    @Singleton
    public Application provideApplication() {
        return application;
    }

    @Provides
    public Realm provideRealm(){
        return Realm.getDefaultInstance();
    }

    @Provides
    @Singleton
    public UserRepository provideUserRepository(Realm realm){
        return new UserRepository(realm);
    }
    @Provides
    @Singleton
    public SchoolRepository provideSchoolRepository(Realm realm){
        return new SchoolRepository(realm);
    }

    @Provides
    @Singleton
    public DocumentRepository provideDocumentRepository(Realm realm){
        return new DocumentRepository(realm);
    }
    @Provides
    @Singleton
    public Translations translations(){
        return new Translations(application.getBaseContext());
    }

    @Provides
    @Singleton
    public SharedPreferences provideSharedPreferences(){
        return PreferenceManager.getDefaultSharedPreferences(application.getBaseContext());
    }

    @Provides
    @Singleton
    public ImageService provideImageService(SharedPreferences preferences){
        return new ImageService(application.getBaseContext(), preferences);
    }


}
