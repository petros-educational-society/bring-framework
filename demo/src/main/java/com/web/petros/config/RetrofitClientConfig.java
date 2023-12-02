package com.web.petros.config;

import com.google.gson.GsonBuilder;
import com.petros.bringframework.context.annotation.Bean;
import com.petros.bringframework.context.annotation.Configuration;
import com.web.petros.http.client.MarsApiClient;
import com.web.petros.http.client.NasaApiClient;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import java.util.concurrent.TimeUnit;

import static com.web.petros.http.OkHttpConnectionPool.CONNECTION_POOL;

/**
 * @author Viktor Basanets
 * @Project: bring-framework
 */
@Configuration
public class RetrofitClientConfig {

    @Bean
    public NasaApiClient nasaApiClient() {
        return new Retrofit.Builder()
                .baseUrl("https://api.nasa.gov")
                .addConverterFactory(GsonConverterFactory.create())
                .client(createClient(120))
                .build()
                .create(NasaApiClient.class);
    }

    @Bean
    public MarsApiClient marsApiClient() {
        return new Retrofit.Builder()
                .baseUrl("http://mars.jpl.nasa.gov")
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().setLenient().create()))
                .client(createClient(240))
                .build()
                .create(MarsApiClient.class);
    }

    private static OkHttpClient createClient(int timeout) {
        return new OkHttpClient.Builder()
                .connectionPool(CONNECTION_POOL)
                .addInterceptor(chain -> chain.withConnectTimeout(timeout, TimeUnit.SECONDS)
                        .withWriteTimeout(timeout, TimeUnit.SECONDS)
                        .withReadTimeout(timeout, TimeUnit.SECONDS)
                        .proceed(chain.request()))
                .build();
    }
}
