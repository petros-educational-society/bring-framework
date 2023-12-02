package com.web.petros.service;

import com.google.gson.JsonObject;
import com.petros.bringframework.beans.factory.annotation.InjectPlease;
import com.petros.bringframework.context.annotation.Component;
import com.web.petros.data.PhotoInfo;
import com.web.petros.http.client.MarsApiClient;
import com.web.petros.http.client.NasaApiClient;
import lombok.extern.log4j.Log4j2;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.HttpException;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static java.util.Objects.nonNull;

/**
 * @author Viktor Basanets
 * @Project: bring-framework
 */
@Log4j2
@Component
public class RetrofitNasaApiService implements NasaApiService {

    private final NasaApiClient nasaApiClient;
    private final MarsApiClient marsApiClient;

    @InjectPlease
    public RetrofitNasaApiService(NasaApiClient nasaApiClient, MarsApiClient marsApiClient) {
        this.nasaApiClient = nasaApiClient;
        this.marsApiClient = marsApiClient;
    }

    @Override
    public ResponseBody getLargestPicture(int sol, String apiKey) {
        var body = retryOneTimeIfThrows(nasaApiClient.getRoot(sol, apiKey)).body();
        List<String> sources = new ArrayList<>();
        if (nonNull(body)) {
            for (var photo : body.getAsJsonArray("photos")) {
                if (photo instanceof JsonObject jsonObject) {
                    sources.add(jsonObject.get("img_src").getAsString());
                }
            }
        }

        var largestPhotoInfo = sources.parallelStream()
                .map(url -> getPhotoInfo(url, marsApiClient))
                .filter(Objects::nonNull)
                .reduce((info1, info2) -> info1.contentLength() > info2.contentLength() ? info1 : info2)
                .orElseThrow();

        var response = retryOneTimeIfThrows(marsApiClient.getRawPhoto(largestPhotoInfo.url()));
        return response.body();
    }

    private PhotoInfo getPhotoInfo(String url, MarsApiClient client) {
        var response = retryOneTimeIfThrows(client.getPhotoInfo(url));
        var contentLengthStr = response
                .headers()
                .get("content-length");

        if (contentLengthStr != null) {
            var info = new PhotoInfo(url, Long.parseLong(contentLengthStr));
            log.debug("Received {}", info);
            return info;
        }

        return null;
    }

    private <T> Response<T> retryOneTimeIfThrows(Call<T> call) {
        try {
            return call.execute();
        } catch (HttpException httpEx) {
            if (httpEx.code() == 503) {
                try {
                    TimeUnit.SECONDS.sleep(10);
                } catch (Throwable ignored) {}
                // secondary try ...
                try {
                    return call.execute();
                } catch (Throwable th) {
                    throw new RuntimeException(th);
                }
            }
            throw httpEx;
        } catch (Throwable th) {
            throw new RuntimeException(th);
        }
    }
}
