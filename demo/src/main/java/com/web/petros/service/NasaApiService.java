package com.web.petros.service;

import okhttp3.ResponseBody;

/**
 * @author Viktor Basanets
 * @Project: bring-framework
 */
public interface NasaApiService {
    ResponseBody getLargestPicture(int sol, String apiKey);
}
