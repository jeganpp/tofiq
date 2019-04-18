package com.toufic.myshows;

import com.toufic.myshows.db.Episode;
import com.toufic.myshows.db.TvShow;
import com.toufic.myshows.db.TvShowDatabase;
import com.toufic.myshows.db.model.Season;
import com.toufic.myshows.db.model.SeasonInfo;
import com.toufic.myshows.net.TvShowsWebService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.Single;
import io.reactivex.functions.Function;
import io.reactivex.functions.Function3;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class TvShowsRepository {

    /**
     * Retrofit webservice APIS for communication with REST API.
     */
    private final TvShowsWebService mWebService;
    /**
     * Application database used as to hold a persistent cache.
     */
    private TvShowDatabase mDatabase;
    //FIXME:: Change value
    /**
     * Time after which the cache is considered stale.
     * Currently it is set to 1 minute just for development
     */
    private final long staleCache = (60 * 1 * 1 * 1000);

    /**
     * Constructor that allows dependency injection within the repository.
     *
     * @param retrofit       Initialized retrofit instance
     * @param tvShowDatabase Initialized database instance
     */
    public TvShowsRepository(Retrofit retrofit, TvShowDatabase tvShowDatabase) {
        mDatabase = tvShowDatabase;
        mWebService = retrofit.create(TvShowsWebService.class);
    }

    /**
     * Loads Tv Shows by first checking if database copy is there and not stale, otherwise
     * it fetched the data from the network.
     *
     * @return A Maybe deferred computation.
     */
    Maybe<List<TvShow>> loadTvShows() {
        return Maybe.concat(loadTvShowsFromCache(), loadTvShowsFromNetwork().toMaybe())
                .filter(new Predicate<List<TvShow>>() {
                    @Override
                    public boolean test(List<TvShow> tvShows) {
                        return tvShows.size() > 0
                                && System.currentTimeMillis() - staleCache <= tvShows.get(0).getCacheTimeStamp();
                    }
                })
                .firstElement();
    }

    /**
     * Loads TvShows from database.
     *
     * @return A deferred computation that loads Tv Shows from database.
     */
    private Maybe<List<TvShow>> loadTvShowsFromCache() {
        return mDatabase.tvShowDao().loadAll()
                .subscribeOn(Schedulers.io())
                .filter(new Predicate<List<TvShow>>() {
                    @Override
                    public boolean test(List<TvShow> tvShows) {
                        return tvShows.size() > 0;
                    }
                });
    }

    //TODO :: Input sanitizing and handle individual errors

    /**
     * Loads Tv Shows from network, nukes the db table and saves the new results.
     *
     * @return A deferred computation that fetches TvShows from the network resource.
     */
    private Single<List<TvShow>> loadTvShowsFromNetwork() {
        Single<TvShow> obs1 = mWebService.getTvShow("The+Office");
        Single<TvShow> obs2 = mWebService.getTvShow("Parks+and+Recreation");
        Single<TvShow> obs3 = mWebService.getTvShow("The+Good+Place");

        return Single.zip(
                obs1.subscribeOn(Schedulers.io()),
                obs2.subscribeOn(Schedulers.io()),
                obs3.subscribeOn(Schedulers.io()), new Function3<TvShow, TvShow, TvShow, List<TvShow>>() {
                    @Override
                    public List<TvShow> apply(TvShow tvShow, TvShow tvShow2, TvShow tvShow3) {
                        List<TvShow> shows = new ArrayList<>(Arrays.asList(tvShow, tvShow2, tvShow3));
                        mDatabase.tvShowDao().deleteAll();
                        mDatabase.tvShowDao().saveAll(shows);
                        return shows;
                    }
                }
        );
    }

    /**
     * Loads the episodes for a particular series' season from either db if present and not stale
     * otherwise, from netwokr resource.
     *
     * @param seriesID     Series ID
     * @param seasonNumber Season Number
     * @return A Maybe deferred computation.
     */
    Maybe<List<Episode>> loadEpisodes(String seriesID, String seasonNumber) {
        return Maybe.concat(loadEpisodesFromCache(seriesID, seasonNumber),
                loadSeasonInfoFromNetwork(seriesID, seasonNumber).toMaybe())
                .filter(new Predicate<List<Episode>>() {
                    @Override
                    public boolean test(List<Episode> episodes) {
                        return episodes.size() > 0
                                && System.currentTimeMillis() - staleCache <= episodes.get(0).getCacheTimeStamp();
                    }
                })
                .firstElement();
    }

    /**
     * A Helper method to create network resources queries for an episode ID.
     *
     * @param episodeId Episode ID
     * @return A Single deferred computation that uses communicates with the network.
     */
    private Single<Episode> createEpisodeSingle(String episodeId) {
        return mWebService.getEpisodeInfo(episodeId)
                .subscribeOn(Schedulers.io());
    }

    /**
     * Loads episodes of a particular series' season from the database.
     *
     * @param seriesID     Series ID
     * @param seasonNumber Season Number
     * @return A deferred computation that queries db for a list of episodes
     */
    private Maybe<List<Episode>> loadEpisodesFromCache(String seriesID, String seasonNumber) {
        return mDatabase.episodeDao().load(seriesID, seasonNumber)
                .subscribeOn(Schedulers.io())
                .filter(new Predicate<List<Episode>>() {
                    @Override
                    public boolean test(List<Episode> episodes) {
                        return episodes.size() > 0;
                    }
                });
    }

    /**
     * Fetches a series' season episodes from the network, nukes the db table, and saves new fresh entries.
     *
     * @param seriesTitle  Series Title
     * @param seasonNumber Season Number
     * @return A deferred Single computation that queries the network.
     */
    private Single<List<Episode>> loadSeasonInfoFromNetwork(String seriesTitle, String seasonNumber) {
        return mWebService.getSeasonInfo(seriesTitle, seasonNumber)
                .subscribeOn(Schedulers.io())
                .map(new Function<Season, List<Single<Episode>>>() {
                    @Override
                    public List<Single<Episode>> apply(Season season) {
                        List<Single<Episode>> episodeSources = new ArrayList<>();
                        if (season == null || season.getEpisodesId() == null) {
                            return episodeSources;
                        }
                        for (SeasonInfo si : season.getEpisodesId()) {
                            episodeSources.add(createEpisodeSingle(si.getEpisodeId()));
                        }
                        return episodeSources;
                    }
                }).flatMap(new Function<List<Single<Episode>>, Single<List<Episode>>>() {
                    @Override
                    public Single<List<Episode>> apply(List<Single<Episode>> singles) {
                        return Single.zip(singles, new Function<Object[], List<Episode>>() {
                            @Override
                            public List<Episode> apply(Object[] objects) {
                                List<Episode> episodesList = new ArrayList<>();
                                for (Object obj : objects) {
                                    Episode e = (Episode) obj;
                                    episodesList.add(e);
                                }
                                mDatabase.episodeDao().deleteAll();
                                mDatabase.episodeDao().saveAll(episodesList);
                                return episodesList;
                            }
                        });
                    }
                });
    }
}
