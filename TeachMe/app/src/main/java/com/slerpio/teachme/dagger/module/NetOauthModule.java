package com.slerpio.teachme.dagger.module;

import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dagger.Module;
import dagger.Provides;
import okhttp3.*;
import okhttp3.logging.HttpLoggingInterceptor;
import okio.Buffer;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;

import javax.inject.Singleton;
import java.util.concurrent.TimeUnit;

@Module
public class NetOauthModule {
    String baseUrl;

    public NetOauthModule(String baseUrl) {
        this.baseUrl = baseUrl;
    }


    @Provides
    @Singleton
    Cache provideHttpCache(Application application) {
        int cacheSize = 10 * 1024 * 1024;
        return new Cache(application.getCacheDir(), cacheSize);
    }

    @Provides
    @Singleton
    Gson provideGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
        return gsonBuilder.create();
    }

    @Provides
    @Singleton
    ObjectMapper provideObjectMapper(){
        ObjectMapper jsonMapper = new ObjectMapper();
        jsonMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        jsonMapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
        jsonMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        jsonMapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false);
        jsonMapper.configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, false);
        return jsonMapper;
    }


    @Provides
    @Singleton
    OkHttpClient provideOkhttpClient(Cache cache, SharedPreferences preferences) {
        Interceptor interceptor = chain -> {
            Request request = chain.request();
            if(preferences.contains("token")){
                request = request.newBuilder().addHeader("Authorization", "Bearer " + preferences.getString("token", ""))
                        .method(request.method(), request.body()).build();
            }
            Log.w("Headers >>> ", request.headers().names().toString());
            Log.w("Method >>> ", request.method());
            Log.w("Url >>> ", request.url().toString());
            try {
                Buffer body = new Buffer();
                if(request.body() != null){
                    request.body().writeTo(body);
                }
                Log.w("Param >>> ", request.url().query() == null ? "" : request.url().query());
                Log.w("Request >>> ", body.readUtf8());
                Response response = chain.proceed(request);
                if (response.body() != null && response.body().byteStream() != null) {
                    Log.w("Response >>> ", body.readUtf8());
                }
                return response;
            } catch (Exception e) {
                Log.w("Request >>>", "Exception");
                e.printStackTrace();
                return chain.proceed(request);
            }

        };
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder client = new OkHttpClient.Builder();
        client.addInterceptor(logging);
        client.addInterceptor(interceptor);

        client.connectTimeout(10, TimeUnit.SECONDS).readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS).build();
        client.cache(cache);
        return client.build();
    }

    @Provides
    @Singleton
    Retrofit provideRetrofit(ObjectMapper objectMapper, OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(JacksonConverterFactory.create(objectMapper))
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .build();
    }

}