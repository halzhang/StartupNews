/**
 * Copyright (C) 2013 HalZhang
 */

package com.halzhang.android.apps.startupnews.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.halzhang.android.apps.startupnews.R;
import com.halzhang.android.apps.startupnews.analytics.Tracker;
import com.halzhang.android.apps.startupnews.presenter.CommentsListContract;
import com.halzhang.android.apps.startupnews.ui.DiscussActivity;
import com.halzhang.android.startupnews.data.entity.SNComment;
import com.halzhang.android.startupnews.data.entity.SNComments;

import java.util.ArrayList;

/**
 * StartupNews
 * <p>
 * 评论
 * </p>
 *
 * @author <a href="http://weibo.com/halzhang">Hal</a>
 * @version Mar 7, 2013
 */
public class CommentsListFragment extends SwipeRefreshRecyclerFragment implements CommentsListContract.View {

    private static final String LOG_TAG = CommentsListFragment.class.getSimpleName();

    // private ArrayList<SNComment> mComments = new ArrayList<SNComment>(24);

    private CommentsListContract.Presenter mPresenter;

    private CommentsAdapter mAdapter;


    private SNComments mSnComments = new SNComments();

    private static final String NEWCOMMENTS_URL_PATH = "/newcomments";

    public CommentsListFragment() {
    }

    public static CommentsListFragment newInstance() {
        return new CommentsListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new CommentsAdapter();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mRecyclerView.setAdapter(mAdapter);
        if (mAdapter.isEmpty()) {
            mPresenter.getComments(getString(R.string.host, NEWCOMMENTS_URL_PATH));
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
    protected void onRefreshData() {
        super.onRefreshData();
        Tracker.getInstance().sendEvent("ui_action", "pull_down_list_view_refresh",
                "comments_list_fragment_pull_down_list_view_refresh", 0L);
        mPresenter.getComments(getString(R.string.host, NEWCOMMENTS_URL_PATH));

    }

    @Override
    protected void onLoadMore() {
        super.onLoadMore();
        Tracker.getInstance().sendEvent("ui_action", "pull_up_list_view_refresh",
                "comments_list_fragment_pull_up_list_view_refresh", 0L);
        mPresenter.getMoreComments();
    }

    @Override
    public void onSuccess(ArrayList<SNComment> snComments) {
        onRefreshComplete();
        mSnComments.setSnComments(snComments);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onFailure(Throwable e) {
        Log.e(LOG_TAG, "", e);
        onRefreshComplete();
        Tracker.getInstance().sendException("CommentsTask", e, false);
        Toast.makeText(getActivity(), R.string.error, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onAtEnd() {
        onRefreshComplete();
        Toast.makeText(getActivity(), R.string.tip_last_page, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setPresenter(CommentsListContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }

    private class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.ViewHolder> {

        public final class ViewHolder extends RecyclerView.ViewHolder {

            public final TextView mUserId;

            public final TextView mCreated;

            public final TextView mCommentText;

            public final TextView mArtistTitle;

            public final View mItemView;

            public ViewHolder(View itemView) {
                super(itemView);
                mItemView = itemView;
                mUserId = (TextView) itemView.findViewById(R.id.comment_item_user_id);
                mCreated = (TextView) itemView.findViewById(R.id.comment_item_created);
                mCommentText = (TextView) itemView.findViewById(R.id.comment_item_text);
                mArtistTitle = (TextView) itemView.findViewById(R.id.comment_item_artist_titile);
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(getActivity()).inflate(R.layout.comment_list_item, null));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            SNComment comment = mSnComments.getSnComments().get(position);
            holder.mUserId.setText(comment.getUser().getId());
            holder.mCreated.setText(comment.getCreated());
            holder.mCommentText.setText(comment.getText());
            holder.mArtistTitle.setText(getString(R.string.comment_artist_title, comment.getArtistTitle()));
            holder.mItemView.setTag(position);
            holder.mItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Tracker.getInstance().sendEvent("ui_action", "list_item_click", "comments_list_fragment_list_item_click", 0L);
                    int position = (int) v.getTag();
                    SNComment comment = mSnComments.getSnComments().get(position);
                    Intent intent = new Intent(getActivity(), DiscussActivity.class);
                    intent.putExtra(DiscussActivity.ARG_DISCUSS_URL, comment.getDiscussURL());
                    startActivity(intent);
                }
            });
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemCount() {
            return mSnComments.size();
        }

        public boolean isEmpty() {
            return getItemCount() == 0;
        }
    }

    @Override
    public int getViewLayout() {
        return R.layout.refresh_recycler_view_layout;
    }

}
