package com.thetvdb.di;

import android.content.Context;

import com.thetvdb.TheTVApplication;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by dbudyak on 07.04.16.
 */
@Module
public class AppModule {

    TheTVApplication mApplication;

    public AppModule(TheTVApplication application) {
        mApplication = application;
    }

    @Provides
    @Singleton
    TheTVApplication providesApplication() {
        return mApplication;
    }

    @Provides
    @Singleton
    Context providesContext(TheTVApplication application) {
        return application.getApplicationContext();
    }
}