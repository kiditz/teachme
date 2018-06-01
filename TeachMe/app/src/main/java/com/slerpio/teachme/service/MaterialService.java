package com.slerpio.teachme.service;

import com.slerpio.teachme.model.Domain;
import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface MaterialService {
    @GET("teachme/get_material_topic")
    Single<Domain> getMaterialTopic(@QueryMap  Domain input);

    @GET("teachme/get_material_topic_by_level_id")
    Single<Domain> getMaterialTopicByLevel(@QueryMap  Domain input);

    @GET("teachme/get_material_by_topic_id")
    Single<Domain> getMaterialByTopicId(@QueryMap  Domain input);
}
