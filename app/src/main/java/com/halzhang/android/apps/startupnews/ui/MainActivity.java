
package com.halzhang.android.apps.startupnews.ui;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.OnNavigationListener;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.Window;
import com.google.analytics.tracking.android.EasyTracker;
import com.halzhang.android.apps.startupnews.R;
import com.halzhang.android.apps.startupnews.analytics.Tracker;
import com.halzhang.android.apps.startupnews.entity.SNNew;
import com.halzhang.android.apps.startupnews.parser.BaseHTMLParser;
import com.halzhang.android.apps.startupnews.snkit.JsoupFactory;
import com.halzhang.android.apps.startupnews.snkit.SNApi;
import com.halzhang.android.apps.startupnews.snkit.SessionManager;
import com.halzhang.android.apps.startupnews.ui.CommentsListFragment.OnCommentSelectedListener;
import com.halzhang.android.apps.startupnews.ui.NewsListFragment.OnNewsSelectedListener;
import com.halzhang.android.apps.startupnews.ui.phone.BrowseActivity;
import com.halzhang.android.apps.startupnews.ui.tablet.BrowseFragment;
import com.halzhang.android.apps.startupnews.ui.tablet.DiscussFragment;
import com.halzhang.android.apps.startupnews.ui.tablet.DiscussFragment.OnMenuSelectedListener;
import com.halzhang.android.apps.startupnews.utils.ActivityUtils;
import com.halzhang.android.apps.startupnews.utils.AppUtils;
import com.halzhang.android.common.CDLog;
import com.halzhang.android.common.CDToast;
import com.viewpagerindicator.TitlePageIndicator;

import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.io.IOException;

/**
 * 主页
 * 
 * @author Hal
 */
public class MainActivity extends BaseFragmentActivity implements OnNewsSelectedListener,
        ActionBar.TabListener, OnCommentSelectedListener, OnMenuSelectedListener {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private static final String TAG_NEWS = "tag_news";

    private static final String TAG_NEWEST = "tag_newest";

    private static final String TAG_COMMENT = "tag_comment";

    private static final String TAG_BROWSE = "tag_browse";

    private static final String TAG_DISCUSS = "tag_discuss";

    private ViewPager mViewPager;

    private SectionsPagerAdapter mSectionsPagerAdapter;

    private Intent mFeedbackEmailIntent;

    private SNApiHelper mSnApiHelper;

    private SNNew mSnNew;

    @SuppressWarnings("unused")
    private LogoutTask mLogoutTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        CDLog.i(LOG_TAG, "MainActivity create!");
        requestWindowFeature(Window.FEATURE_PROGRESS);
        super.onCreate(savedInstanceState);
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
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setupViews() {
        if (isMultiplePanel()) {
            // 横屏，平板布局
            final ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
            Context context = getSupportActionBar().getThemedContext();
            ArrayAdapter<CharSequence> list = ArrayAdapter.createFromResource(context,
                    R.array.section_titles, R.layout.sherlock_spinner_item);
            list.setDropDownViewResource(R.layout.sherlock_spinner_dropdown_item);
            actionBar.setListNavigationCallbacks(list, new OnNavigationListener() {

                @Override
                public boolean onNavigationItemSelected(int itemPosition, long itemId) {
                    switchNavigation(itemPosition);
                    return false;
                }
            });

            // actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
            // String[] strArray =
            // getResources().getStringArray(R.array.section_titles);
            // for (int i = 0; i < strArray.length; i++) {
            // ActionBar.Tab tab = getSupportActionBar().newTab();
            // tab.setText(strArray[i]);
            // tab.setTag(i);
            // tab.setTabListener(this);
            // getSupportActionBar().addTab(tab);
            // }

            Fragment fragment = new NewsListFragment();
            Bundle args = new Bundle();
            args.putString(NewsListFragment.ARG_URL, getString(R.string.host, "/news"));
            fragment.setArguments(args);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.content_frame_left, fragment, TAG_NEWS).commitAllowingStateLoss();

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
                SessionManager.getInstance(this).clear();
                CDToast.showToast(this, R.string.tip_logout_success);
                // mLogoutTask = new LogoutTask();
                // mLogoutTask.execute((Void) null);
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
            args.putSerializable(DiscussActivity.ARG_SNNEW, snNew);
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

    private String mLastTag = TAG_NEWS;

    private void switchNavigation(int position) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        String tag = null;
        switch (position) {
            case 0: {
                Fragment fragment = fm.findFragmentByTag(TAG_NEWS);
                if (fragment == null) {
                    fragment = new NewsListFragment();
                    Bundle args = new Bundle();
                    args.putString(NewsListFragment.ARG_URL, getString(R.string.host, "/news"));
                    fragment.setArguments(args);
                    ft.add(R.id.content_frame_left, fragment, TAG_NEWS);
                } else {
                    ft.attach(fragment);
                }
                tag = TAG_NEWS;
            }
                break;
            case 1: {
                Fragment fragment = fm.findFragmentByTag(TAG_NEWEST);
                if (fragment == null) {
                    fragment = new NewsListFragment();
                    Bundle args = new Bundle();
                    args.putString(NewsListFragment.ARG_URL, getString(R.string.host, "/newest"));
                    fragment.setArguments(args);
                    ft.add(R.id.content_frame_left, fragment, TAG_NEWEST);
                } else {
                    ft.attach(fragment);
                }

                tag = TAG_NEWEST;
            }
                break;
            case 2: {
                Fragment fragment = fm.findFragmentByTag(TAG_COMMENT);
                if (fragment == null) {
                    fragment = new CommentsListFragment();
                    ft.add(R.id.content_frame_left, fragment, TAG_COMMENT);
                } else {
                    ft.attach(fragment);
                }
                tag = TAG_COMMENT;
            }
                break;
            default:
                throw new IllegalArgumentException("");
        }
        if (!mLastTag.equals(tag)) {
            Fragment fragment = fm.findFragmentByTag(mLastTag);
            if (fragment != null) {
                ft.detach(fragment);
            }
            mLastTag = tag;
        }
        ft.commitAllowingStateLoss();

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
                    Tracker.getInstance().sendException("User Logout error!", e, false);
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
        if (isMultiplePanel()) {
            showBrowseFragment(snNew);
        } else {
            ActivityUtils.openArticle(this, snNew);
        }
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

    /**
     * 平板布局
     * 
     * @return
     */
    public boolean isMultiplePanel() {
        return findViewById(R.id.fragment_container) != null;
    }

    @Override
    public void onTabSelected(Tab tab, FragmentTransaction ft) {
        int pos = (Integer) tab.getTag();
        switchNavigation(pos);
    }

    @Override
    public void onTabUnselected(Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onCommentSelected(int position, String discussUrl) {
        if (isMultiplePanel()) {
            showDiscussFragment(null, discussUrl);
        } else {
            // phone
            Intent intent = new Intent(this, DiscussActivity.class);
            intent.putExtra(DiscussActivity.ARG_DISCUSS_URL, discussUrl);
            startActivity(intent);
        }
    }

    @Override
    public void onShowArticleSelected(SNNew snNew) {
        mSnNew = snNew;
        showBrowseFragment(snNew);
    }

    @Override
    public void onUpVoteSelected(String postId) {
        mSnApiHelper.upVote(postId);
    }

}
