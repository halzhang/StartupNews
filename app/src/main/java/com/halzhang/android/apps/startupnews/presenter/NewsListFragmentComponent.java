package com.halzhang.android.apps.startupnews.presenter;

import com.halzhang.android.apps.startupnews.SnApiComponent;
import com.halzhang.android.apps.startupnews.ui.fragment.NewsListFragment;
import com.halzhang.android.apps.startupnews.utils.FragmentScoped;

import dagger.Component;

/**
 * Created by Hal on 16/8/14.
 */
@FragmentScoped
@Component(dependencies = SnApiComponent.class, modules = NewsListPresenterModule.class)
public interface NewsListFragmentComponent {

    void inject(NewsListFragment newsListFragment);

}
