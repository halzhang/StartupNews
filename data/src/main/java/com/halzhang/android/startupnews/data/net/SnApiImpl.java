package com.halzhang.android.startupnews.data.net;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.halzhang.android.startupnews.data.Constant;
import com.halzhang.android.startupnews.data.entity.SNFeed;
import com.halzhang.android.startupnews.data.parser.BaseHTMLParser;
import com.halzhang.android.startupnews.data.parser.SNFeedParser;
import com.halzhang.android.startupnews.data.utils.SessionManager;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.internal.http.OkHeaders;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.inject.Singleton;

import rx.Observable;
import rx.Subscriber;

/**
 * api impl
 * Created by zhanghanguo@yy.com on 2016/5/30.
 */
@Singleton
public class SnApiImpl implements ISnApi {

    private OkHttpClient mOkHttpClient;
    private Context mContext;
    private SessionManager mSessionManager;
    private JsoupConnector mJsoupConnector;

    public SnApiImpl(OkHttpClient okHttpClient, Context context, SessionManager sessionManager, JsoupConnector jsoupConnector) {
        mOkHttpClient = okHttpClient;
        mContext = context;
        mSessionManager = sessionManager;
        mJsoupConnector = jsoupConnector;
    }

    @Override
    public Observable<SNFeed> getSNFeed(final String url) {
        return Observable.create(new Observable.OnSubscribe<SNFeed>() {
            @Override
            public void call(Subscriber<? super SNFeed> subscriber) {
                try {
                    Connection conn = mJsoupConnector.newJsoupConnection(url);
                    Document doc = conn.get();
                    SNFeedParser parser = new SNFeedParser();
                    SNFeed feed = parser.parseDocument(doc);
                    subscriber.onNext(feed);
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    @Override
    public Observable<String> getFnid() {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                String loginUrl = null;
                try {
                    Document doc = Jsoup.connect(Constant.NEWS_URL).get();
                    if (doc != null) {
                        Elements loginElements = doc.select("a:matches(Login/Register)");
                        if (loginElements.size() == 1) {
                            loginUrl = BaseHTMLParser.resolveRelativeSNURL(loginElements.first().attr(
                                    "href"));
                        }
                    }
                    String fnid = null;
                    if (!TextUtils.isEmpty(loginUrl)) {
                        doc = Jsoup.connect(loginUrl).get();
                        if (doc != null) {
                            Elements inputElements = doc.select("input[name=fnid]");
                            if (inputElements != null && inputElements.size() > 0) {
                                fnid = inputElements.first().attr("value");
                            }
                        }
                    }
                    subscriber.onNext(fnid);
                    subscriber.onCompleted();
                } catch (IOException e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    @Override
    public Observable<String> login(final String fnid, final String username, final String password) {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                String user = null;
                FormEncodingBuilder builder = new FormEncodingBuilder();
                builder.addEncoded("fnid", fnid).addEncoded("u", username).addEncoded("p", password);
                Request request = new Request.Builder().url(Constant.LOGIN_URL).post(builder.build())
                        .addHeader("Accept-Language", "zh-cn")
                        .addHeader("Accept", "*/*")
                        .addHeader("Accept-Encoding", "gzip,deflate")
                        .addHeader("Connection", "keep-alive")
                        .build();
                try {
                    Response response = mOkHttpClient.newCall(request).execute();
                    if (response.isSuccessful()) {
                        Map<String, List<String>> cookiesMap = mOkHttpClient.getCookieHandler().get(request.uri(), OkHeaders.toMultimap(request.headers(), null));
                        if (cookiesMap.size() > 0) {
                            List<String> cookies = cookiesMap.get("Cookie");
                            for (String s : cookies) {
                                String[] cookie = TextUtils.split(s, "=");
                                if (cookie.length == 2 && "user".equals(cookie[0])) {
                                    user = cookie[1];
                                }
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    subscriber.onError(e);
                }
                mSessionManager.storeSession(user, username);
                subscriber.onNext(user);
                subscriber.onCompleted();
            }
        });
    }
}
