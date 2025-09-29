package com.flibs.di;

import android.content.Context;

import com.flibs.FlibsApplication;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by dbudyak on 07.04.16.
 */
@Module
public class AppModule {

    FlibsApplication mApplication;

    public AppModule(FlibsApplication application) {
        mApplication = application;
    }

    @Provides
    @Singleton
    FlibsApplication providesApplication() {
        return mApplication;
    }

    @Provides
    @Singleton
    Context providesContext(FlibsApplication application) {
        return application.getApplicationContext();
    }
}