
package com.halzhang.android.apps.startupnews.ui;


import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.customtabs.CustomTabsIntent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.halzhang.android.apps.startupnews.MyApplication;
import com.halzhang.android.apps.startupnews.R;
import com.halzhang.android.apps.startupnews.SnApiComponent;
import com.halzhang.android.apps.startupnews.analytics.Tracker;
import com.halzhang.android.apps.startupnews.presenter.CommentsListPresenter;
import com.halzhang.android.apps.startupnews.presenter.CommentsListPresenterModule;
import com.halzhang.android.apps.startupnews.presenter.DaggerMainComponent;
import com.halzhang.android.apps.startupnews.presenter.MainActivityContract;
import com.halzhang.android.apps.startupnews.presenter.MainActivityPresenter;
import com.halzhang.android.apps.startupnews.presenter.MainActivityPresenterModule;
import com.halzhang.android.apps.startupnews.ui.fragment.CommentsListFragment;
import com.halzhang.android.apps.startupnews.ui.fragment.NewsListFragment;
import com.halzhang.android.apps.startupnews.ui.fragment.NewsListFragment.OnNewsSelectedListener;
import com.halzhang.android.apps.startupnews.ui.phone.BrowseActivity;
import com.halzhang.android.apps.startupnews.ui.tablet.BrowseFragment;
import com.halzhang.android.apps.startupnews.ui.tablet.DiscussFragment;
import com.halzhang.android.apps.startupnews.ui.tablet.DiscussFragment.OnMenuSelectedListener;
import com.halzhang.android.apps.startupnews.utils.ActivityUtils;
import com.halzhang.android.apps.startupnews.utils.AppUtils;
import com.halzhang.android.apps.startupnews.utils.CustomTabsActivityHelper;
import com.halzhang.android.common.CDLog;
import com.halzhang.android.common.CDToast;
import com.halzhang.android.startupnews.data.entity.SNNew;
import com.halzhang.android.startupnews.data.entity.Status;
import com.halzhang.android.startupnews.data.utils.SessionManager;

import javax.inject.Inject;

/**
 * 主页
 *
 * @author Hal
 */
public class MainActivity extends BaseFragmentActivity implements OnNewsSelectedListener, OnMenuSelectedListener, MainActivityContract.View {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private static final String TAG_NEWS = "tag_news";

    private static final String TAG_NEWEST = "tag_newest";

    private static final String TAG_COMMENT = "tag_comment";

    private static final String TAG_BROWSE = "tag_browse";

    private static final String TAG_DISCUSS = "tag_discuss";

    private Intent mFeedbackEmailIntent;

    private SNNew mSnNew;
    private CustomTabsActivityHelper mHelper;

    private NewsListFragment mHotNewsListFragment;
    private NewsListFragment mNewsListFragment;
    private CommentsListFragment mCommentsListFragment;

    @Inject
    CommentsListPresenter mCommentsListPresenter;

    @Inject
    MainActivityPresenter mMainActivityPresenter;

    @Inject
    SessionManager mSessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        CDLog.i(LOG_TAG, "MainActivity create!");
        super.onCreate(savedInstanceState);
        if (isFinishing()) {
            return;
        }
        setContentView(R.layout.activity_main);
        setupViews();
        mFeedbackEmailIntent = createEmailIntent();
        mHelper = new CustomTabsActivityHelper();

        SnApiComponent snApiComponent = ((MyApplication) getApplication()).getSnApiComponent();
        DaggerMainComponent.builder().snApiComponent(snApiComponent)
                .commentsListPresenterModule(new CommentsListPresenterModule(mCommentsListFragment))
                .mainActivityPresenterModule(new MainActivityPresenterModule(this))
                .build().inject(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        mHelper.bindService(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mHelper.unBindService(this);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    private void setupViews() {

        mHotNewsListFragment = NewsListFragment.newInstance(getString(R.string.host, "/news"));
        mNewsListFragment = NewsListFragment.newInstance(getString(R.string.host, "/newest"));
        mCommentsListFragment = CommentsListFragment.newInstance();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(false);
        }
        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        if (viewPager != null) {
            SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
            viewPager.setAdapter(sectionsPagerAdapter);
            TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
            tabLayout.setupWithViewPager(viewPager);
        }
    }

    private Intent createEmailIntent() {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{
                "ghanguo@gmail.com"
        });
        StringBuilder builder = new StringBuilder();
        builder.append(getString(R.string.app_name)).append(" v")
                .append(AppUtils.getVersionName(getApplicationContext()))
                .append(getString(R.string.feedback));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, builder.toString());
        emailIntent.setType("message/rfc822");
        return emailIntent;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.removeItem(R.id.menu_login);
        menu.removeItem(R.id.menu_logout);
        if (mSessionManager.isValid()) {
            menu.add(Menu.NONE, R.id.menu_logout, Menu.NONE, R.string.menu_logout);
        } else {
            menu.add(Menu.NONE, R.id.menu_login, Menu.NONE, R.string.menu_login);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_settings:
                Tracker.getInstance().sendEvent("ui_action", "options_item_selected",
                        "mainactivity_menu_settings", 0L);
                startActivity(new Intent(this, AboutActivity.class));
                return true;
            case R.id.menu_feedback:
                Tracker.getInstance().sendEvent("ui_action", "options_item_selected",
                        "mainactivity_menu_feedback", 0L);
                if (ActivityUtils.isIntentAvailable(getApplicationContext(), mFeedbackEmailIntent)) {
                    startActivity(mFeedbackEmailIntent);
                } else {
                    Toast.makeText(getApplicationContext(), R.string.error_noemailapp,
                            Toast.LENGTH_LONG).show();
                }
                return true;
            case R.id.menu_login: {
                Tracker.getInstance().sendEvent("ui_action", "options_item_selected",
                        "mainactivity_menu_login", 0L);
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
            }
            return true;
            case R.id.menu_logout:
                Tracker.getInstance().sendEvent("ui_action", "options_item_selected",
                        "mainactivity_menu_logout", 0L);
                mSessionManager.clear();
                CDToast.showToast(this, R.string.tip_logout_success);
                mMainActivityPresenter.logout();
                return true;
            case R.id.menu_show_comment:
                showDiscussFragment();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showDiscussFragment() {
        showDiscussFragment(mSnNew, null);
    }

    private void showDiscussFragment(SNNew snNew, String discussUrl) {
        Bundle args = new Bundle();
        if (snNew != null) {
            args.putParcelable(DiscussActivity.ARG_SNNEW, snNew);
            args.putString(DiscussActivity.ARG_DISCUSS_URL, mSnNew.getDiscussURL());
        } else {
            args.putString(DiscussActivity.ARG_DISCUSS_URL, discussUrl);
        }
        DiscussFragment fragment = new DiscussFragment();
        fragment.setArguments(args);
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                .replace(R.id.fragment_container, fragment, TAG_DISCUSS).commit();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onLogoutResult(boolean result) {
        CDToast.showToast(getApplicationContext(), result ? R.string.tip_logout_success
                : R.string.tip_logout_failure);
    }

    @Override
    public void onUpVoteFailure(Throwable e) {
        Tracker.getInstance().sendException("up vote error!", e, false);
        CDToast.showToast(this, getString(R.string.tip_vote_failure));
    }

    @Override
    public void onUpVoteSuccess(Status status) {
        switch (status.code) {
            case Status.CODE_COOKIE_VALID:
                startActivity(new Intent(this, LoginActivity.class));
                CDToast.showToast(this, R.string.tip_cookie_invalid);
                break;
            case Status.CODE_REPEAT:
                CDToast.showToast(this, getString(R.string.tip_vote_duplicate));
                break;
            case Status.CODE_SUCCESS:
                CDToast.showToast(this, R.string.tip_vote_success);
                break;
            default:
                break;
        }
    }

    /* no-op */
    @Override
    public void setPresenter(MainActivityContract.Presenter presenter) {

    }

    @Override
    public boolean isActive() {
        return !isFinishing();
    }

    private class SectionsPagerAdapter extends FragmentPagerAdapter {

        private String[] titles;

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
            titles = getResources().getStringArray(R.array.section_titles);
        }

        @Override
        public Fragment getItem(int arg0) {
            switch (arg0) {
                case 0:
                    return mHotNewsListFragment;
                case 1:
                    return mNewsListFragment;
                case 2:
                    return mCommentsListFragment;
                default:
                    throw new IllegalArgumentException();
            }
        }

        @Override
        public int getCount() {
            return titles.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }

        @Override
        public long getItemId(int position) {
            return super.getItemId(position);
        }

    }

    @Override
    public void onNewsSelected(int position, final SNNew snNew) {
        mSnNew = snNew;
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder(mHelper.getSession());
        //设置 toolbar 颜色
        builder.setToolbarColor(0XFF33B5E5);
        CustomTabsIntent customTabsIntent = builder.build();
        mHelper.launchUrl(this, snNew.getUrl(), customTabsIntent, new CustomTabsActivityHelper.OnCustomTabsInvalidListener() {
            @Override
            public void onInvalid(String url) {
                ActivityUtils.openArticle(MainActivity.this, snNew);
            }
        });
    }

    private void showBrowseFragment(SNNew snNew) {
        BrowseFragment browseFragment = (BrowseFragment) getSupportFragmentManager()
                .findFragmentByTag(TAG_BROWSE);
        if (browseFragment != null) {
            browseFragment.setTitle(snNew.getTitle());
            browseFragment.load(snNew.getUrl());
        } else {
            BrowseFragment fragment = new BrowseFragment();
            Bundle bundle = new Bundle();
            bundle.putString(BrowseActivity.EXTRA_URL, snNew.getUrl());
            bundle.putString(BrowseActivity.EXTRA_TITLE, snNew.getTitle());
            fragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right)
                    .replace(R.id.fragment_container, fragment, TAG_BROWSE).commit();
        }
    }


    @Override
    public void onShowArticleSelected(SNNew snNew) {
        mSnNew = snNew;
        showBrowseFragment(snNew);
    }

    @Override
    public void onUpVoteSelected(String postId) {
        mMainActivityPresenter.upVote(postId);
    }

}
