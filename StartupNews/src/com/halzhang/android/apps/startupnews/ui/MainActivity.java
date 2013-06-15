
package com.halzhang.android.apps.startupnews.ui;

import android.content.res.Configuration;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
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
import com.halzhang.android.apps.startupnews.ui.tablet.BrowseFragment;
import com.halzhang.android.apps.startupnews.utils.ActivityUtils;
import com.halzhang.android.apps.startupnews.utils.AppUtils;
import com.halzhang.android.common.CDLog;
import com.halzhang.android.common.CDToast;
import com.viewpagerindicator.TitlePageIndicator;

import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.widget.Toast;

import java.io.IOException;

/**
 * 主页
 * 
 * @author Hal
 */
public class MainActivity extends BaseFragmentActivity implements AdapterView.OnItemClickListener,NewsListFragment.OnNewsSelectedListener {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private ViewPager mViewPager;

    private SectionsPagerAdapter mSectionsPagerAdapter;

    private Intent mFeedbackEmailIntent;

    private ListView mDrawerListView;

    private DrawerLayout mDrawerLayout;

    private ActionBarDrawerToggle mDrawerToggle;

    @SuppressWarnings("unused")
    private LogoutTask mLogoutTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        CDLog.i(LOG_TAG,"MainAction create!");
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_PROGRESS);
        setContentView(R.layout.activity_main);
        setupViews();
        mFeedbackEmailIntent = createEmailIntent();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if(mDrawerToggle != null){
           mDrawerToggle.syncState();
        }
    }

    private void setupViews() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (mDrawerLayout != null) {
            //横屏，平板布局
            final ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            mDrawerToggle = new ActionBarDrawerToggle(this, actionBar, mDrawerLayout, R.drawable.ic_drawer, R.string.drawer_opened, R.string.drawer_closed) {

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
            mDrawerListView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_activated_1, android.R.id.text1, getResources().getStringArray(R.array.section_titles)));

            NewsListFragment newsListFragment = new NewsListFragment();
            Bundle args = new Bundle();
            args.putString(NewsListFragment.ARG_URL, getString(R.string.host, "/news"));
            newsListFragment.setArguments(args);
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame_left,newsListFragment).commitAllowingStateLoss();

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
                .append(AppUtils.getVersionName(getApplicationContext())).append(getString(R.string.feedback));
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
        if(mDrawerToggle != null && mDrawerToggle.onOptionsItemSelected(item)){
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
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        final Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.article_fragment);
        if (fragment != null){
            if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
                getSupportFragmentManager().beginTransaction().hide(fragment).commit();
            }else {
                getSupportFragmentManager().beginTransaction().show(fragment).commit();
            }
        }
        if(mDrawerToggle != null){
            mDrawerToggle.onConfigurationChanged(newConfig);
        }
    }


    @Override
    public void onNewsSelected(int position, SNNew snNew) {
        //处理文章被选中，竖屏启动Activity，平板更新右栏
        BrowseFragment browseFragment = (BrowseFragment) getSupportFragmentManager().findFragmentById(R.id.article_fragment);
        if (browseFragment != null) {
            browseFragment.load(snNew.getUrl());
        } else {
            ActivityUtils.openArticle(this, snNew);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //TODO handle drawer left listview
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

}
