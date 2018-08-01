package com.slerpio.teachme.service;

import com.slerpio.lib.core.Domain;
import io.reactivex.Single;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.*;

public interface DocumentService {
    @Multipart
    @POST("teachme/add_document")
    Single<Domain> addDocument(@Part("directory") RequestBody directory, @Part MultipartBody.Part file);
    @Multipart
    @POST("teachme/add_document")
    Single<Domain> addDocument(@Part("directory") RequestBody directory, @Part MultipartBody.Part file, @Part("secure") RequestBody secure);

    @Headers({"Content-Type:application/json"})
    @DELETE("teachme/delete_document_by_id")
    Single<Domain> deleteDocument(@Query("id") Long id);

    @GET("teachme/get_document")
    @Streaming
    Single<ResponseBody> getDocument(@Query("id") Long id);
}
