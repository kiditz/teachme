package com.slerpio.teachme.service;

import com.slerpio.teachme.model.Domain;
import io.reactivex.Single;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.*;

public interface DocumentService {
    @Multipart
    @POST("teachme/add_document")
    Single<Domain> addDocument(@Part("directory") RequestBody directory, @Part MultipartBody.Part file);
}
