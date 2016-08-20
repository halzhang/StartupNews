/**
 * Copyright (C) 2013 HalZhang
 */

package com.halzhang.android.apps.startupnews.ui.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.TextView;
import android.widget.Toast;

import com.halzhang.android.apps.startupnews.Constants.IntentAction;
import com.halzhang.android.apps.startupnews.MyApplication;
import com.halzhang.android.apps.startupnews.R;
import com.halzhang.android.apps.startupnews.SnApiComponent;
import com.halzhang.android.apps.startupnews.analytics.Tracker;
import com.halzhang.android.apps.startupnews.presenter.DaggerNewsListFragmentComponent;
import com.halzhang.android.apps.startupnews.presenter.NewsListContract;
import com.halzhang.android.apps.startupnews.presenter.NewsListPresenter;
import com.halzhang.android.apps.startupnews.presenter.NewsListPresenterModule;
import com.halzhang.android.apps.startupnews.ui.DiscussActivity;
import com.halzhang.android.apps.startupnews.utils.AppUtils;
import com.halzhang.android.common.CDLog;
import com.halzhang.android.startupnews.data.entity.SNFeed;
import com.halzhang.android.startupnews.data.entity.SNNew;

import java.util.ArrayList;

import javax.inject.Inject;

/**
 * StartupNews
 * <p>
 * </p>
 *
 * @author <a href="http://weibo.com/halzhang">Hal</a>
 * @version Mar 7, 2013
 */
public class NewsListFragment extends SwipeRefreshRecyclerFragment implements NewsListContract.View {

    private static final String LOG_TAG = NewsListFragment.class.getSimpleName();

    @Inject
    NewsListPresenter mNewsListPresenter;

    private NewsListContract.Presenter mPresenter;

    @Override
    public void setPresenter(NewsListContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }

    @Override
    public void onSuccess(ArrayList<SNNew> snNews) {
        onRefreshComplete();
        mSnFeed.setSnNews(snNews);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onFailure(Throwable e) {
        CDLog.w(LOG_TAG, "", e);
        onRefreshComplete();
        Toast.makeText(getActivity(), R.string.error, Toast.LENGTH_LONG).show();
        Tracker.getInstance().sendException("NewsTask", e, false);
    }

    @Override
    public void onAtEnd() {
        onRefreshComplete();
        Toast.makeText(getActivity(), R.string.tip_last_page, Toast.LENGTH_SHORT).show();
    }

    /**
     * {@link NewsListFragment}选中监听器
     */
    public interface OnNewsSelectedListener {
        /**
         * 处理news被选中事件
         *
         * @param position list position
         * @param snNew    {@link SNNew}
         */
        public void onNewsSelected(int position, SNNew snNew);
    }

    private OnNewsSelectedListener mNewsSelectedListener;


    private String mNewsURL;

    public static final String ARG_URL = "new_url";

    private SNFeed mSnFeed = new SNFeed();

    private NewsAdapter mAdapter;

    public NewsListFragment() {

    }

    public static NewsListFragment newInstance(String url) {
        NewsListFragment fragment = new NewsListFragment();
        Bundle args = new Bundle(1);
        args.putString(ARG_URL, url);
        fragment.setArguments(args);
        return fragment;
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (IntentAction.ACTION_LOGIN.equals(action)) {
                String user = intent.getStringExtra(IntentAction.EXTRA_LOGIN_USER);
                if (!TextUtils.isEmpty(user)) {
                    mPresenter.getFeed(mNewsURL);
                }
            }
        }
    };

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mNewsSelectedListener = (OnNewsSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnNewsSelectedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mNewsSelectedListener = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new NewsAdapter();
        Bundle args = getArguments();
        if (args != null) {
            mNewsURL = args.getString(ARG_URL);
        } else {
            mNewsURL = getString(R.string.host, "/news");
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction(IntentAction.ACTION_LOGIN);
        getActivity().registerReceiver(mReceiver, filter);
        SnApiComponent snApiComponent = ((MyApplication) getActivity().getApplication()).getSnApiComponent();
        DaggerNewsListFragmentComponent.builder().snApiComponent(snApiComponent)
                .newsListPresenterModule(new NewsListPresenterModule(this)).build().inject(this);
    }

    @Override
    protected int getViewLayout() {
        return R.layout.refresh_recycler_view_layout;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mRecyclerView.setAdapter(mAdapter);
        if (mAdapter.isEmpty()) {
            mPresenter.getFeed(mNewsURL);
            mSwipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    mSwipeRefreshLayout.setRefreshing(true);
                }
            });
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        CDLog.d(LOG_TAG, this.toString() + " destroy view!");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(mReceiver);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int position = ((AdapterContextMenuInfo) item.getMenuInfo()).position;
        final SNNew snNew = mSnFeed.getSnNews().get(position);
        Log.i(LOG_TAG, snNew.toString());
        switch (item.getItemId()) {
            case R.id.menu_show_comment:
                Tracker.getInstance().sendEvent("ui_action", "context_item_selected",
                        "newslistfragment_menu_show_comment", 0L);
                openDiscuss(snNew);
                return true;
            case R.id.menu_show_article:
                Tracker.getInstance().sendEvent("ui_action", "context_item_selected",
                        "newslistfragment_menu_show_acticle", 0L);
                openArticle(position - 1, snNew);
                return true;
            case R.id.menu_up_vote:
                Tracker.getInstance().sendEvent("ui_action", "context_item_selected",
                        "newslistfragment_menu_upvote", 0L);

                // TODO: 16/8/14 投票操作

                return true;
            default:
                break;
        }
        return true;
    }

    @Override
    protected void onRefreshData() {
        super.onRefreshData();
        Tracker.getInstance().sendEvent("ui_action", "pull_down_list_view_refresh", "news_list_fragment_pull_down_list_view_refresh", 0L);
        mPresenter.getFeed(mNewsURL);
    }

    @Override
    protected void onLoadMore() {
        super.onLoadMore();
        Tracker.getInstance().sendEvent("ui_action", "pull_up_list_view_refresh", "news_list_fragment_pull_up_list_view_refresh", 0L);

        mPresenter.getMoreFeed();
    }

    private void openArticle(int position, SNNew snNew) {
        if (mNewsSelectedListener != null) {
            mNewsSelectedListener.onNewsSelected(position, snNew);
        }
    }

    private void openDiscuss(SNNew snNew) {
        if (snNew == null) {
            return;
        }

        DiscussActivity.start(getActivity(),snNew.getDiscussURL(),snNew);
    }

    private class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {

        public final class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {

            public final TextView user;

            public final TextView createAt;

            public final TextView title;

            public final TextView subText;

            public final TextView domain;

            public final View mView;

            public ViewHolder(View itemView) {
                super(itemView);
                mView = itemView;
                user = (TextView) itemView.findViewById(R.id.news_item_user);
                createAt = (TextView) itemView.findViewById(R.id.news_item_createat);
                title = (TextView) itemView.findViewById(R.id.news_item_title);
                subText = (TextView) itemView.findViewById(R.id.news_item_subtext);
                domain = (TextView) itemView.findViewById(R.id.news_item_domain);
                mView.setOnCreateContextMenuListener(this);
                mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = (int) v.getTag();
                        SNNew entity = mSnFeed.getSnNews().get(position);
                        Tracker.getInstance().sendEvent("ui_action", "list_item_click",
                                "news_list_fragment_list_item_click", 0L);
                        mAdapter.notifyDataSetChanged();
                        openArticle(position, entity);
                    }
                });
            }

            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
                getActivity().getMenuInflater().inflate(R.menu.fragment_news, menu);
            }
        }


        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.news_list_item, null));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            final SNNew entity = mSnFeed.getSnNews().get(position);
            holder.user.setText(entity.getUser().getId());
            holder.title.setText(entity.getTitle());
            holder.subText.setText(getString(R.string.news_subtext, entity.getPoints(),
                    entity.getCommentsCount()));
            holder.createAt.setText(entity.getCreateat());
            holder.domain.setText(entity.getUrlDomain());
            int textColor = AppUtils.getMyApplication(getActivity()).isHistoryContains(
                    entity.getUrl()) ? Color.GRAY : Color.BLACK;
            holder.title.setTextColor(textColor);
            holder.subText.setTextColor(textColor);
            holder.domain.setTextColor(textColor);
            holder.createAt.setTextColor(textColor);
            holder.mView.setTag(position);
        }

        @Override
        public int getItemCount() {
            return mSnFeed.getSnNews().size();
        }

        public boolean isEmpty() {
            return getItemCount() == 0;
        }

    }

}
