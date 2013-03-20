/**
 * Copyright (C) 2013 HalZhang
 */

package com.halzhang.android.apps.startupnews.ui;

import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.MenuItem;
import com.halzhang.android.apps.startupnews.R;
import com.halzhang.android.apps.startupnews.entity.SNComment;
import com.halzhang.android.apps.startupnews.entity.SNDiscuss;
import com.halzhang.android.apps.startupnews.entity.SNNew;
import com.halzhang.android.apps.startupnews.parser.SNDiscussParser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;

/**
 * StartupNews
 * <p>
 * 评论界面
 * </p>
 * 
 * @author <a href="http://weibo.com/halzhang">Hal</a>
 * @version Mar 17, 2013
 */
public class DiscussActivity extends SherlockListActivity {

    private static final String LOG_TAG = DiscussActivity.class.getSimpleName();

    public static final String ARG_DISCUSS_URL = "discuss_url";

    public static final String ARG_SNNEW = "snnew";

    private SNDiscuss mSnDiscuss;

    private ListView mListView;

    private String mDiscussURL;

    private DiscussCommentAdapter mAdapter;

    private DiscussTask mDiscussTask;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_discuss);
        mListView = getListView();
        mSnDiscuss = new SNDiscuss();
        SNNew snNew = (SNNew) getIntent().getSerializableExtra(ARG_SNNEW);
        mSnDiscuss.setSnNew(snNew);
        mDiscussURL = snNew.getDiscussURL();
        mAdapter = new DiscussCommentAdapter();
        mListView.addHeaderView(wrapHeaderView(snNew));
        mListView.setAdapter(mAdapter);
        mDiscussTask = new DiscussTask();
        mDiscussTask.execute(mDiscussURL);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    
    private View wrapHeaderView(SNNew snNew){
        View view = LayoutInflater.from(this).inflate(R.layout.news_list_item, null);
        if(snNew != null){
            TextView text1 =(TextView) view.findViewById(android.R.id.text1);
            TextView text2 =(TextView) view.findViewById(android.R.id.text2);
            text1.setText(snNew.getTitle());
            text2.setText(Html.fromHtml(snNew.getSubText()));
        }
        return view;
    }
    

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;

            default:
                break;
        }
        return true;
    }

    private class DiscussTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            try {
                Document doc = Jsoup.connect(params[0]).get();
                SNDiscussParser parser = new SNDiscussParser();
                SNDiscuss discuss = parser.parseDocument(doc);
                mSnDiscuss.copy(discuss);
            } catch (IOException e) {
                return false;
            } catch (Exception e) {
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                mAdapter.notifyDataSetChanged();
            }
            mDiscussTask = null;
            super.onPostExecute(result);
        }

        @Override
        protected void onCancelled() {
            mDiscussTask = null;
            super.onCancelled();
        }

    }

    private class DiscussCommentAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mSnDiscuss.commentSize();
        }

        @Override
        public Object getItem(int position) {
            return mSnDiscuss.getComments().get(position);
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
                convertView = LayoutInflater.from(DiscussActivity.this).inflate(
                        R.layout.comment_list_item, null);
                holder.mUserId = (TextView) convertView.findViewById(R.id.comment_item_user_id);
                holder.mCreated = (TextView) convertView.findViewById(R.id.comment_item_created);
                holder.mCommentText = (TextView) convertView.findViewById(R.id.comment_item_text);
                holder.mArtistTitle = (TextView) convertView
                        .findViewById(R.id.comment_item_artist_titile);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            SNComment comment = mSnDiscuss.getComments().get(position);
            holder.mUserId.setText(comment.getUser().getId());
            holder.mCreated.setText(comment.getCreated());
            holder.mCommentText.setText(comment.getText());
            holder.mArtistTitle.setVisibility(View.GONE);
            return convertView;
        }

        class ViewHolder {
            TextView mUserId;

            TextView mCreated;

            TextView mCommentText;

            TextView mArtistTitle;
        }

    }

}
