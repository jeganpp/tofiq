package com.toufic.myshows.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

@Database(entities = {TvShow.class, Episode.class}, version = 1)
public abstract class TvShowDatabase extends RoomDatabase {
    public abstract TvShowDao tvShowDao();

    public abstract EpisodeDao episodeDao();
}