package com.web.petros.service;

import com.google.gson.JsonObject;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * @author Viktor Basanets
 * @Project: bring-framework
 */
public interface NasaApiClient {
    @GET("/mars-photos/api/v1/rovers/curiosity/photos")
    Call<JsonObject> getRoot(@Query("sol") int sol, @Query("api_key") String apiKey);
}
