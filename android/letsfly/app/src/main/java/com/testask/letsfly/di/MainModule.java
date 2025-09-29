package com.testask.letsfly.di;

import android.content.Context;
import android.view.LayoutInflater;

import com.google.gson.Gson;
import com.testask.letsfly.api.Api;

import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by dbudyak on 07.04.16.
 */
@Module
public class MainModule {

    @Provides
    @Singleton
    OkHttpClient providesClient() {
        return new OkHttpClient()
                .newBuilder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS)
                .build();
    }

    @Provides
    @Singleton
    Api providesApi(OkHttpClient client) {
        return new Retrofit.Builder()
                .baseUrl(Api.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
                .create(Api.class);
    }

    @Provides
    @Singleton
    Gson providesGson() {
        return new Gson();
    }


}
