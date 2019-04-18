package com.toufic.myshows;

import com.toufic.myshows.db.Episode;
import com.toufic.myshows.db.TvShow;
import com.toufic.myshows.db.TvShowDatabase;

import java.util.List;

import io.reactivex.Maybe;
import retrofit2.Retrofit;

public class MockRepository extends TvShowsRepository {
    private List<TvShow> mShows;
    private List<Episode> mEpisodes;

    /**
     * Constructor that allows dependency injection within the repository.
     *
     * @param retrofit       Initialized retrofit instance
     * @param tvShowDatabase Initialized database instance
     */
    public MockRepository(Retrofit retrofit, TvShowDatabase tvShowDatabase) {
        super(retrofit, tvShowDatabase);
    }


    public void setShows(List<TvShow> shows) {
        mShows = shows;
    }

    public void setEpisodes(List<Episode> episodes) {
        mEpisodes = episodes;
    }

    @Override
    Maybe<List<TvShow>> loadTvShows() {
        return Maybe.just(mShows);
    }

    @Override
    Maybe<List<Episode>> loadEpisodes(String seriesID, String seasonNumber) {
        return Maybe.just(mEpisodes);
    }
}
