package com.toufic.myshows.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import io.reactivex.Maybe;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface TvShowDao {
    @Insert(onConflict = REPLACE)
    void saveAll(List<TvShow> shows);

    @Query("SELECT * FROM tvshow")
    Maybe<List<TvShow>> loadAll();

    @Query("DELETE FROM tvshow")
    void deleteAll();
}