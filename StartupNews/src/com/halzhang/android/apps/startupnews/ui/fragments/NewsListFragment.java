/**
 * Copyright (C) 2013 HalZhang
 */

package com.halzhang.android.apps.startupnews.ui.fragments;

import com.halzhang.android.apps.startupnews.R;
import com.halzhang.android.apps.startupnews.entity.NewEntity;
import com.halzhang.android.apps.startupnews.ui.BrowseActivity;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * StartupNews
 * <p>
 * 
 * </p>
 * 
 * @author <a href="http://weibo.com/halzhang">Hal</a>
 * @version Mar 7, 2013
 */
public class NewsListFragment extends AbsBaseListFragment {

    private static final String LOG_TAG = NewsListFragment.class.getSimpleName();

    private NewsTask mNewsTask;

    private String mMoreURLPath;

    private String mNewsURL;

    public static final String ARG_URL = "new_url";

    private ArrayList<NewEntity> mNews = new ArrayList<NewEntity>(32);

    private NewsAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new NewsAdapter();
        if (mNewsTask == null) {
            Bundle args = getArguments();
            if (args != null) {
                mNewsURL = args.getString(ARG_URL);
            } else {
                mNewsURL = getString(R.string.host, "/news");
            }
            mNewsTask = new NewsTask();
            mNewsTask.execute(mNewsURL);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setListAdapter(mAdapter);
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
    protected void onPullUpListViewRefresh(PullToRefreshListView refreshListView) {
        super.onPullDownListViewRefresh(refreshListView);
        if (mNewsTask == null && !TextUtils.isEmpty(mMoreURLPath)) {
            mNewsTask = new NewsTask();
            mNewsTask.execute(getString(R.string.host, mMoreURLPath));
            mMoreURLPath = null;
        }
    }
    
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        NewEntity entity =(NewEntity) mAdapter.getItem(position -1);
        Intent intent = new Intent(getActivity(), BrowseActivity.class);
        intent.putExtra(BrowseActivity.EXTRA_URL, entity.getUrl());
        intent.putExtra(BrowseActivity.EXTRA_TITLE, entity.getTitle());
        startActivity(intent);
    }

    private class NewsTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            try {
                Log.d(LOG_TAG, "Doc request start! " + params[0]);
                Document doc = Jsoup.connect(params[0]).get();
                Log.d(LOG_TAG, "Doc init OK!");
                Element body = doc.body();
                Elements titleEs = body.getElementsByClass("title");
                Elements subTitleEs = body.getElementsByClass("subtext");
                int index = 1;
                if (!titleEs.isEmpty()) {
                    Iterator<Element> iterator = titleEs.iterator();
                    Iterator<Element> subIt = subTitleEs.iterator();
                    NewEntity entity = null;
                    while (iterator.hasNext()) {
                        Element e = iterator.next();
                        if (index % 2 == 0) {
                            Element subE = subIt.next();

                            entity = new NewEntity(e.select("a").get(0).attr("href"), e.select("a")
                                    .get(0).text(), /*
                                                     * e.select("span").get(0).text
                                                     * ()
                                                     */null, subE.html());
                            // Log.i(LOG_TAG, entity.toString());
                            mNews.add(entity);
                        }
                        index++;
                    }
                }
                Elements more = doc.getElementsByAttributeValueStarting("href", "/x?fnid=");
                if (!more.isEmpty()) {
                    mMoreURLPath = more.get(1).attr("href");
                    Log.i(LOG_TAG, "More Path: " + mMoreURLPath);
                }

                return true;
            } catch (IOException e) {
                Log.e(LOG_TAG, "", e);
                return false;
            }

        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                mAdapter.notifyDataSetChanged();
            }else{
                Toast.makeText(getActivity(), R.string.error, Toast.LENGTH_LONG).show();
            }
            mNewsTask = null;
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
            return mNews.size();
        }

        @Override
        public Object getItem(int position) {
            return mNews.get(position);
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
                convertView = LayoutInflater.from(getActivity()).inflate(
                        android.R.layout.simple_list_item_2, null);
                holder.mText1 = (TextView) convertView.findViewById(android.R.id.text1);
                holder.mText2 = (TextView) convertView.findViewById(android.R.id.text2);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            NewEntity entity = mNews.get(position);
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
