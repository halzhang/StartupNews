package com.halzhang.android.apps.startupnews.ui.tablet;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.Html;
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

import com.halzhang.android.apps.startupnews.R;
import com.halzhang.android.apps.startupnews.analytics.Tracker;
import com.halzhang.android.apps.startupnews.presenter.DiscussContract;
import com.halzhang.android.apps.startupnews.ui.LoginActivity;
import com.halzhang.android.apps.startupnews.utils.ActivityUtils;
import com.halzhang.android.common.CDToast;
import com.halzhang.android.startupnews.data.entity.SNComment;
import com.halzhang.android.startupnews.data.entity.SNDiscuss;
import com.halzhang.android.startupnews.data.entity.SNNew;
import com.halzhang.android.startupnews.data.entity.Status;
import com.halzhang.android.startupnews.data.utils.SessionManager;

import javax.inject.Inject;

/**
 * 查看评论
 * Created by Hal on 13-5-26.
 */
public class DiscussFragment extends Fragment implements OnItemClickListener, DiscussContract.View {

    public static final String ARG_DISCUSS_URL = "discuss_url";
    public static final String ARG_SNNEW = "snnew";

    private static final String LOG_TAG = DiscussFragment.class.getSimpleName();


    private DiscussContract.Presenter mPresenter;

    @Override
    public void setPresenter(DiscussContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }

    @Override
    public void onGetDiscuss(SNDiscuss snDiscuss) {
        mSnDiscuss.clearComments();
        mSnDiscuss.copy(snDiscuss);
        setRefreshActionButtonState(false);
        wrapHeaderView(mSnDiscuss.getSnNew());
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onGetDiscussFailure(Throwable e) {
        Tracker.getInstance().sendException("DiscussTask", e, false);
        Toast.makeText(getActivity(), R.string.error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCommentSuccess(Status status) {

        switch (status.code){
            case Status.CODE_COOKIE_VALID:
                CDToast.showToast(getActivity(), R.string.tip_cookie_invalid);
                startActivity(new Intent(getActivity(), LoginActivity.class));
                Tracker.getInstance().sendEvent("ui_action_feedback",
                        "comment_feedback", getString(R.string.tip_cookie_invalid),
                        0L);
                break;
            case Status.CODE_SUCCESS:
                mCommentEdit.setText(null);
                CDToast.showToast(getActivity(), R.string.tip_comment_success);
                Tracker.getInstance().sendEvent("ui_action_feedback",
                        "comment_feedback", "success", 0L);
                loadData();
                break;
            default:
                Tracker.getInstance().sendEvent("ui_action_feedback",
                        "comment_feedback", status.message, 0L);
                CDToast.showToast(getActivity(), R.string.tip_comment_failure);
                break;
        }
    }

    @Override
    public void onCommentFailure(Throwable e) {
        CDToast.showToast(getActivity(), R.string.tip_comment_failure);
        Tracker.getInstance().sendException("comment error!", e, false);
    }

    @Override
    public void onSessionExpired() {
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
    }

    public interface OnMenuSelectedListener {
        public void onShowArticleSelected(SNNew snNew);

        public void onUpVoteSelected(String postId);
    }

    private SNDiscuss mSnDiscuss;

    private ListView mListView;

    private String mDiscussURL;

    private DiscussCommentAdapter mAdapter;

    private TextView mTitle;

    private TextView mSubTitle;

    private TextView mText;

    private Menu mOptionsMenu;

    private EditText mCommentEdit;

    private ImageButton mSendBtn;

    private OnMenuSelectedListener mListener;

    @Inject
    SessionManager mSessionManager;

    public DiscussFragment() {

    }

    public static DiscussFragment newInstance(String discussURL, SNNew snNew) {
        DiscussFragment fragment = new DiscussFragment();
        Bundle args = new Bundle();
        args.putString(ARG_DISCUSS_URL, discussURL);
        args.putParcelable(ARG_SNNEW, snNew);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
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
        if (args != null && args.containsKey(ARG_DISCUSS_URL)) {
            mDiscussURL = args.getString(ARG_DISCUSS_URL);
        } else {
            throw new IllegalArgumentException("Discuss URL is required!");
        }
        if (args.containsKey(ARG_SNNEW)) {
            snNew = args.getParcelable(ARG_SNNEW);
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
            mPresenter.comment(mCommentEdit.getText().toString());
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
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
        mPresenter.getDiscuss(mDiscussURL);
        setRefreshActionButtonState(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_refresh:
                Tracker.getInstance().sendEvent("ui_action", "options_item_selected", "discussactivity_menu_refresh", 0L);
                loadData();
                return true;
            case R.id.menu_show_article:
                if (mListener != null) {
                    mListener.onShowArticleSelected(mSnDiscuss.getSnNew());
                }
                return true;
            case R.id.menu_up_vote:
                if (mListener != null) {
                    mListener.onUpVoteSelected(mSnDiscuss.getSnNew().getPostID());
                }
            default:
                return super.onOptionsItemSelected(item);
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
