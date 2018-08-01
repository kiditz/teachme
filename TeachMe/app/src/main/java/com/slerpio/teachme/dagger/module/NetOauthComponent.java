package com.slerpio.teachme.dagger.module;

import com.slerpio.teachme.*;
import com.slerpio.teachme.fragment.FragmentActivity;
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
    void inject(FragmentActivity fragment);
    void inject(LearnFragment fragment);
    void inject(MaterialWriteActivity activity);
    void inject(AddMaterialActivity activity);
    void inject(MaterialDetailActivity activity);
    void inject(MaterialPreviewActivity activity);
    void inject(CommentMaterialActivity activity);
    void inject(MySubmissionActivity activity);
}