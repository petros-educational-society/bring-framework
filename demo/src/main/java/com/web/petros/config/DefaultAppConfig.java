package com.web.petros.config;

import com.google.gson.GsonBuilder;
import com.petros.bringframework.context.annotation.ComponentScan;
import com.petros.bringframework.context.annotation.Configuration;
import com.web.petros.service.MarsApiClient;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import java.util.concurrent.TimeUnit;

import static com.web.petros.http.OkHttpConnectionPool.CONNECTION_POOL;
import static java.util.Objects.isNull;

/**
 * @author "Maksym Oliinyk"
 * @author "Viktor Basanets"
 */
@ComponentScan(basePackages = {"com.web.petros", "com.petros"})
@Configuration
public class DefaultAppConfig {

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
                                    .addHeader("Content-Type", "application/json; charset=utf-8")
                                    .build());
                        })
                        .build())
                .build()
                .create(MarsApiClient.class);
    }
}
