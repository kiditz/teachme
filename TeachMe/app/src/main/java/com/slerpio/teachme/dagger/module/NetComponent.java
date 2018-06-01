package com.slerpio.teachme.dagger.module;

import com.slerpio.teachme.LoginActivity;
import com.slerpio.teachme.RegisterActivity;
import dagger.Component;

import javax.inject.Singleton;

@Singleton
@Component(modules = {AppModule.class, NetModule.class})
public interface NetComponent {
    void inject(RegisterActivity activity);
    void inject(LoginActivity activity);
}