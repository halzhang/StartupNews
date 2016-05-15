package com.halzhang.android.apps.startupnews.presenter;

import android.os.Bundle;

import com.halzhang.android.mvp.presenter.Presenter;
import com.halzhang.android.startupnews.data.entity.SNNew;

import java.util.ArrayList;

/**
 * 新闻列表
 * Created by zhanghanguo@yy.com on 2016/5/12.
 */
public class NewsListPresenter extends Presenter<NewsListPresenter.INewsListView, NewsListPresenter.INewsListCallback> {


    @Override
    public INewsListCallback createViewCallback(IView view) {
        return new INewsListCallback() {
            @Override
            public void refresh(String url) {
                // TODO: 16/5/15 call
            }

            @Override
            public void loadMore() {
                // TODO: 16/5/15 call
            }
        };
    }

    /**
     * 业务接口
     */
    public interface INewsListCallback {

        /**
         * 刷新
         *
         * @param url
         */
        void refresh(String url);

        /**
         * 加载更多
         */
        void loadMore();

    }

    public interface INewsListView extends Presenter.IView<INewsListCallback> {

        /**
         * 刷新
         *
         * @param snNews 列表数据
         */
        void onRefresh(ArrayList<SNNew> snNews);

        /**
         * 加载更多
         *
         * @param snNews 列表数据
         */
        void onLoadMore(ArrayList<SNNew> snNews);

        /**
         * 是否已经激活
         *
         * @return true 已激活
         */
        boolean isActive();
    }

    public static final String KEY_URL = "key_url";


    @Override
    protected void onCreate(Bundle saveState) {
        super.onCreate(saveState);
    }

    @Override
    protected void onSave(Bundle saveState) {
        super.onSave(saveState);
    }

    @Override
    protected void onAttachView(INewsListView view) {
        super.onAttachView(view);
    }
}
