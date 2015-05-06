/**
 * Copyright (C) 2013 HalZhang
 */

package com.halzhang.android.apps.startupnews.ui;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.google.analytics.tracking.android.EasyTracker;
import com.halzhang.android.apps.startupnews.R;
import com.halzhang.android.apps.startupnews.analytics.Tracker;
import com.halzhang.android.apps.startupnews.utils.AppUtils;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;

/**
 * StartupNews
 * <p>
 * 设置
 * </p>
 * 
 * @author <a href="http://weibo.com/halzhang">Hal</a>
 * @version Mar 8, 2013
 */
public class AboutActivity extends SherlockPreferenceActivity implements OnPreferenceChangeListener {

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_Sherlock);
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        ListPreference listPreference = (ListPreference) findPreference(getString(R.string.pref_key_html_provider));
        listPreference.setOnPreferenceChangeListener(this);
        listPreference.setSummary(listPreference.getEntry());
        (findPreference(getString(R.string.pref_key_default_browse)))
                .setOnPreferenceChangeListener(this);
        Preference versionPref = findPreference(getString(R.string.pref_key_version));
        versionPref.setSummary(getString(R.string.pref_summary_version,
                AppUtils.getVersionName(getApplicationContext())));
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
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
