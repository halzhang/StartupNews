package com.halzhang.android.startupnews.data.utils;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.webkit.WebSettings;

import com.squareup.okhttp.Cache;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.File;
import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * okhttp
 * Created by Hal on 2015/11/12.
 */
@Singleton
public class OkHttpClientHelper {

    public interface CookieFactory {
        public String getCookie();
    }

    /**
     * USER-AGENT
     */
    public static final String USER_AGENT = "Mozilla/5.0 (Linux; Android " + Build.VERSION.RELEASE
            + "; " + Build.MODEL + " Build/" + Build.ID + ")";

    private static final long SIZE_OF_CACHE = 10 * 1024 * 1024; // 10 MB


    private OkHttpClient mOkHttpClient;

    private CookieFactory mCookieFactory;

    @NonNull
    private Context mContext;

    @Inject
    public OkHttpClientHelper(CookieFactory cookieFactory, @NonNull Context context) {
        mCookieFactory = cookieFactory;
        mContext = context;
        init(mContext, mCookieFactory);
    }

    private void init(final Context context, CookieFactory factory) {
        mCookieFactory = factory;
        if (mOkHttpClient == null) {
            Interceptor mCacheControlInterceptor = new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request request = chain.request();
                    Request.Builder builder = request.newBuilder();

                    builder.addHeader("Accept-Language", "zh-cn");
                    builder.addHeader("Accept", "*/*");
                    if (mCookieFactory != null) {
                        builder.addHeader("Cookie", mCookieFactory.getCookie());
                    }
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
                        builder.header("User-Agent", WebSettings.getDefaultUserAgent(context));
                    } else {
                        builder.header("User-Agent", USER_AGENT);
                    }

                    // Add Cache Control only for GET methods
                    if (request.method().equals("GET")) {
                        if (NetworkUtils.isNetworkAvailable(context)) {
                            // 1 day
                            request.newBuilder().header("Cache-Control", "only-if-cached").build();
                        } else {
                            // 4 weeks stale
                            request.newBuilder().header("Cache-Control", "public, max-stale=2419200").build();
                        }
                    }
                    Response response = chain.proceed(request);
                    // Re-write response CC header to force use of cache
                    return response.newBuilder()
                            .header("Cache-Control", "public, max-age=86400") // 1 day
                            .build();
                }
            };
            Cache cache = new Cache(new File(context.getCacheDir(), "http"), SIZE_OF_CACHE);
            mOkHttpClient = new OkHttpClient();
            mOkHttpClient.setCache(cache);
            mOkHttpClient.setConnectTimeout(30, TimeUnit.SECONDS);
            mOkHttpClient.setReadTimeout(30, TimeUnit.SECONDS);
            // Add Cache-Control Interceptor
            mOkHttpClient.networkInterceptors().add(mCacheControlInterceptor);
            //重试
            mOkHttpClient.interceptors().add(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request request = chain.request();
                    // try the request
                    Response response = chain.proceed(request);
                    int tryCount = 0;
                    while (!response.isSuccessful() && tryCount < 3) {
                        tryCount++;
                        // retry the request
                        response = chain.proceed(request);
                    }
                    // otherwise just pass the original response on
                    return response;
                }
            });
            mOkHttpClient.setCookieHandler(new CookieManager(new PersistentCookieStore(context), CookiePolicy.ACCEPT_ALL));
        }
    }

    public OkHttpClient getOkHttpClient() {
        if (mOkHttpClient == null) {
            throw new RuntimeException("okhttp uninit!");
        }
        return mOkHttpClient;
    }

}
