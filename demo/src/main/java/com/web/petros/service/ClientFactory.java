package com.web.petros.service;

import com.google.gson.GsonBuilder;
import com.web.petros.http.client.MarsApiClient;
import com.web.petros.http.client.NasaApiClient;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import java.util.concurrent.TimeUnit;

import static com.web.petros.http.OkHttpConnectionPool.CONNECTION_POOL;
import static java.util.Objects.isNull;

/**
 * @author Viktor Basanets
 * @Project: bring-framework
 */
public class ClientFactory {

    //    @Bean
    public static NasaApiClient nasaApiClient() {
        return new Retrofit.Builder()
                .baseUrl("https://api.nasa.gov")
                .addConverterFactory(GsonConverterFactory.create())
                .client(new OkHttpClient.Builder()
                        .connectionPool(CONNECTION_POOL)
                        .addInterceptor(chain -> chain
                                .withConnectTimeout(120, TimeUnit.SECONDS)
                                .withWriteTimeout(120, TimeUnit.SECONDS)
                                .withReadTimeout(120, TimeUnit.SECONDS)
                                .proceed(chain.request()
                                        .newBuilder()
                                        .addHeader("Content-Type", "application/json; charset=utf-8")
                                        .build()))
                        .build())
                .build()
                .create(NasaApiClient.class);
    }

    //    @Bean
    public static MarsApiClient marsApiClient(String url) {
        return new Retrofit.Builder()
                .baseUrl("https://*")
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().setLenient().create()))
                .client(new OkHttpClient.Builder()
                        .connectionPool(CONNECTION_POOL)
                        .addInterceptor(chain -> {
                            chain.withConnectTimeout(240, TimeUnit.SECONDS)
                                    .withWriteTimeout(240, TimeUnit.SECONDS)
                                    .withReadTimeout(240, TimeUnit.SECONDS);
                            var baseUrl = HttpUrl.parse(url);
                            if (isNull(baseUrl)) {
                                throw new RuntimeException("Invalid Nasa API URL");
                            }
                            return chain.proceed(chain.request()
                                    .newBuilder()
                                    .url(chain.request()
                                            .url()
                                            .newBuilder()
                                            .scheme(baseUrl.scheme())
                                            .host(baseUrl.host())
                                            .build())
                                    .build());
                        })
                        .build())
                .build()
                .create(MarsApiClient.class);
    }
}
