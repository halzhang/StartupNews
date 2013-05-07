
package com.halzhang.android.apps.startupnews.ui;

import com.actionbarsherlock.view.Menu;
import com.halzhang.android.apps.startupnews.MyApplication;
import com.halzhang.android.apps.startupnews.R;
import com.halzhang.android.apps.startupnews.snkit.SNApi;
import com.halzhang.android.apps.startupnews.snkit.SessionManager;
import com.halzhang.android.apps.startupnews.ui.fragments.CommentsListFragment;
import com.halzhang.android.apps.startupnews.ui.fragments.NewsListFragment;
import com.halzhang.android.apps.startupnews.utils.ActivityUtils;
import com.halzhang.android.apps.startupnews.utils.AppUtils;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.viewpagerindicator.TitlePageIndicator;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.widget.Toast;

/**
 * 主页
 * 
 * @author Hal
 */
public class MainActivity extends BaseFragmentActivity {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private ViewPager mViewPager;

    private SectionsPagerAdapter mSectionsPagerAdapter;

    private Intent mFeedbackEmailIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mViewPager = (ViewPager) findViewById(R.id.pager);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mSectionsPagerAdapter);
        TitlePageIndicator indicator = (TitlePageIndicator) findViewById(R.id.titles);
        indicator.setViewPager(mViewPager);

        mFeedbackEmailIntent = createEmailIntent();
    }

    private Intent createEmailIntent() {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] {
            "ghanguo@gmail.com"
        });
        StringBuilder builder = new StringBuilder();
        builder.append(getString(R.string.app_name)).append(" v")
                .append(AppUtils.getVersionName(getApplicationContext())).append("反馈");
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
                startActivity(new Intent(this, AboutActivity.class));
                return true;
            case R.id.menu_feedback:
                if (ActivityUtils.isIntentAvailable(getApplicationContext(), mFeedbackEmailIntent)) {
                    startActivity(mFeedbackEmailIntent);
                } else {
                    Toast.makeText(getApplicationContext(), R.string.error_noemailapp,
                            Toast.LENGTH_LONG).show();
                }
                return true;
            case R.id.menu_login: {
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
            }
                return true;
            case R.id.menu_logout:
                SNApi api = new SNApi(getApplicationContext());
                api.logout(MyApplication.instance().getLogInOutURL(),
                        new AsyncHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, String content) {
                                super.onSuccess(statusCode, content);
                                SessionManager.getInstance(getApplicationContext()).clear();
                                Log.i(LOG_TAG, "注销成功！");
                            }

                            @Override
                            public void onFailure(Throwable error, String content) {
                                super.onFailure(error, content);
                            }
                        });
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
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

}
