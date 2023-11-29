package com.web.petros.service;

import retrofit2.Response;

/**
 * @author Viktor Basanets
 * @Project: bring-framework
 */
public interface NasaApiService {
    Response<String> getLargestPicture(String url);
}
