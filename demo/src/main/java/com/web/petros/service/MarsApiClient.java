package com.web.petros.service;

import com.google.gson.JsonObject;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.HEAD;
import retrofit2.http.Url;

/**
 * @author Viktor Basanets
 * @Project: bring-framework
 */
public interface MarsApiClient {
    @GET
    Call<JsonObject> getRoot(@Url String url);

    @HEAD
    Call<Void> getPhotoInfo(@Url String url);

    @GET
    Call<String> getRawPhoto(@Url String url);
}
