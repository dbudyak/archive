package com.testask.letsfly;

import android.app.Application;

import com.testask.letsfly.di.MainModule;
import com.testask.letsfly.ui.FlightActivity;
import com.testask.letsfly.ui.StartActivity;

import javax.inject.Singleton;
import dagger.Component;

/**
 * Created by dbudyak on 24.03.17.
 */
public class FlightApplication extends Application {

    @Singleton
    @Component(modules = {MainModule.class})
    public interface AppComponent {
        void inject(StartActivity activity);
        void inject(FlightActivity activity);
    }

    private AppComponent component;

    @Override
    public void onCreate() {
        super.onCreate();
        component = DaggerFlightApplication_AppComponent.builder().mainModule(new MainModule()).build();
    }

    public AppComponent getComponent() {
        return component;
    }
}
