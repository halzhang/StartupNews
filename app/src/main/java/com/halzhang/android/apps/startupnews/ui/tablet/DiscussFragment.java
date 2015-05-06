/*
 * Copyright (C) 2013  HalZhang
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.halzhang.android.apps.startupnews.ui.tablet;

import com.halzhang.android.apps.startupnews.R;
import com.halzhang.android.apps.startupnews.analytics.Tracker;
import com.halzhang.android.apps.startupnews.entity.SNComment;
import com.halzhang.android.apps.startupnews.entity.SNDiscuss;
import com.halzhang.android.apps.startupnews.entity.SNNew;
import com.halzhang.android.apps.startupnews.parser.SNDiscussParser;
import com.halzhang.android.apps.startupnews.snkit.JsoupFactory;
import com.halzhang.android.apps.startupnews.snkit.SNApi;
import com.halzhang.android.apps.startupnews.snkit.SessionManager;
import com.halzhang.android.apps.startupnews.ui.DiscussActivity;
import com.halzhang.android.apps.startupnews.ui.LoginActivity;
import com.halzhang.android.apps.startupnews.utils.ActivityUtils;
import com.halzhang.android.common.CDLog;
import com.halzhang.android.common.CDToast;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;
import org.jsoup.Connection;
import org.jsoup.nodes.Document;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuCompat;
import android.support.v4.view.MenuItemCompat;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
 * Created by Hal on 13-5-26.
 */
public class DiscussFragment extends Fragment implements OnItemClickListener {

    private static final String LOG_TAG = DiscussFragment.class.getSimpleName();

    public interface OnMenuSelectedListener {
        public void onShowArticleSelected(SNNew snNew);

        public void onUpVoteSelected(String postId);
    }

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

    private OnMenuSelectedListener mListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mJsoupFactory = JsoupFactory.getInstance(activity.getApplicationContext());
        if (activity instanceof OnMenuSelectedListener) {
            mListener = (OnMenuSelectedListener) activity;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_discuss, null);
        mListView = (ListView) view.findViewById(android.R.id.list);
        mListView.setOnItemClickListener(this);
        mSnDiscuss = new SNDiscuss();
        Bundle args = getArguments();
        SNNew snNew = null;
        if (args != null && args.containsKey(DiscussActivity.ARG_DISCUSS_URL)) {
            mDiscussURL = args.getString(DiscussActivity.ARG_DISCUSS_URL);
        } else {
            throw new IllegalArgumentException("Discuss URL is required!");
        }
        if (args.containsKey(DiscussActivity.ARG_SNNEW)) {
            snNew = (SNNew) args.getSerializable(DiscussActivity.ARG_SNNEW);
            mSnDiscuss.setSnNew(snNew);
        }
        mAdapter = new DiscussCommentAdapter();
        View headerView = inflater.inflate(R.layout.discuss_header_view, null);
        mTitle = (TextView) headerView.findViewById(R.id.discuss_news_title);
        mSubTitle = (TextView) headerView.findViewById(R.id.discuss_news_subtitle);
        mText = (TextView) headerView.findViewById(R.id.discuss_text);

        mSendBtn = (ImageButton) view.findViewById(R.id.discuss_comment_send_btn);
        mSendBtn.setEnabled(false);
        mSendBtn.setOnClickListener(mSendBtnClickListener);
        mCommentEdit = (EditText) view.findViewById(R.id.discuss_comment_edit);
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
        mListView.addHeaderView(headerView);
        mListView.setAdapter(mAdapter);
        wrapHeaderView(snNew);
        loadData();
        return view;
    }

    private OnClickListener mSendBtnClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            Tracker.getInstance().sendEvent("ui_action", "view_clicked",
                    "discussactivity_button_comment", 0L);
            if (!SessionManager.getInstance(getActivity()).isValid()) {
                // 未登陆
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                return;
            }
            SNApi api = new SNApi(getActivity());
            api.comment(getActivity(), mSnDiscuss.getFnid(), mCommentEdit.getText().toString(),
                    new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, String content) {
                            mCommentEdit.setText(null);
                            CDToast.showToast(getActivity(), R.string.tip_comment_success);
                            Tracker.getInstance().sendEvent("ui_action_feedback",
                                    "comment_feedback", "success", 0L);
                            loadData();
                        }

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, String content) {
                            String refreerLocation = null;
                            for (Header header : headers) {
                                CDLog.w(LOG_TAG, header.getName() + " : " + header.getValue());
                                if (AsyncHttpClient.REFREER_LOCATION.equals(header.getName())) {
                                    refreerLocation = header.getValue();
                                    break;
                                }
                            }
                            if (TextUtils.isEmpty(refreerLocation)
                                    || refreerLocation.contains("fnid")) {
                                /*
                                 * Location:fnid=xxxxx Cookie失效，重新登陆
                                 */
                                CDToast.showToast(getActivity(), R.string.tip_cookie_invalid);
                                startActivity(new Intent(getActivity(), LoginActivity.class));
                                Tracker.getInstance().sendEvent("ui_action_feedback",
                                        "comment_feedback", getString(R.string.tip_cookie_invalid),
                                        0L);
                            } else if (refreerLocation.contains("item")) {
                                onSuccess(statusCode, content);
                            } else {
                                Tracker.getInstance().sendEvent("ui_action_feedback",
                                        "comment_feedback", content, 0L);
                                CDToast.showToast(getActivity(), R.string.tip_comment_failure);
                            }
                        }

                        @Override
                        public void onFailure(Throwable error, String content) {
                            CDToast.showToast(getActivity(), R.string.tip_comment_failure);
                            Tracker.getInstance().sendException("comment error:" + content,
                                    error, false);
                        }
                    });

        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mDiscussTask != null) {
            mDiscussTask.cancel(true);
            mDiscussTask = null;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        mOptionsMenu = menu;
        inflater.inflate(R.menu.fragment_discuss, menu);
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

    public void loadData() {
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
            case R.id.menu_refresh:
                Tracker.getInstance().sendEvent("ui_action", "options_item_selected",
                        "discussactivity_menu_refresh", 0L);
                loadData();
                return true;
            case R.id.menu_show_article:
                if (mListener != null) {
                    mListener.onShowArticleSelected(mSnDiscuss.getSnNew());
                }
                return true;
            case R.id.menu_up_vote:
                if(mListener != null){
                    mListener.onUpVoteSelected(mSnDiscuss.getSnNew().getPostID());
                }
            default:
                return super.onOptionsItemSelected(item);
        }

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
                Tracker.getInstance().sendException("DiscussTask", e, false);
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
                Toast.makeText(getActivity(), R.string.error, Toast.LENGTH_SHORT).show();
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
                convertView = LayoutInflater.from(getActivity()).inflate(
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
        if (position == 0 && mListener == null) {
            Tracker.getInstance().sendEvent("ui_action", "list_item_click",
                    "discuss_activity_list_header_click", 0L);
            // 查看文章
            ActivityUtils.openArticle(getActivity(), mSnDiscuss.getSnNew());
        }
    }

}
