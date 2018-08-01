package com.slerpio.teachme.service;

import com.slerpio.lib.core.Domain;
import io.reactivex.Single;
import retrofit2.http.*;

public interface SchoolService {
    @Headers({"Content-Type:application/json"})
    @POST("teachme/add_school")
    Single<Domain> addSchool(@Body Domain input);

    @Headers({"Content-Type:application/json"})
    @GET("teachme/get_school_by_name")
    Single<Domain> getSchoolByName(@QueryMap Domain input);

    @Headers({"Content-Type:application/json"})
    @GET("teachme/find_school_by_id")
    Single<Domain> findSchoolById(@Query("id") Long schoolId);

    @Headers({"Content-Type:application/json"})
    @GET("teachme/get_school_level")
    Single<Domain> getSchoolLevel();

    @Headers({"Content-Type:application/json"})
    @GET("teachme/get_school_class_by_level_id")
    Single<Domain> getSchoolClassByLevelId(@Query("level_id") Long levelId);
}
