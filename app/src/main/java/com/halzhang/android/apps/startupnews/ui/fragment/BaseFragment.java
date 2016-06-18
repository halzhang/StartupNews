package com.halzhang.android.apps.startupnews.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import rx.subscriptions.CompositeSubscription;

/**
 * Created by Hal on 16/6/18.
 */
public class BaseFragment extends Fragment {

    protected CompositeSubscription mCompositeSubscription;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCompositeSubscription = new CompositeSubscription();
    }

    @Override
    public void onDestroyView() {
        mCompositeSubscription.clear();
        super.onDestroyView();
    }
}
