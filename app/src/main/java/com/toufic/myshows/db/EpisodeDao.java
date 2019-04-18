package com.toufic.myshows.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import io.reactivex.Maybe;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface EpisodeDao {
    @Insert(onConflict = REPLACE)
    void saveAll(List<Episode> episodes);

    @Query("SELECT * FROM episode WHERE seriesID = :id AND seasonNumber = :number")
    Maybe<List<Episode>> load(String id, String number);

    @Query("DELETE FROM episode")
    void deleteAll();
}
