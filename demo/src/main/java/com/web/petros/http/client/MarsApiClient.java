package com.web.petros.http.client;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.HEAD;
import retrofit2.http.Url;

/**
 * @author Viktor Basanets
 * @Project: bring-framework
 */
public interface MarsApiClient {
    @HEAD
    Call<Void> getPhotoInfo(@Url String url);

//    @Headers("Content-Type: application/json; charset=utf-8")
    @GET
    Call<ResponseBody> getRawPhoto(@Url String url/*, @Header("Content-Length") long contentLength*/);
}
