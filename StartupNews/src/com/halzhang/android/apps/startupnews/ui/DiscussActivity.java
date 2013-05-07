/**
 * Copyright (C) 2013 HalZhang
 */

package com.halzhang.android.apps.startupnews.ui;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.google.analytics.tracking.android.EasyTracker;
import com.halzhang.android.apps.startupnews.Constants.IntentAction;
import com.halzhang.android.apps.startupnews.MyApplication;
import com.halzhang.android.apps.startupnews.R;
import com.halzhang.android.apps.startupnews.entity.SNComment;
import com.halzhang.android.apps.startupnews.entity.SNDiscuss;
import com.halzhang.android.apps.startupnews.entity.SNNew;
import com.halzhang.android.apps.startupnews.parser.SNDiscussParser;
import com.halzhang.android.apps.startupnews.snkit.SNApi;
import com.halzhang.android.apps.startupnews.snkit.SessionManager;
import com.halzhang.android.apps.startupnews.utils.ActivityUtils;
import com.halzhang.android.apps.startupnews.utils.JsoupFactory;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.jsoup.Connection;
import org.jsoup.nodes.Document;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
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
public class DiscussActivity extends BaseFragmentActivity implements OnItemClickListener {

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

    private EditText mCommentEdit;

    private ImageButton mSendBtn;

    private JsoupFactory mJsoupFactory;

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(android.content.Context context, Intent intent) {
            final String action = intent.getAction();
            if (IntentAction.ACTION_LOGIN.equals(action)) {
                String user = intent.getStringExtra(IntentAction.EXTRA_LOGIN_USER);
                if (!TextUtils.isEmpty(user)) {
                    loadData();
                }
            }
        };
    };

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        mJsoupFactory = JsoupFactory.getInstance(getApplicationContext());
        setTheme(R.style.Theme_Sherlock_Light_DarkActionBar);
        setContentView(R.layout.activity_discuss);
        mListView = (ListView) findViewById(android.R.id.list);
        mListView.setOnItemClickListener(this);
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

        mSendBtn = (ImageButton) findViewById(R.id.discuss_comment_send_btn);
        mSendBtn.setEnabled(false);
        mSendBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                SNApi api = new SNApi(getApplicationContext());
                api.comment(getApplicationContext(), mSnDiscuss.getFnid(), mCommentEdit.getText()
                        .toString(), new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, String content) {
                        if (SessionManager.getInstance(getApplicationContext()).isValid()) {
                            mCommentEdit.setText(null);
                            Toast.makeText(getApplicationContext(), "评论成功!", Toast.LENGTH_SHORT)
                                    .show();
                            loadData();
                        } else {
                            Intent intent = new Intent(DiscussActivity.this, LoginActivity.class);
                            intent.putExtra(LoginActivity.EXTRA_LOGIN_PAGER_URL, MyApplication
                                    .instance().getLogInOutURL());
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onFailure(Throwable error, String content) {
                        Toast.makeText(getApplicationContext(), "评论失败:" + error.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        mCommentEdit = (EditText) findViewById(R.id.discuss_comment_edit);
        mCommentEdit.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mSendBtn.setEnabled(s.length() > 0);
            }
        });
        mListView.addHeaderView(view);
        mListView.setAdapter(mAdapter);
        wrapHeaderView(snNew);

        IntentFilter filter = new IntentFilter(IntentAction.ACTION_LOGIN);
        registerReceiver(mReceiver, filter);
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
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
        if (mDiscussTask != null) {
            mDiscussTask.cancel(true);
            mDiscussTask = null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        mOptionsMenu = menu;
        getSupportMenuInflater().inflate(R.menu.activity_discuss, menu);
        return true;
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
                EasyTracker.getTracker().sendEvent("ui_action", "options_item_selected",
                        "discussactivity_menu_refresh", 0L);
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
                Connection conn = mJsoupFactory.newJsoupConnection(params[0]);
                if (conn == null) {
                    return false;
                }
                Document doc = conn.get();
                SNDiscussParser parser = new SNDiscussParser();
                SNDiscuss discuss = parser.parseDocument(doc);
                mSnDiscuss.clearComments();
                mSnDiscuss.copy(discuss);
            } catch (Exception e) {
                // Log.e(LOG_TAG, "", e);
                EasyTracker.getTracker().sendException("DiscussTask", e, false);
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position == 0) {
            EasyTracker.getTracker().sendEvent("ui_action", "list_item_click",
                    "discuss_activity_list_header_click", 0L);
            // 查看文章
            ActivityUtils.openArticle(this, mSnDiscuss.getSnNew());
        }
    }

}
