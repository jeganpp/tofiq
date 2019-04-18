package com.toufic.myshows.net;

import com.toufic.myshows.BuildConfig;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * An interceptor that will intercept every retrofit call and rebuild the url
 * by adding the api key as a query and return the new url for retrofit to
 * continue its request.
 */

public class AuthorizationInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();
        HttpUrl originalUrl = original.url();

        HttpUrl newUrl = originalUrl.newBuilder()
                .addQueryParameter("apikey", BuildConfig.OMDB_API_KEY)
                .build();
        Request.Builder reqBuilder = original.newBuilder()
                .url(newUrl);
        Request newRequest = reqBuilder
                .build();
        return chain.proceed(newRequest);
    }
}
