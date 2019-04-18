package com.toufic.myshows.net;

import com.toufic.myshows.db.Episode;
import com.toufic.myshows.db.model.Season;
import com.toufic.myshows.db.TvShow;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface TvShowsWebService {
    @Headers("Content-Type: application/json")
    @GET(".")
    Single<TvShow> getTvShow(
            @Query("t") String title
    );

    @Headers("Content-Type: application/json")
    @GET(".")
    Single<Season> getSeasonInfo(
            @Query("t") String title,
            @Query("Season") String season
    );

    @Headers("Content-Type: application/json")
    @GET(".")
    Single<Episode> getEpisodeInfo(
            @Query("i") String episodeId
    );
}
