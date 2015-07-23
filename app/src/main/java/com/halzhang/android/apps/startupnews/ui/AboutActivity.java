/**
 * Copyright (C) 2013 HalZhang
 */

package com.halzhang.android.apps.startupnews.ui;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;

import com.google.analytics.tracking.android.EasyTracker;
import com.halzhang.android.apps.startupnews.R;
import com.halzhang.android.apps.startupnews.analytics.Tracker;
import com.halzhang.android.apps.startupnews.utils.AppUtils;

/**
 * StartupNews
 * <p>
 * 设置
 * </p>
 *
 * @author <a href="http://weibo.com/halzhang">Hal</a>
 * @version Mar 8, 2013
 */
public class AboutActivity extends BaseFragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        getFragmentManager().beginTransaction().replace(android.R.id.content, new AppPreferenceFragment()).commit();

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
//        LinearLayout root = (LinearLayout) findViewById(android.R.id.list).getParent().getParent().getParent();
//        Toolbar bar = (Toolbar) LayoutInflater.from(this).inflate(R.layout.preference_toolbar, root, false);
//        root.addView(bar, 0); // insert at top
//        bar.setTitleTextColor(getResources().getColor(android.R.color.white));
//        bar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public static class AppPreferenceFragment extends PreferenceFragment implements OnPreferenceChangeListener {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
            ListPreference listPreference = (ListPreference) findPreference(getString(R.string.pref_key_html_provider));
            listPreference.setOnPreferenceChangeListener(this);
            listPreference.setSummary(listPreference.getEntry());
            (findPreference(getString(R.string.pref_key_default_browse)))
                    .setOnPreferenceChangeListener(this);
            Preference versionPref = findPreference(getString(R.string.pref_key_version));
            versionPref.setSummary(getString(R.string.pref_summary_version,
                    AppUtils.getVersionName(getActivity())));
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            final String key = preference.getKey();
            if (getString(R.string.pref_key_html_provider).equals(key)) {
                Tracker.getInstance().sendEvent("preference_change_action",
                        "preference_change_html_provider",
                        String.format("html_provider_%1$s", (String) newValue), 0L);
                ListPreference listPreference = (ListPreference) preference;
                preference.setSummary(listPreference.getEntries()[listPreference
                        .findIndexOfValue((String) newValue)]);
            } else if (getString(R.string.pref_key_default_browse).equals(key)) {
                Tracker.getInstance().sendEvent("preference_change_action",
                        "preference_change_default_browse",
                        String.format("default_browse_%1$s", String.valueOf(newValue)), 0L);
            }
            return true;
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        EasyTracker.getInstance(this).activityStart(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EasyTracker.getInstance(this).activityStop(this);
    }

}
