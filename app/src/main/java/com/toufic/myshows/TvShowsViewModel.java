package com.toufic.myshows;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.toufic.myshows.db.Episode;
import com.toufic.myshows.db.TvShow;
import com.toufic.myshows.net.ApiResponse;
import com.toufic.myshows.net.ApiStatus;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.MaybeObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class TvShowsViewModel extends AndroidViewModel {

    /**
     * Field Injected repository
     */
    @Inject
    TvShowsRepository mTvShowsRepository;

    /**
     * LiveData to notify UI asynchronously about new UI updates for Tv Shows.
     */
    private MutableLiveData<ApiResponse> mTvShows = new MutableLiveData<>();
    /**
     * LiveData to notify UI asynchronously about new UI updates for Episodes.
     */
    private final MutableLiveData<ApiResponse> mSeasonInfoLiveData = new MutableLiveData<>();

    /**
     * A composite disposable to hold all disposables from deferred calls to be cleaned up.
     */
    private final CompositeDisposable disposables = new CompositeDisposable();

    public TvShowsViewModel(@NonNull Application application) {
        super(application);
        ((TvShowsApplication) application).getApplicationComponent().inject(this);
    }

    /**
     * @return LiveData to post changes for Tv Shows updates.
     */
    public LiveData<ApiResponse> getShows() {
        return mTvShows;
    }

    /**
     * @return LiveData to post changes for season updates.
     */
    public LiveData<ApiResponse> getSeason() {
        return mSeasonInfoLiveData;
    }

    /**
     * Loads the TvShows. This will either loads from the local persistent storage cache or from the network resource.
     */
    public void loadTvShows() {
        mTvShowsRepository.loadTvShows()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        ApiResponse response = new ApiResponse();
                        response.status = ApiStatus.LOADING;
                        mTvShows.postValue(response);
                    }
                })
                .subscribe(new MaybeObserver<List<TvShow>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposables.add(d);
                    }

                    @Override
                    public void onSuccess(List<TvShow> tvShows) {
                        ApiResponse response = new ApiResponse();
                        response.status = ApiStatus.SUCCESS;
                        response.tvshows = tvShows;
                        mTvShows.postValue(response);
                    }

                    @Override
                    public void onError(Throwable e) {
                        ApiResponse response = new ApiResponse();
                        response.status = ApiStatus.ERROR;
                        mTvShows.postValue(response);
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    /**
     * Loads the Episodes of a Tv Show Season. This will either loads from the local persistent storage cache or from the network resource.
     *
     * @param seriesId     Series ID
     * @param seasonNumber Season Number
     */
    public void loadSeasonInfo(String seriesId, String seasonNumber) {
        mTvShowsRepository.loadEpisodes(seriesId, seasonNumber)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        ApiResponse response = new ApiResponse();
                        response.status = ApiStatus.LOADING;
                        mSeasonInfoLiveData.setValue(response);
                    }
                }).subscribe(new MaybeObserver<List<Episode>>() {
            @Override
            public void onSubscribe(Disposable d) {
                disposables.add(d);
            }

            @Override
            public void onSuccess(List<Episode> episodes) {
                ApiResponse response = new ApiResponse();
                response.status = ApiStatus.SUCCESS;
                response.episodes = episodes;
                mSeasonInfoLiveData.setValue(response);
            }

            @Override
            public void onError(Throwable e) {
                ApiResponse response = new ApiResponse();
                response.status = ApiStatus.ERROR;
                mSeasonInfoLiveData.setValue(response);
            }

            @Override
            public void onComplete() {
            }
        });
    }

    /**
     * Clean up subscriptions when being destroyed.
     */
    @Override
    protected void onCleared() {
        super.onCleared();
        if (!disposables.isDisposed()) {
            disposables.clear();
        }
    }
}
