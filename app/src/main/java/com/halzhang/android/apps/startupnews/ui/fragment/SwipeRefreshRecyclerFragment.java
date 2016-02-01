package com.halzhang.android.apps.startupnews.ui.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.halzhang.android.apps.startupnews.R;
import com.halzhang.android.apps.startupnews.ui.widgets.CardViewDividerDecoration;
import com.halzhang.android.apps.startupnews.ui.widgets.DividerDecoration;

public abstract class SwipeRefreshRecyclerFragment extends Fragment {

    private static final String LOG_TAG = SwipeRefreshRecyclerFragment.class.getSimpleName();

    protected SwipeRefreshLayout mSwipeRefreshLayout;
    protected RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;


    public SwipeRefreshRecyclerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected abstract int getViewLayout();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(getViewLayout(), null);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        if (mRecyclerView == null || mSwipeRefreshLayout == null) {
            throw new IllegalArgumentException("mush be have RecyclerView and SwipeRefreshLayout");
        }
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light, android.R.color.holo_blue_light);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                onRefreshData();
            }
        });
        mLinearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                boolean enable = false;
                int visibleItemCount = mRecyclerView.getChildCount();
                int itemCount = mLinearLayoutManager.getItemCount();
                int firstVisibleItemPosition = mLinearLayoutManager.findFirstVisibleItemPosition();
                int lastVisibleItemPosition = mLinearLayoutManager.findLastVisibleItemPosition();
//                if (visibleItemCount > 0) {
//                    boolean firstItemVisible = firstVisibleItemPosition == 0;
//                    boolean topOfFirstItemVisible = mLinearLayoutManager.getChildAt(0).getTop() == 0;
//                    enable = firstItemVisible && topOfFirstItemVisible;
//                    Log.d(LOG_TAG, "SwipeRefreshLayout enable: " + enable);
//                }
//                mSwipeRefreshLayout.setEnabled(enable);
                if (lastVisibleItemPosition == itemCount - 1) {
                    onLoadMore();
                }
            }
        });
        mRecyclerView.addItemDecoration(new CardViewDividerDecoration(getActivity()));
        return view;
    }

    /**
     * 刷新数据
     */
    protected void onRefreshData() {
    }

    /**
     * 加载更多
     */
    protected void onLoadMore() {
    }

    protected void onRefreshComplete() {
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


}
