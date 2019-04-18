package com.toufic.myshows;

import android.app.Application;
import android.support.annotation.VisibleForTesting;

import com.toufic.myshows.di.ApplicationComponent;
import com.toufic.myshows.di.ApplicationModule;
import com.toufic.myshows.di.DaggerApplicationComponent;
import com.toufic.myshows.di.NetworkModule;
import com.toufic.myshows.di.StorageModule;

public class TvShowsApplication extends Application {

    private ApplicationComponent mApplicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        mApplicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .networkModule(new NetworkModule("https://www.omdbapi.com/"))
                .storageModule(new StorageModule())
                .build();
    }

    public ApplicationComponent getApplicationComponent() {
        return mApplicationComponent;
    }

    /**
     * Visible only for testing purposes.
     */
    @VisibleForTesting
    public void setTestComponent(ApplicationComponent appComponent) {
        mApplicationComponent = appComponent;
    }
}
