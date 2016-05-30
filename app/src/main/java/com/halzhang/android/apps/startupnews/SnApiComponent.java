package com.halzhang.android.apps.startupnews;

import com.halzhang.android.startupnews.data.JsoupConnectorModule;
import com.halzhang.android.startupnews.data.OkHttpClientModule;
import com.halzhang.android.startupnews.data.SessionManagerModule;
import com.halzhang.android.startupnews.data.net.ISnApi;
import com.halzhang.android.startupnews.data.SnApiModule;
import com.halzhang.android.startupnews.data.net.JsoupConnector;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by zhanghanguo@yy.com on 2016/5/30.
 */
@Singleton
@Component(modules = {ApplicationModule.class, SnApiModule.class, OkHttpClientModule.class, SessionManagerModule.class, JsoupConnectorModule.class})
public interface SnApiComponent {

    ISnApi getSnApi();

}
