package com.slerpio.teachme;

import android.app.Application;
import com.slerpio.teachme.dagger.module.*;
import io.realm.Realm;
import io.realm.RealmConfiguration;

public class App extends Application{
    private NetComponent netComponent;
    private NetOauthComponent netOauthComponent;
    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);

        this.netComponent = DaggerNetComponent.builder().appModule(new AppModule(this)).netModule(new NetModule(getString(R.string.teach_me_url))).build();
        this.netOauthComponent = DaggerNetOauthComponent.builder().appModule(new AppModule(this)).netOauthModule(new NetOauthModule(getString(R.string.teach_me_url))).build();
    }

    public NetComponent getNetComponent() {
        return netComponent;
    }

    public NetOauthComponent getNetOauthComponent() {
        return netOauthComponent;
    }
}
