package com.thetvdb.di;

import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.gson.Gson;
import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;
import com.squareup.picasso.Picasso;
import com.thetvdb.TheTVApplication;
import com.thetvdb.service.TvDbRestApi;
import com.thetvdb.util.AuthInterceptor;
import com.thetvdb.util.Config;
import com.thetvdb.util.DefaultPreferences;
import com.thetvdb.util.FavoritesManager;

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
    DefaultPreferences providesSharedPrefManager(TheTVApplication application) {
        return new DefaultPreferences(PreferenceManager.getDefaultSharedPreferences(application));
    }

    @Provides
    @Singleton
    Config providesConfig(TheTVApplication application) {
        return new Config(application);
    }

    @Provides
    @Singleton
    GoogleApiClient providesGoogleApi(TheTVApplication context) {
        return new GoogleApiClient.Builder(context)
                .addScope(new Scope(Scopes.PROFILE))
                .addScope(new Scope(Scopes.EMAIL))
                .addApi(Auth.GOOGLE_SIGN_IN_API,
                        new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                .requestId()
                                .requestProfile()
                                .requestEmail()
                                .build()
                )
                .build();
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
                .addNetworkInterceptor(new AuthInterceptor(prefs.getTokenHeader()))
                .build();
    }

    @Provides
    TvDbRestApi providesApiService(OkHttpClient client) {
        return new Retrofit.Builder()
                .baseUrl(TvDbRestApi.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
                .create(TvDbRestApi.class);
    }

    @Provides
    Picasso providesImageLoader(TheTVApplication context, OkHttpClient client) {
        return new Picasso.Builder(context)
                .listener(new Picasso.Listener() {
                    @Override
                    public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                        Log.e("DEBUG", "url=" + uri + ";" + exception.getMessage());
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
    FavoritesManager providesPreferenceWrapper(DefaultPreferences prefs, Gson gson) {
        return new FavoritesManager(prefs, gson);
    }

}
