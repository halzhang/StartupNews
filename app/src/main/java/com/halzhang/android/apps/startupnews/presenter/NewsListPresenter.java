package com.halzhang.android.apps.startupnews.presenter;

import com.halzhang.android.mvp.presenter.Presenter;

/**
 * 新闻列表
 * Created by zhanghanguo@yy.com on 2016/5/12.
 */
public class NewsListPresenter extends Presenter<NewsListPresenter.INewsListView, NewsListPresenter.INewsListCallback> {


    @Override
    public INewsListCallback createViewCallback(IView view) {
        return null;
    }

    /**
     *
     */
    public interface INewsListCallback {

    }

    public interface INewsListView extends Presenter.IView<INewsListCallback> {

    }
}
