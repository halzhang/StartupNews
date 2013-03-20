/**
 * Copyright (C) 2013 HalZhang
 */

package com.halzhang.android.apps.startupnews.ui.fragments;

import com.halzhang.android.apps.startupnews.R;
import com.halzhang.android.apps.startupnews.entity.SNFeed;
import com.halzhang.android.apps.startupnews.entity.SNNew;
import com.halzhang.android.apps.startupnews.parser.SNFeedParser;
import com.halzhang.android.apps.startupnews.ui.BrowseActivity;
import com.halzhang.android.apps.startupnews.ui.DiscussActivity;
import com.halzhang.android.apps.startupnews.utils.DateUtils;
import com.halzhang.android.apps.startupnews.utils.PreferenceUtils;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

/**
 * StartupNews
 * <p>
 * </p>
 * 
 * @author <a href="http://weibo.com/halzhang">Hal</a>
 * @version Mar 7, 2013
 */
public class NewsListFragment extends AbsBaseListFragment {

    private static final String LOG_TAG = NewsListFragment.class.getSimpleName();

    private NewsTask mNewsTask;

    private String mNewsURL;

    public static final String ARG_URL = "new_url";

    private SNFeed mSnFeed = new SNFeed();

    private NewsAdapter mAdapter;

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
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        registerForContextMenu(getListView());
        setListAdapter(mAdapter);
        if (mNewsTask == null && mAdapter.isEmpty()) {
            mNewsTask = new NewsTask(NewsTask.TYPE_REFRESH);
            mNewsTask.execute(mNewsURL);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        getActivity().getMenuInflater().inflate(R.menu.fragment_news, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int position = ((AdapterContextMenuInfo) item.getMenuInfo()).position;
        SNNew snNew = (SNNew) mAdapter.getItem(position - 1);
        switch (item.getItemId()) {
            case R.id.menu_show_comment:
                Intent intent = new Intent(getActivity(), DiscussActivity.class);
                intent.putExtra(DiscussActivity.ARG_SNNEW, snNew);
                startActivity(intent);
                break;
            case R.id.menu_show_article:
                openArticle(snNew);
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    protected void onPullDownListViewRefresh(PullToRefreshListView refreshListView) {
        super.onPullDownListViewRefresh(refreshListView);
        if (mNewsTask != null) {
            return;
        }
        mNewsTask = new NewsTask(NewsTask.TYPE_REFRESH);
        mNewsTask.execute(mNewsURL);
    }

    @Override
    protected void onPullUpListViewRefresh(PullToRefreshListView refreshListView) {
        super.onPullDownListViewRefresh(refreshListView);
        if (mNewsTask != null) {
            return;
        }
        if (TextUtils.isEmpty(mSnFeed.getMoreUrl())) {
            Toast.makeText(getActivity(), R.string.tip_last_page, Toast.LENGTH_SHORT).show();
            getPullToRefreshListView().onRefreshComplete();
        } else {
            mNewsTask = new NewsTask(NewsTask.TYPE_LOADMORE);
            mNewsTask.execute(mSnFeed.getMoreUrl());
        }
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        SNNew entity = (SNNew) mAdapter.getItem(position - 1);
        openArticle(entity);
    }

    private void openArticle(SNNew snNew) {
        if (snNew == null) {
            return;
        }
        Intent intent = null;
        if (PreferenceUtils.isUseInnerBrowse(getActivity())) {
            intent = new Intent(getActivity(), BrowseActivity.class);
            intent.putExtra(BrowseActivity.EXTRA_URL, snNew.getUrl());
            intent.putExtra(BrowseActivity.EXTRA_TITLE, snNew.getTitle());
        } else {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(PreferenceUtils.getHtmlProvider(getActivity())
                    + snNew.getUrl()));
        }
        startActivity(intent);

    }

    private class NewsTask extends AsyncTask<String, Void, Boolean> {

        public static final int TYPE_REFRESH = 1;

        public static final int TYPE_LOADMORE = 2;

        private int mType = 0;

        public NewsTask(int type) {
            mType = type;
        }

        @Override
        protected Boolean doInBackground(String... params) {
            try {
                Document doc = Jsoup.connect(params[0]).get();
                SNFeedParser parser = new SNFeedParser();
                SNFeed feed = parser.parseDocument(doc);
                if (mType == TYPE_REFRESH && mSnFeed.size() > 0) {
                    mSnFeed.clear();
                }
                mSnFeed.addNews(feed.getSnNews());
                mSnFeed.setMoreUrl(feed.getMoreUrl());
                return true;
            } catch (IOException e) {
                Log.e(LOG_TAG, "", e);
                return false;
            } catch (Exception e) {
                Log.e(LOG_TAG, "", e);
                return false;
            }

        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                onDataFirstLoadComplete();
                mAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(getActivity(), R.string.error, Toast.LENGTH_LONG).show();
            }
            mNewsTask = null;
            getPullToRefreshListView().getLoadingLayoutProxy().setLastUpdatedLabel(
                    DateUtils.getLastUpdateLabel(getActivity()));
            getPullToRefreshListView().onRefreshComplete();
            super.onPostExecute(result);
        }

        @Override
        protected void onCancelled() {
            getPullToRefreshListView().onRefreshComplete();
            mNewsTask = null;
            super.onCancelled();
        }

    }

    @Override
    public int getContentViewId() {
        return R.layout.ptr_list_layout;
    }

    private class NewsAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mSnFeed.size();
        }

        @Override
        public Object getItem(int position) {
            return mSnFeed.getSnNews().get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(getActivity()).inflate(R.layout.news_list_item,
                        null);
                holder.mText1 = (TextView) convertView.findViewById(android.R.id.text1);
                holder.mText2 = (TextView) convertView.findViewById(android.R.id.text2);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            SNNew entity = mSnFeed.getSnNews().get(position);
            holder.mText1.setText(entity.getTitle());
            holder.mText2.setText(Html.fromHtml(entity.getSubText()));
            return convertView;
        }

        class ViewHolder {
            TextView mText1;

            TextView mText2;
        }

    }

}
