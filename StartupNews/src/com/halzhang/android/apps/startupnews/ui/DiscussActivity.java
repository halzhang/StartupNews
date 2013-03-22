/**
 * Copyright (C) 2013 HalZhang
 */

package com.halzhang.android.apps.startupnews.ui;

import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;
import com.halzhang.android.apps.startupnews.R;
import com.halzhang.android.apps.startupnews.entity.SNComment;
import com.halzhang.android.apps.startupnews.entity.SNDiscuss;
import com.halzhang.android.apps.startupnews.entity.SNNew;
import com.halzhang.android.apps.startupnews.parser.SNDiscussParser;
import com.halzhang.android.apps.startupnews.utils.ActivityUtils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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

    private TextView mTitle;

    private TextView mSubTitle;

    private TextView mText;

    private Menu mOptionsMenu;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setTheme(R.style.Theme_Sherlock_Light_DarkActionBar);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_discuss);
        mListView = getListView();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mSnDiscuss = new SNDiscuss();
        SNNew snNew = (SNNew) getIntent().getSerializableExtra(ARG_SNNEW);
        mDiscussURL = getIntent().getStringExtra(ARG_DISCUSS_URL);
        mSnDiscuss.setSnNew(snNew);
        mAdapter = new DiscussCommentAdapter();
        View view = getLayoutInflater().inflate(R.layout.discuss_header_view, null);
        mTitle = (TextView) view.findViewById(R.id.discuss_news_title);
        mSubTitle = (TextView) view.findViewById(R.id.discuss_news_subtitle);
        mText = (TextView) view.findViewById(R.id.discuss_text);
        mListView.addHeaderView(view);
        mListView.setAdapter(mAdapter);
        wrapHeaderView(snNew);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
        invalidateOptionsMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        mOptionsMenu = menu;
        getSupportMenuInflater().inflate(R.menu.activity_discuss, menu);
        return true;
    }
    
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        if(position == 0){
            //查看文章
            ActivityUtils.openArticle(this, mSnDiscuss.getSnNew());
        }
    }

    private void wrapHeaderView(SNNew snNew) {
        if (snNew != null) {
            mTitle.setText(snNew.getTitle());
            mSubTitle.setText(Html.fromHtml(snNew.getSubText()));
            if (snNew.isDiscuss()) {
                mText.setVisibility(View.VISIBLE);
                mText.setText(snNew.getText());
            } else {
                mText.setVisibility(View.GONE);
            }
        }
    }

    public void setRefreshActionButtonState(boolean refreshing) {
        if (mOptionsMenu == null) {
            Log.i(LOG_TAG, "Option menu is null!");
            return;
        }

        final MenuItem refreshItem = mOptionsMenu.findItem(R.id.menu_refresh);
        if (refreshItem != null) {
            if (refreshing) {
                refreshItem.setActionView(R.layout.actionbar_indeterminate_progress);
            } else {
                refreshItem.setActionView(null);
            }
        }
    }

    private void loadData() {
        if (mDiscussTask != null) {
            return;
        }
        setRefreshActionButtonState(true);
        mDiscussTask = new DiscussTask();
        mDiscussTask.execute(mDiscussURL);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.menu_refresh:
                loadData();
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private class DiscussTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            try {
                Document doc = Jsoup.connect(params[0]).get();
                SNDiscussParser parser = new SNDiscussParser();
                SNDiscuss discuss = parser.parseDocument(doc);
                mSnDiscuss.clearComments();
                mSnDiscuss.copy(discuss);
            } catch (Exception e) {
                Log.e(LOG_TAG, "", e);
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            setRefreshActionButtonState(false);
            if (result) {
                wrapHeaderView(mSnDiscuss.getSnNew());
                mAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(getApplicationContext(), R.string.error, Toast.LENGTH_SHORT).show();
            }
            // mListView.getEmptyView().setVisibility(View.GONE);
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
                        R.layout.discuss_comment_item, null);
                holder.mUserId = (TextView) convertView
                        .findViewById(R.id.discuss_comment_item_user_id);
                holder.mCreated = (TextView) convertView
                        .findViewById(R.id.discuss_comment_item_created);
                holder.mCommentText = (TextView) convertView
                        .findViewById(R.id.discuss_comment_item_text);
                holder.mArtistTitle = (TextView) convertView
                        .findViewById(R.id.discuss_comment_item_artist_titile);
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
