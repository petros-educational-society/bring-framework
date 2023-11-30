package com.web.petros.service;

import org.junit.jupiter.api.Test;
import retrofit2.Call;
import retrofit2.HttpException;
import retrofit2.Response;

import java.util.concurrent.TimeUnit;

import static com.web.petros.config.DefaultAppConfig.marsApiClient;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Viktor Basanets
 * @Project: bring-framework
 */
public class MarsApiClientTest {

    private static final String directUrl = "http://mars.jpl.nasa.gov/msl-raw-images/ods/surface/sol/00015/soas/rdr/ccam/CR0_398826604PRC_F0030004CCAM05015L1.PNG";
    private static final long contentLength = 621495;

    private static final String targetUrl = "https://mars.nasa.gov/msl-raw-images/ods/surface/sol/00015/soas/rdr/ccam/CR0_398826604PRC_F0030004CCAM05015L1.PNG";


    @Test
    public void test() {
        var client = marsApiClient(directUrl);
        var call = client.getRawPhoto(directUrl, 0);
        var response = retryOneTimeIfThrows(call);
        assertNotNull(response);
        assertTrue(Response.class.isAssignableFrom(response.getClass()));
        assertNotNull(response.body());
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