package com.toufic.myshows.di;

import android.app.Application;
import android.arch.persistence.room.Room;
import android.support.annotation.VisibleForTesting;

import com.toufic.myshows.db.TvShowDatabase;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(includes = {ApplicationModule.class})
public class StorageModule {

    public StorageModule() {
    }

    @Provides
    @Singleton
    @VisibleForTesting
    public TvShowDatabase provideTvShowDb(Application application) {
        return Room.databaseBuilder(application.getApplicationContext(),
                TvShowDatabase.class, "tv_shows_db").build();
    }
}
