package com.slerpio.teachme.service;

import com.slerpio.teachme.model.Domain;
import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface TeacherService {
    @GET("teachme/find_teacher_by_username")
    Single<Domain> findTeacherByUsername(@Query("username") String username);
}
