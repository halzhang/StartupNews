
package com.halzhang.android.apps.startupnews.ui;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBarDrawerToggle;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.Window;
import com.google.analytics.tracking.android.EasyTracker;
import com.halzhang.android.apps.startupnews.R;
import com.halzhang.android.apps.startupnews.entity.SNNew;
import com.halzhang.android.apps.startupnews.parser.BaseHTMLParser;
import com.halzhang.android.apps.startupnews.snkit.JsoupFactory;
import com.halzhang.android.apps.startupnews.snkit.SNApi;
import com.halzhang.android.apps.startupnews.snkit.SessionManager;
import com.halzhang.android.apps.startupnews.ui.NewsListFragment.OnNewsSelectedListener;
import com.halzhang.android.apps.startupnews.ui.phone.BrowseActivity;
import com.halzhang.android.apps.startupnews.ui.tablet.BrowseFragment;
import com.halzhang.android.apps.startupnews.ui.tablet.DiscussFragment;
import com.halzhang.android.apps.startupnews.utils.ActivityUtils;
import com.halzhang.android.apps.startupnews.utils.AppUtils;
import com.halzhang.android.common.CDLog;
import com.halzhang.android.common.CDToast;
import com.slidinglayer.SlidingLayer;
import com.viewpagerindicator.TitlePageIndicator;

import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Toast;

import java.io.IOException;

/**
 * 主页
 * 
 * @author Hal
 */
public class MainActivity extends BaseFragmentActivity implements AdapterView.OnItemClickListener,
        OnNewsSelectedListener, SlidingLayer.OnInteractListener {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private static final String TAG_NEWS = "tag_news";

    private static final String TAG_NEWEST = "tag_newest";

    private static final String TAG_COMMENT = "tag_comment";

    private static final String TAG_BROWSE = "tag_browse";

    private static final String TAG_DISCUSS = "tag_discuss";

    private ViewPager mViewPager;

    private SectionsPagerAdapter mSectionsPagerAdapter;

    private Intent mFeedbackEmailIntent;

    private ListView mDrawerListView;

    private DrawerLayout mDrawerLayout;

    private ActionBarDrawerToggle mDrawerToggle;

    private SNApiHelper mSnApiHelper;

    private SNNew mSnNew;

    private NewsListFragment mNewsListFragment;

    private NewsListFragment mNewestListFragment;

    private CommentsListFragment mCommentsListFragment;

    private SlidingLayer mSlidingLayer;

    @SuppressWarnings("unused")
    private LogoutTask mLogoutTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        CDLog.i(LOG_TAG, "MainAction create!");
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_PROGRESS);
        if (isFinishing()) {
            return;
        }
        setContentView(R.layout.activity_main);
        setupViews();
        mFeedbackEmailIntent = createEmailIntent();
        mSnApiHelper = new SNApiHelper(this);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (mDrawerToggle != null) {
            mDrawerToggle.syncState();
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setupViews() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (mDrawerLayout != null) {
            // 横屏，平板布局
            final ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);

            mSlidingLayer = (SlidingLayer) findViewById(R.id.slidingLayer1);
            mSlidingLayer.setOnInteractListener(this);
            setSlidinglayerWidth();

            mDrawerToggle = new ActionBarDrawerToggle(this, actionBar, mDrawerLayout,
                    R.drawable.ic_drawer, R.string.drawer_opened, R.string.drawer_closed) {

                @Override
                public void onDrawerOpened(View drawerView) {
                    super.onDrawerOpened(drawerView);
                }

                @Override
                public void onDrawerClosed(View drawerView) {
                    super.onDrawerClosed(drawerView);
                }
            };
            mDrawerLayout.setDrawerListener(mDrawerToggle);
            mDrawerListView = (ListView) findViewById(R.id.left_drawer_list);
            mDrawerListView.setOnItemClickListener(this);
            mDrawerListView.setAdapter(new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_activated_1, android.R.id.text1,
                    getResources().getStringArray(R.array.section_titles)));

            mNewsListFragment = new NewsListFragment();
            Bundle args = new Bundle();
            args.putString(NewsListFragment.ARG_URL, getString(R.string.host, "/news"));
            mNewsListFragment.setArguments(args);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_frame_left, mNewsListFragment, TAG_NEWS)
                    .commitAllowingStateLoss();

        }
        mViewPager = (ViewPager) findViewById(R.id.pager);
        if (mViewPager != null) {
            mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
            mViewPager.setAdapter(mSectionsPagerAdapter);
            TitlePageIndicator indicator = (TitlePageIndicator) findViewById(R.id.titles);
            indicator.setViewPager(mViewPager);
        }
    }

    private Intent createEmailIntent() {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] {
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
    public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {
        getSupportMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.removeItem(R.id.menu_login);
        menu.removeItem(R.id.menu_logout);
        if (SessionManager.getInstance(getApplicationContext()).isValid()) {
            menu.add(Menu.NONE, R.id.menu_logout, Menu.NONE, R.string.menu_logout);
        } else {
            menu.add(Menu.NONE, R.id.menu_login, Menu.NONE, R.string.menu_login);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {
        if (mDrawerToggle != null && mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        switch (item.getItemId()) {
            case R.id.menu_settings:
                EasyTracker.getTracker().sendEvent("ui_action", "options_item_selected",
                        "mainactivity_menu_settings", 0L);
                startActivity(new Intent(this, AboutActivity.class));
                return true;
            case R.id.menu_feedback:
                EasyTracker.getTracker().sendEvent("ui_action", "options_item_selected",
                        "mainactivity_menu_feedback", 0L);
                if (ActivityUtils.isIntentAvailable(getApplicationContext(), mFeedbackEmailIntent)) {
                    startActivity(mFeedbackEmailIntent);
                } else {
                    Toast.makeText(getApplicationContext(), R.string.error_noemailapp,
                            Toast.LENGTH_LONG).show();
                }
                return true;
            case R.id.menu_login: {
                EasyTracker.getTracker().sendEvent("ui_action", "options_item_selected",
                        "mainactivity_menu_login", 0L);
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
            }
                return true;
            case R.id.menu_logout:
                EasyTracker.getTracker().sendEvent("ui_action", "options_item_selected",
                        "mainactivity_menu_logout", 0L);
                SessionManager.getInstance(this).clear();
                CDToast.showToast(this, R.string.tip_logout_success);
                // mLogoutTask = new LogoutTask();
                // mLogoutTask.execute((Void) null);
                return true;
            case R.id.menu_screenorientation:
                // TODO for debug
                int ori = getRequestedOrientation();
                if (ori == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                } else if (ori == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                } else {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                }
                return true;
            case R.id.menu_up_vote:
                mSnApiHelper.upVote(mSnNew);
                return true;
            case R.id.menu_show_comment:
                Bundle args = new Bundle();
                args.putSerializable(DiscussActivity.ARG_SNNEW, mSnNew);
                args.putString(DiscussActivity.ARG_DISCUSS_URL, mSnNew.getDiscussURL());
                DiscussFragment fragment = new DiscussFragment();
                fragment.setArguments(args);
                getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                        .replace(R.id.discuss_fragment_container, fragment, TAG_DISCUSS).commit();
                mSlidingLayer.openLayer(true);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (mDrawerToggle != null) {
            mDrawerToggle.onConfigurationChanged(newConfig);
        }
        //横竖屏切换后需要更新SlidingLayer的宽度
        if (mSlidingLayer != null) {
            setSlidinglayerWidth();
        }
        
    }

    private void setSlidinglayerWidth() {
        LayoutParams lp = (LayoutParams) mSlidingLayer.getLayoutParams();
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        lp.width = (int) (screenWidth * 0.5);
        mSlidingLayer.setLayoutParams(lp);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0: {
                Fragment fragment = getSupportFragmentManager().findFragmentByTag(TAG_NEWS);
                if (fragment == null) {
                    if (mNewsListFragment == null) {
                        mNewsListFragment = new NewsListFragment();
                        Bundle args = new Bundle();
                        args.putString(NewsListFragment.ARG_URL, getString(R.string.host, "/news"));
                        mNewsListFragment.setArguments(args);
                    }
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.content_frame_left, mNewsListFragment, TAG_NEWS)
                            .commitAllowingStateLoss();
                } else {
                    getSupportFragmentManager().beginTransaction().attach(fragment);
                }
            }
                break;
            case 1: {
                Fragment fragment = getSupportFragmentManager().findFragmentByTag(TAG_NEWEST);
                if (fragment == null) {
                    if (mNewestListFragment == null) {
                        mNewestListFragment = new NewsListFragment();
                        Bundle args = new Bundle();
                        args.putString(NewsListFragment.ARG_URL,
                                getString(R.string.host, "/newest"));
                        mNewestListFragment.setArguments(args);
                    }
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.content_frame_left, mNewestListFragment, TAG_NEWEST)
                            .commitAllowingStateLoss();
                }
            }
                break;
            case 2: {
                Fragment fragment = getSupportFragmentManager().findFragmentByTag(TAG_COMMENT);
                if (fragment == null) {
                    if (mCommentsListFragment == null) {
                        mCommentsListFragment = new CommentsListFragment();
                    }
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.content_frame_left, mCommentsListFragment, TAG_COMMENT)
                            .commitAllowingStateLoss();
                }
            }
                break;
            default:
                throw new IllegalArgumentException("");
        }
        mDrawerLayout.closeDrawers();
    }

    private class SectionsPagerAdapter extends FragmentPagerAdapter {

        private String[] titles;

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
            titles = getResources().getStringArray(R.array.section_titles);
        }

        @Override
        public Fragment getItem(int arg0) {
            Fragment fragment = null;
            Bundle args = new Bundle();
            switch (arg0) {
                case 0:
                    fragment = new NewsListFragment();
                    args.putString(NewsListFragment.ARG_URL, getString(R.string.host, "/news"));
                    fragment.setArguments(args);
                    break;
                case 1:
                    fragment = new NewsListFragment();
                    args.putString(NewsListFragment.ARG_URL, getString(R.string.host, "/newest"));
                    fragment.setArguments(args);
                    break;
                case 2:
                    fragment = new CommentsListFragment();
                default:
                    break;
            }
            return fragment;
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

    private class LogoutTask extends AsyncTask<Void, Void, Boolean> {

        private ProgressDialog mDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (mDialog != null && mDialog.isShowing()) {
                mDialog.dismiss();
                mDialog = null;
            }
            mDialog = ProgressDialog.show(MainActivity.this, null, getString(R.string.tip_logout));
            mDialog.setCancelable(false);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            String logoutUrl = null;
            JsoupFactory jsoupFactory = JsoupFactory.getInstance(getApplicationContext());
            Connection conn = jsoupFactory.newJsoupConnection(getString(R.string.host, "/news"));
            if (conn != null) {
                try {
                    Document doc = conn.get();
                    Elements elements = doc.select("a:matches(logout)");
                    if (elements.size() > 0) {
                        logoutUrl = BaseHTMLParser.resolveRelativeSNURL(elements.attr("href"));
                    } else {
                        // 用户可能在pc注销了
                        SessionManager.getInstance(getApplicationContext()).clear();
                        return true;
                    }
                } catch (IOException e) {
                    CDLog.w(LOG_TAG, null, e);
                    EasyTracker.getTracker().sendException("User Logout error!", e, false);
                }

            }

            if (TextUtils.isEmpty(logoutUrl)) {
                return false;
            }

            SNApi api = new SNApi(getApplicationContext());
            boolean result = api.logout(logoutUrl);
            if (result) {
                SessionManager.getInstance(getApplicationContext()).clear();
            }
            return result;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            mLogoutTask = null;
            mDialog.dismiss();
            mDialog = null;
            CDToast.showToast(getApplicationContext(), result ? R.string.tip_logout_success
                    : R.string.tip_logout_failure);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            mLogoutTask = null;
        }

    }

    @Override
    public void onNewsSelected(int position, SNNew snNew) {
        // 处理文章被选中，竖屏启动Activity，平板更新右栏
        mSnNew = snNew;
        if (isFragmentContainerExist()) {
            mSlidingLayer.closeLayer(true);
            BrowseFragment browseFragment = (BrowseFragment) getSupportFragmentManager()
                    .findFragmentByTag(TAG_BROWSE);
            if (browseFragment != null) {
                browseFragment.setTitle(mSnNew.getTitle());
                browseFragment.load(snNew.getUrl());
            } else {
                BrowseFragment fragment = new BrowseFragment();
                Bundle bundle = new Bundle();
                bundle.putString(BrowseActivity.EXTRA_URL, mSnNew.getUrl());
                bundle.putString(BrowseActivity.EXTRA_TITLE, mSnNew.getTitle());
                fragment.setArguments(bundle);
                getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right)
                        .replace(R.id.fragment_container, fragment, TAG_BROWSE).commit();
            }
        } else {
            ActivityUtils.openArticle(this, snNew);
        }
    }

    private boolean isFragmentContainerExist() {
        return findViewById(R.id.fragment_container) != null;
    }

    @Override
    public void onOpen() {
        // TODO Auto-generated method stub

    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void onClose() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(TAG_DISCUSS);
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();
        }
        invalidateOptionsMenu();
    }

    @Override
    public void onOpened() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onClosed() {
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK: {
                if (mSlidingLayer != null && mSlidingLayer.isOpened()) {
                    mSlidingLayer.closeLayer(true);
                    return true;
                }

            }
                break;

            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

}
