/**
 * Copyright (C) 2013 HalZhang
 */

package com.halzhang.android.apps.startupnews.ui;

import com.halzhang.android.apps.startupnews.R;

import android.app.ActionBar;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;
import android.support.v4.app.FragmentActivity;
import android.view.MenuItem;

/**
 * StartupNews
 * <p>
 * </p>
 * 
 * @author <a href="http://weibo.com/halzhang">Hal</a>
 * @version Mar 8, 2013
 */
public class AboutActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction().replace(android.R.id.content, new PrefsFragment())
                .commit();
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

    public class PrefsFragment extends PreferenceFragment implements OnPreferenceChangeListener {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.preferences);
            ListPreference listPreference = (ListPreference) findPreference(getString(R.string.pref_key_html_provider));
            listPreference.setOnPreferenceChangeListener(this);
            listPreference.setSummary(listPreference.getEntry());
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            if (preference instanceof ListPreference) {
                ListPreference listPreference = (ListPreference) preference;
                preference.setSummary(listPreference.getEntries()[listPreference.findIndexOfValue((String) newValue)]);
            }
            return true;
        }
    }

}
