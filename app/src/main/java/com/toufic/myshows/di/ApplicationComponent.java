package com.toufic.myshows.di;

import com.toufic.myshows.TvShowsViewModel;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {ApplicationModule.class, StorageModule.class, NetworkModule.class})
public interface ApplicationComponent {
    void inject(TvShowsViewModel viewModel);
}
