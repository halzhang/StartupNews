/**
 * Copyright (C) 2013 HalZhang
 */

package com.halzhang.android.apps.startupnews.ui.fragments;

import com.halzhang.android.apps.startupnews.R;
import com.halzhang.android.apps.startupnews.entity.Comment;
import com.halzhang.android.apps.startupnews.entity.User;
import com.halzhang.android.apps.startupnews.utils.DateUtils;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * StartupNews
 * <p>
 * 评论
 * </p>
 * 
 * @author <a href="http://weibo.com/halzhang">Hal</a>
 * @version Mar 7, 2013
 */
public class CommentsListFragment extends AbsBaseListFragment {

    @SuppressWarnings("unused")
    private static final String LOG_TAG = CommentsListFragment.class.getSimpleName();

    private ArrayList<Comment> mComments = new ArrayList<Comment>(24);

    private CommentsAdapter mAdapter;

    private CommentsTask mTask;

    private String mMoreUrl;

    private static final String NEWCOMMENTS_URL_PATH = "/newcomments";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new CommentsAdapter();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setListAdapter(mAdapter);
        if (mTask == null && mAdapter.isEmpty()) {
            mTask = new CommentsTask(CommentsTask.TYPE_REFRESH);
            mTask.execute(getString(R.string.host, NEWCOMMENTS_URL_PATH));
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
    protected void onPullDownListViewRefresh(PullToRefreshListView refreshListView) {
        super.onPullDownListViewRefresh(refreshListView);
        if (mTask != null) {
            return;
        }
        mTask = new CommentsTask(CommentsTask.TYPE_REFRESH);
        mTask.execute(getString(R.string.host, NEWCOMMENTS_URL_PATH));
    }

    @Override
    protected void onPullUpListViewRefresh(PullToRefreshListView refreshListView) {
        super.onPullUpListViewRefresh(refreshListView);
        if (mTask != null) {
            return;
        }
        mTask = new CommentsTask(CommentsTask.TYPE_LOADMORE);
        mTask.execute(getString(R.string.host, TextUtils.isEmpty(mMoreUrl) ? NEWCOMMENTS_URL_PATH
                : mMoreUrl));
        mMoreUrl = null;
    }

    private class CommentsTask extends AsyncTask<String, Void, Boolean> {

        public static final int TYPE_REFRESH = 1;

        public static final int TYPE_LOADMORE = 2;

        private int mType = 0;

        public CommentsTask(int type) {
            mType = type;
        }

        @Override
        protected Boolean doInBackground(String... params) {
            try {
                Document doc = Jsoup.connect(params[0]).get();
                Element body = doc.body();
                Elements commentSpans = body.select("span.comment");
                Elements comHeadSpans = body.select("span.comhead");
                if (!commentSpans.isEmpty()) {
                    if (mType == TYPE_REFRESH && mComments.size() > 0) {
                        mComments.clear();
                    }
                    Iterator<Element> spanCommentIt = commentSpans.iterator();
                    Iterator<Element> spanComHeadIt = comHeadSpans.iterator();
                    Comment comment = null;
                    User user = null;
                    while (spanComHeadIt.hasNext() && spanCommentIt.hasNext()) {
                        String commentText = spanCommentIt.next().text();
                        Element span = spanComHeadIt.next();
                        Elements as = span.getElementsByTag("a");
                        user = new User();
                        user.setId(as.get(0).text());
                        String link = as.get(1).attr("href");
                        String parent = as.get(2).attr("href");
                        String discuss = as.get(3).attr("href");
                        String title = as.get(3).text();
                        comment = new Comment();
                        comment.setUser(user);
                        comment.setLink(link);
                        comment.setParent(parent);
                        comment.setDiscuss(discuss);
                        comment.setText(commentText);
                        comment.setArtistTitle(title);
                        mComments.add(comment);
                    }
                }
                mMoreUrl = body.select("a[href^=/x?fnid=]").get(1).attr("href");
                return true;
            } catch (IOException e) {
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
            getPullToRefreshListView().getLoadingLayoutProxy().setLastUpdatedLabel(
                    DateUtils.getLastUpdateLabel(getActivity()));
            getPullToRefreshListView().onRefreshComplete();
            mTask = null;
            super.onPostExecute(result);
        }

        @Override
        protected void onCancelled() {
            getPullToRefreshListView().onRefreshComplete();
            mTask = null;
            super.onCancelled();
        }

    }

    private class CommentsAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mComments.size();
        }

        @Override
        public Object getItem(int position) {
            return mComments.get(position);
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
                        R.layout.comment_list_item, null);
                holder.mUserId = (TextView) convertView.findViewById(R.id.comment_item_user_id);
                holder.mCreated = (TextView) convertView.findViewById(R.id.comment_item_created);
                holder.mCommentText = (TextView) convertView.findViewById(R.id.comment_item_text);
                holder.mArtistTitle = (TextView) convertView.findViewById(R.id.comment_item_artist_titile);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            Comment comment = mComments.get(position);
            holder.mUserId.setText(comment.getUser().getId());
            holder.mCreated.setText(comment.getCreated());
            holder.mCommentText.setText(comment.getText());
            holder.mArtistTitle.setText(getString(R.string.comment_artist_title, comment.getArtistTitle()));
            return convertView;
        }

        class ViewHolder {
            TextView mUserId;

            TextView mCreated;

            TextView mCommentText;
            
            TextView mArtistTitle;
        }

    }

    @Override
    public int getContentViewId() {
        return R.layout.ptr_list_layout;
    }

}
