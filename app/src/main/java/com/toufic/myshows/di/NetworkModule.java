package com.toufic.myshows.di;

import android.support.annotation.VisibleForTesting;

import com.toufic.myshows.TvShowsRepository;
import com.toufic.myshows.db.TvShowDatabase;
import com.toufic.myshows.net.AuthorizationInterceptor;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
public class NetworkModule {

    private final String mBaseUrl;

    public NetworkModule(String baseUrl) {
        mBaseUrl = baseUrl;
    }

    @Provides
    @Singleton
    @VisibleForTesting
    public TvShowsRepository provideTvShowsRepository(Retrofit retrofit, TvShowDatabase database) {
        return new TvShowsRepository(retrofit, database);
    }

    @Provides
    @Singleton
    HttpLoggingInterceptor provideLoggingInterceptor() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return interceptor;
    }

    @Provides
    @Singleton
    AuthorizationInterceptor provideAuthorizationInterceptor() {
        return new AuthorizationInterceptor();
    }

    @Provides
    @Singleton
    public OkHttpClient provideOkHttpClient(AuthorizationInterceptor authInterceptor, HttpLoggingInterceptor LogInterceptor) {
        return new OkHttpClient.Builder()
                .addInterceptor(authInterceptor)
                .addInterceptor(LogInterceptor)
                .build();
    }

    @Provides
    @Singleton
    GsonConverterFactory provideGson() {
        return GsonConverterFactory.create();
    }

    @Provides
    @Singleton
    public Retrofit provideRetrofit(GsonConverterFactory gsonConverterFactory, OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .baseUrl(mBaseUrl)
                .client(okHttpClient)
                .addConverterFactory(gsonConverterFactory)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }
}
