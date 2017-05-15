package com.flibs.di;

import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

import com.flibs.FlibsApplication;
import com.flibs.service.CacheManager;
import com.flibs.service.FlibsApi;
import com.flibs.util.Config;
import com.flibs.util.DefaultPreferences;
import com.google.gson.Gson;
import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;
import com.squareup.picasso.Picasso;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by dbudyak on 07.04.16.
 */
@Module(includes = AppModule.class)
public class MainModule {

    @Provides
    @Singleton
    DefaultPreferences providesSharedPrefManager(FlibsApplication application) {
        return new DefaultPreferences(PreferenceManager.getDefaultSharedPreferences(application));
    }

    @Provides
    @Singleton
    Config providesConfig(FlibsApplication application) {
        return new Config(application);
    }


    @Provides
    @Singleton
    Bus providesBus() {
        return new Bus(ThreadEnforcer.ANY);
    }

    @Provides
    OkHttpClient providesClient(final DefaultPreferences prefs) {
        return new OkHttpClient()
                .newBuilder()
                .build();
    }

    @Provides
    FlibsApi providesApiService(OkHttpClient client) {
        return new Retrofit.Builder()
                .baseUrl(FlibsApi.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
                .create(FlibsApi.class);
    }

    @Provides
    Picasso providesImageLoader(FlibsApplication context, OkHttpClient client) {
        return new Picasso.Builder(context)
                .listener(new Picasso.Listener() {
                    @Override
                    public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                        Log.e("DEBUG", "url=" + uri+";"+exception.getMessage());
                    }
                })
                .downloader(new OkHttp3Downloader(client))
                .build();
    }

    @Provides
    @Singleton
    Gson providesGson() {
        return new Gson();
    }

    @Provides
    @Singleton
    CacheManager providesCacheManager(DefaultPreferences prefs, Gson gson) {
        return new CacheManager(prefs, gson);
    }

}
