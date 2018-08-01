package com.slerpio.teachme.service;

import com.slerpio.lib.core.Domain;
import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

public interface ActivityService {
    @GET("teachme/get_activity_by_user_id")
    Single<Domain> getActivityByUserId(@QueryMap Domain input);

    @GET("teachme/get_activity")
    Single<Domain> getActivity(@QueryMap Domain input);
}
