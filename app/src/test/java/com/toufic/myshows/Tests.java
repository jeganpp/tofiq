package com.toufic.myshows;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.support.annotation.NonNull;

import com.toufic.myshows.db.Episode;
import com.toufic.myshows.db.TvShow;
import com.toufic.myshows.db.TvShowDatabase;
import com.toufic.myshows.di.ApplicationComponent;
import com.toufic.myshows.di.ApplicationModule;
import com.toufic.myshows.di.DaggerApplicationComponent;
import com.toufic.myshows.di.NetworkModule;
import com.toufic.myshows.di.StorageModule;
import com.toufic.myshows.net.ApiResponse;
import com.toufic.myshows.net.ApiStatus;
import com.toufic.myshows.net.AuthorizationInterceptor;
import com.toufic.myshows.utils.Utils;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import io.reactivex.Scheduler;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.internal.schedulers.ExecutorScheduler;
import io.reactivex.plugins.RxJavaPlugins;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Retrofit.class)
@PowerMockIgnore("javax.net.ssl.*")
public class Tests {
    @Mock
    private
    TvShowsApplication mMockApplication;
    @Mock
    private
    TvShowDatabase mMockDatabase;
    @Mock
    private
    Retrofit mMockRetrofit;
    @Mock
    private
    OkHttpClient mMockOkHttpClient;

    private MockRepository mMockRepository;

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Before
    public void setup() {

        final Scheduler immediate = new Scheduler() {
            @Override
            public Disposable scheduleDirect(@NonNull Runnable run, long delay, @NonNull TimeUnit unit) {
                // this prevents StackOverflowErrors when scheduling with a delay
                return super.scheduleDirect(run, 0, unit);
            }

            @Override
            public Worker createWorker() {
                return new ExecutorScheduler.ExecutorWorker(new Executor() {
                    @Override
                    public void execute(@NonNull Runnable command) {
                        command.run();
                    }
                }, false);
            }
        };

        RxJavaPlugins.setInitIoSchedulerHandler(new Function<Callable<Scheduler>, Scheduler>() {
            @Override
            public Scheduler apply(Callable<Scheduler> schedulerCallable) throws Exception {
                return immediate;
            }
        });

        RxAndroidPlugins.setInitMainThreadSchedulerHandler(new Function<Callable<Scheduler>, Scheduler>() {
            @Override
            public Scheduler apply(Callable<Scheduler> schedulerCallable) throws Exception {
                return immediate;
            }
        });

        init();
    }

    @Test
    public void test_change_http_to_https_url() {
        String httpUrl = "http://testabc.png";
        assertEquals("https://testabc.png", Utils.changeHttpToHttps(httpUrl));
    }

    @Test
    public void test_https_stays_https() {
        String httpUrl = "https://testabc.png";
        assertEquals("https://testabc.png", Utils.changeHttpToHttps(httpUrl));
    }

    @Test
    public void test_null_url_returns_empty_string() {
        String httpUrl = null;
        assertEquals("", Utils.changeHttpToHttps(httpUrl));
    }

    @Test
    public void test_viewModel_receives_shows_from_repository() {
        List<TvShow> shows = getTvShows();
//        ApiResponse response = new ApiResponse();
//        response.tvshows = shows;
//        response.status = ApiStatus.SUCCESS;
        mMockRepository.setShows(shows);
        TvShowsViewModel viewModel = Mockito.spy(new TvShowsViewModel(mMockApplication));
        viewModel.loadTvShows();
        assertEquals(shows.size(), viewModel.getShows().getValue().tvshows.size());
    }

    @Test
    public void test_viewModel_receives_empty_shows_from_repository1() {
        mMockRepository.setShows(Collections.EMPTY_LIST);
        TvShowsViewModel viewModel = Mockito.spy(new TvShowsViewModel(mMockApplication));
        viewModel.loadTvShows();
        assertEquals(Collections.EMPTY_LIST.size(), viewModel.getShows().getValue().tvshows.size());
    }


    @Test
    public void test_viewModel_receives_episodes_from_repository() {
        mMockRepository.setEpisodes(Collections.EMPTY_LIST);
        TvShowsViewModel viewModel = Mockito.spy(new TvShowsViewModel(mMockApplication));
        viewModel.loadSeasonInfo(anyString(), anyString());
        assertEquals(Collections.EMPTY_LIST.size(), viewModel.getSeason().getValue().episodes.size());
    }

    @Test
    public void test_viewModel_receives_empty_episodes_from_repository() {
        List<Episode> episodes = getEpisodes();
        mMockRepository.setEpisodes(episodes);
        TvShowsViewModel viewModel = Mockito.spy(new TvShowsViewModel(mMockApplication));
        viewModel.loadSeasonInfo(anyString(), anyString());
        assertEquals(episodes.size(), viewModel.getSeason().getValue().episodes.size());
    }

    private void init() {
        mMockRepository = new MockRepository(mMockRetrofit, mMockDatabase);
        NetworkModule networkModule = Mockito.spy(new NetworkModule("https://www.omdbapi.com/"));
        Mockito.doReturn(mMockRepository)
                .when(networkModule).provideTvShowsRepository((Retrofit) any(), (TvShowDatabase) any());

        Mockito.doReturn(mMockOkHttpClient)
                .when(networkModule).provideOkHttpClient(new AuthorizationInterceptor(), new HttpLoggingInterceptor());

        Mockito.doReturn(mMockRetrofit)
                .when(networkModule).provideRetrofit(GsonConverterFactory.create(), mMockOkHttpClient);

        ApplicationModule applicationModule = Mockito.spy(new ApplicationModule(mMockApplication));
        StorageModule storageModule = Mockito.spy(new StorageModule());

        Mockito.doReturn(mMockDatabase).when(storageModule).provideTvShowDb(mMockApplication);

        ApplicationComponent component = DaggerApplicationComponent.builder()
                .networkModule(networkModule)
                .applicationModule(applicationModule)
                .storageModule(storageModule)
                .build();

        mMockApplication.setTestComponent(component);

        Mockito.doReturn(component).when(mMockApplication).getApplicationComponent();
    }

    private List<TvShow> getTvShows() {
        List<TvShow> shows = new ArrayList<>();
        TvShow tvShow1 = new TvShow();
        tvShow1.setSeriesId("1");
        tvShow1.setCacheTimeStamp(1);
        TvShow tvShow2 = new TvShow();
        tvShow2.setSeriesId("2");
        tvShow2.setCacheTimeStamp(2);
        shows.add(tvShow1);
        shows.add(tvShow2);
        return shows;
    }

    private List<Episode> getEpisodes() {
        List<Episode> episodes = new ArrayList<>();
        Episode episode = new Episode();
        episode.setTitle("t1");
        episode.setCacheTimeStamp(1);
        Episode episode1 = new Episode();
        episode1.setTitle("t2");
        episode1.setCacheTimeStamp(1);
        episodes.add(episode);
        episodes.add(episode1);
        return episodes;
    }
}