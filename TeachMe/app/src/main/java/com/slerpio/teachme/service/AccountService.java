package com.slerpio.teachme.service;

import com.slerpio.lib.core.Domain;
import io.reactivex.Single;
import retrofit2.http.*;

public interface AccountService {
    @POST("/uua/oauth/token")
    Single<Domain> login(@Header("Authorization") String authentication, @QueryMap Domain input);
    @Headers({"Content-Type:application/json"})
    @POST("teachme/register_teacher")
    Single<Domain> registerTeacher(@Body Domain input);
    @Headers({"Content-Type:application/json"})
    @GET("teachme/find_user_principal_by_username")
    Single<Domain> findUser(@QueryMap Domain input);

    @Headers({"Content-Type:application/json"})
    @PUT("teachme/edit_user_principal_by_username")
    Single<Domain> editUser(@Body Domain input);
}
