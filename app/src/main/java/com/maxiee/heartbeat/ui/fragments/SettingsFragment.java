package com.maxiee.heartbeat.ui.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import com.maxiee.heartbeat.R;
import com.maxiee.heartbeat.ui.CrashListActivity;

/**
 * Created by maxiee on 15-6-28.
 * Thanks to fython/NHentai-android (https://github.com/fython/NHentai-android)
 */
public class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener{

    private final static String GITHUB_URL = "https://github.com/maxiee/HeartBeat";
    private final static String Weibo_URL = "http://weibo.com/maxiee";

    private Preference mVersionPref;
    private Preference mGitHubPref;
    private Preference mWeiboPref;
    private Preference mCrashPref;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);

        mVersionPref = (Preference) findPreference("version");
        mGitHubPref = (Preference) findPreference("github");
        mWeiboPref = (Preference) findPreference("weibo");
        mCrashPref = (Preference) findPreference("crash");

        String version = "Unknown";
        try {
            version = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName;
            version += " (" + getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionCode + ")";
        } catch (Exception e) {e.printStackTrace();}

        mVersionPref.setSummary(version);
        mGitHubPref.setOnPreferenceClickListener(this);
        mGitHubPref.setSummary(GITHUB_URL);
        mWeiboPref.setOnPreferenceClickListener(this);
        mWeiboPref.setSummary(Weibo_URL);
        mCrashPref.setSummary(getString(R.string.settings_crash_summary));
        mCrashPref.setOnPreferenceClickListener(this);
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        if (preference == mWeiboPref) {
            Uri uri = Uri.parse(Weibo_URL);
            startActivity(new Intent(Intent.ACTION_VIEW, uri));
            return true;
        }
        if (preference == mGitHubPref) {
            Uri uri = Uri.parse(GITHUB_URL);
            startActivity(new Intent(Intent.ACTION_VIEW, uri));
            return true;
        }
        if (preference == mCrashPref) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_MAIN);
            intent.setClass(getActivity(), CrashListActivity.class);
            startActivity(intent);
            return true;
        }
        return false;
    }
}
