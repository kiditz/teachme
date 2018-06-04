package com.slerpio.teachme.service;

import com.slerpio.teachme.model.Domain;
import io.reactivex.Single;
import retrofit2.http.*;

public interface MaterialService {
    @GET("teachme/get_material_topic_by_name")
    Single<Domain> getMaterialTopicByName(@QueryMap  Domain input);

    @GET("teachme/get_material_topic_by_level_id")
    Single<Domain> getMaterialTopicByLevel(@QueryMap  Domain input);

    @GET("teachme/get_material_by_topic_id")
    Single<Domain> getMaterialByTopicId(@QueryMap  Domain input);
    @Headers({"Content-Type:application/json"})
    @POST("teachme/add_material")
    Single<Domain> addMaterial(@Body  Domain input);
}
