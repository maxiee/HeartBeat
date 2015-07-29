package com.maxiee.heartbeat.ui.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.AlertDialog;

import com.maxiee.heartbeat.R;
import com.maxiee.heartbeat.ui.CrashListActivity;
import com.maxiee.heartbeat.ui.PatternActivity;

/**
 * Created by maxiee on 15-6-28.
 * Thanks to fython/NHentai-android (https://github.com/fython/NHentai-android)
 */
public class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener{

    private final static String GITHUB_URL = "https://github.com/maxiee/HeartBeat";
    private final static String Weibo_URL = "http://weibo.com/maxiee";
    private final static String EMAIL = "maxieewong@gmail.com";

    private Preference mPatternPref;
    private Preference mVersionPref;
    private Preference mGitHubPref;
    private Preference mWeiboPref;
    private Preference mCrashPref;
    private Preference mEmailPref;
    private SharedPreferences mPrefs;

    private String mPattern;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);

        mPatternPref = (Preference) findPreference("pattern");
        mVersionPref = (Preference) findPreference("version");
        mGitHubPref = (Preference) findPreference("github");
        mWeiboPref = (Preference) findPreference("weibo");
        mCrashPref = (Preference) findPreference("crash");
        mEmailPref = (Preference) findPreference("email");

        String version = "Unknown";
        try {
            version = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName;
            version += " (" + getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionCode + ")";
        } catch (Exception e) {e.printStackTrace();}

        mPrefs = getActivity().getSharedPreferences("hb", Context.MODE_PRIVATE);
        mVersionPref.setSummary(version);
        mGitHubPref.setOnPreferenceClickListener(this);
        mGitHubPref.setSummary(GITHUB_URL);
        mWeiboPref.setOnPreferenceClickListener(this);
        mWeiboPref.setSummary(Weibo_URL);
        mCrashPref.setSummary(getString(R.string.settings_crash_summary));
        mCrashPref.setOnPreferenceClickListener(this);
        mEmailPref.setSummary(EMAIL);
        initPattern();
    }

    @Override
    public void onResume() {
        super.onResume();
        initPattern();
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
        if (preference == mPatternPref) {
            onPatternClick();
            return true;
        }
        return false;
    }

    private void initPattern() {
        mPattern = mPrefs.getString(
                "pattern",
                ""
        );
        if (mPattern.isEmpty()) {
            mPatternPref.setSummary(getString(R.string.empty));
        } else {
            mPatternPref.setSummary(getString(R.string.setted));
        }
        mPatternPref.setOnPreferenceClickListener(this);
    }

    private void onPatternClick() {
        if (mPattern.isEmpty()) {
            Intent i = new Intent();
            i.setClass(getActivity(), PatternActivity.class);
            i.putExtra(PatternActivity.ACTION, PatternActivity.SET);
            startActivity(i);
        } else {
            AlertDialog.Builder builder =
                    new AlertDialog.Builder(getActivity(), R.style.AppTheme_Dialog);
            builder.setTitle(getString(R.string.choice));
            builder.setItems(
                    new String[]{
                            getString(R.string.settings_pattern_cancel),
                            getString(R.string.settings_pattern_change)
                    },
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == 0) {
                                Intent i = new Intent(getActivity(), PatternActivity.class);
                                i.putExtra(PatternActivity.ACTION, PatternActivity.CANCEL);
                                startActivity(i);
                                dialog.dismiss();
                            } else if (which == 1) {
                                Intent i = new Intent(getActivity(), PatternActivity.class);
                                i.putExtra(PatternActivity.ACTION, PatternActivity.MODIFY);
                                startActivity(i);
                                dialog.dismiss();
                            }
                        }
                    }
            );
            builder.show();
        }
    }
}
