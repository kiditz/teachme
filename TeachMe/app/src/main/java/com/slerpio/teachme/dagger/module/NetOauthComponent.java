package com.slerpio.teachme.dagger.module;

import com.slerpio.teachme.*;
import com.slerpio.teachme.fragment.ActivityFragment;
import com.slerpio.teachme.fragment.LearnFragment;
import com.slerpio.teachme.fragment.VideoFragment;
import dagger.Component;

import javax.inject.Singleton;

@Singleton
@Component(modules = {AppModule.class, NetOauthModule.class})
public interface NetOauthComponent {
    void inject(MainActivity activity);
    void inject(ProfileActivity activity);
    void inject(EditProfileActivity activity);
    void inject(AddSchoolActivity activity);
    void inject(SchoolActivity activity);
    void inject(SchoolDetailActivity activity);
    void inject(VideoFragment fragment);
    void inject(ActivityFragment fragment);
    void inject(LearnFragment fragment);
    void inject(MaterialWriteActivity activity);
    void inject(AddMaterialActivity activity);
}