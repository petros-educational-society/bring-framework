package com.web.petros.service;

import com.google.gson.JsonObject;
import com.petros.bringframework.context.annotation.Component;
import com.web.petros.data.PhotoInfo;
import retrofit2.Call;
import retrofit2.HttpException;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.web.petros.config.DefaultAppConfig.marsApiClient;
import static java.util.Objects.nonNull;

/**
 * @author Viktor Basanets
 * @Project: bring-framework
 */
@Component
public class RetrofitNasaApiService implements NasaApiService {
    @Override
    public Response<String> getLargestPicture(String apiUrl) {
        var body = retryOneTimeIfThrows(marsApiClient(apiUrl).getRoot(apiUrl)).body();
        List<String> sources = new ArrayList<>();
        if (nonNull(body)) {
            for (var photo : body.getAsJsonArray("photos")) {
                if (photo instanceof JsonObject jsonObject) {
                    sources.add(jsonObject.get("img_src").getAsString());
                }
            }
        }

        var client = marsApiClient(sources.get(0));
        var largestPhotoInfo = sources.parallelStream()
                .map(url -> getPhotoInfo(url, client))
                .filter(Objects::nonNull)
                .reduce((photoInfo1, photoInfo2) -> photoInfo1.contentLength() > photoInfo2.contentLength() ? photoInfo1 : photoInfo2)
                .orElseThrow();

        return retryOneTimeIfThrows(client.getRawPhoto(largestPhotoInfo.url()));
    }

    private PhotoInfo getPhotoInfo(String url, MarsApiClient client) {
        var contentLengthStr = retryOneTimeIfThrows(client.getPhotoInfo(url))
                .headers()
                .get("content-length");

        if (contentLengthStr != null) {
            var info = new PhotoInfo(url, Long.parseLong(contentLengthStr));
            System.out.println(info);
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
