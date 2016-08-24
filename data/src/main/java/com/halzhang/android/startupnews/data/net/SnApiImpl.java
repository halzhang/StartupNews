package com.halzhang.android.startupnews.data.net;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.halzhang.android.startupnews.data.Constant;
import com.halzhang.android.startupnews.data.entity.SNComments;
import com.halzhang.android.startupnews.data.entity.SNDiscuss;
import com.halzhang.android.startupnews.data.entity.SNFeed;
import com.halzhang.android.startupnews.data.entity.Status;
import com.halzhang.android.startupnews.data.exception.NetworkException;
import com.halzhang.android.startupnews.data.parser.BaseHTMLParser;
import com.halzhang.android.startupnews.data.parser.SNCommentsParser;
import com.halzhang.android.startupnews.data.parser.SNDiscussParser;
import com.halzhang.android.startupnews.data.parser.SNFeedParser;
import com.halzhang.android.startupnews.data.utils.SessionManager;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
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
import rx.functions.Action0;

/**
 * api impl
 * Created by Hal on 2016/5/30.
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
                FormEncodingBuilder builder = new FormEncodingBuilder();
                builder.addEncoded("fnid", fnid).addEncoded("u", username).addEncoded("p", password);
                Request request = new Request.Builder().url(Constant.LOGIN_URL).post(builder.build())
                        .addHeader("Accept-Language", "zh-cn")
                        .addHeader("Accept", "*/*")
                        .addHeader("Accept-Encoding", "gzip,deflate")
                        .addHeader("Connection", "keep-alive")
                        .build();
                try {
                    String user = null;
                    Response response = mOkHttpClient.newCall(request).execute();
                    if (response.isSuccessful()) {
                        Map<String, List<String>> cookiesMap = mOkHttpClient.getCookieHandler().get(request.uri(), OkHeaders.toMultimap(request.headers(), null));
                        if (cookiesMap.size() > 0) {
                            List<String> cookies = cookiesMap.get("Cookie");
                            for (String s : cookies) {
                                String[] cookie = TextUtils.split(s, "=");
                                if (cookie.length == 2 && "user".equals(cookie[0])) {
                                    user = cookie[1];
                                    break;
                                }
                            }
                        }
                    }
                    response.body().close();
                    mSessionManager.storeSession(user, username);
                    subscriber.onNext(user);
                    subscriber.onCompleted();
                } catch (IOException e) {
                    subscriber.onError(e);
                }

            }
        }).doOnSubscribe(new Action0() {
            @Override
            public void call() {
                mSessionManager.clear();
            }
        });
    }

    @Override
    public Observable<SNComments> getSNComments(final String url) {
        return Observable.create(new Observable.OnSubscribe<SNComments>() {
            @Override
            public void call(Subscriber<? super SNComments> subscriber) {
                try {
                    Connection conn = mJsoupConnector.newJsoupConnection(url);
                    Document doc = conn.get();
                    SNCommentsParser parser = new SNCommentsParser();
                    SNComments comments = parser.parseDocument(doc);
                    subscriber.onNext(comments);
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    @Override
    public Observable<Status> upVote(final String postId) {
        return Observable.create(new Observable.OnSubscribe<Status>() {
            @Override
            public void call(Subscriber<? super Status> subscriber) {
                try {
                    HttpUrl httpUrl = new HttpUrl.Builder()
                            .scheme(Constant.SCHEME)
                            .host(Constant.HOST)
                            .addPathSegment("vote")
                            .addQueryParameter("for", postId)
                            .addQueryParameter("dir", "up")
                            .addQueryParameter("by", mSessionManager.getSessionId())
                            .addQueryParameter("auth", mSessionManager.getSessionUser())
                            .addQueryParameter("whence", "news")
                            .build();
                    Request request = new Request.Builder().url(httpUrl).build();
                    Response response = mOkHttpClient.newCall(request).execute();
                    Status status = new Status();
                    if (response.isSuccessful()) {
                        status.code = Status.CODE_SUCCESS;
                    } else {
                        String content = response.body().string();

                        if (content.contains("mismatch")) {
                            // 用户cookie无效
                            status.code = Status.CODE_COOKIE_VALID;
                        } else {
                            status.code = Status.CODE_REPEAT;
                        }
                    }
                    subscriber.onNext(status);
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    @Override
    public Observable<Status> comment(final String text, final String fnid) {
        return Observable.create(new Observable.OnSubscribe<Status>() {
            @Override
            public void call(Subscriber<? super Status> subscriber) {
                try {
                    RequestBody body = new FormEncodingBuilder().add("fnid", fnid).add("text", text).build();
                    Request request = new Request.Builder().url(Constant.COMMENT_URL).post(body).build();
                    Response response = mOkHttpClient.newCall(request).execute();
                    if (response.isSuccessful()) {
                        String refreerLocation = null;
                        Headers headers = response.headers();
                        for (int i = 0; i < headers.size(); i++) {
                            if ("Refreer-Location".equals(headers.name(i))) {
                                refreerLocation = headers.value(i);
                            }
                        }
                        Status status = new Status();
                        if (TextUtils.isEmpty(refreerLocation) || refreerLocation.contains("fnid")) {
                            //Location:fnid=xxxxx Cookie失效，重新登陆
                            status.code = Status.CODE_COOKIE_VALID;
                        } else if (refreerLocation.contains("item")) {
                            status.code = Status.CODE_SUCCESS;
                        } else {
                            status.code = Status.CODE_FAILURE;
                        }
                        subscriber.onNext(status);
                        subscriber.onCompleted();
                    } else {
                        NetworkException networkException = new NetworkException(response.code(),
                                response.message());
                        subscriber.onError(networkException);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    subscriber.onError(e);
                }
            }
        });
    }

    @Override
    public Observable<Boolean> logout() {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                try {
                    String logoutUrl = null;
                    Boolean result = false;
                    Connection conn = mJsoupConnector.newJsoupConnection(Constant.HOST + "/news");
                    Document doc = conn.get();
                    Elements elements = doc.select("a:matches(logout)");
                    if (elements.size() > 0) {
                        logoutUrl = BaseHTMLParser.resolveRelativeSNURL(elements.attr("href"));
                    } else {
                        // 用户可能在pc注销了
                        mSessionManager.clear();
                        result = true;
                    }
                    if (!TextUtils.isEmpty(logoutUrl)) {
                        Request request = new Request.Builder().url(logoutUrl).build();
                        Response response = mOkHttpClient.newCall(request).execute();
                        result = response.isSuccessful();
                    }
                    subscriber.onNext(result);
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    @Override
    public Observable<SNDiscuss> getDiscuss(final String url) {
        return Observable.create(new Observable.OnSubscribe<SNDiscuss>() {
            @Override
            public void call(Subscriber<? super SNDiscuss> subscriber) {
                try {
                    Connection conn = mJsoupConnector.newJsoupConnection(url);
                    Document doc = conn.get();
                    SNDiscussParser parser = new SNDiscussParser();
                    SNDiscuss discuss = parser.parseDocument(doc);
                    subscriber.onNext(discuss);
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                }

            }
        });
    }
}
