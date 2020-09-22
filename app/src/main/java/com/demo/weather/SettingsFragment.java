package com.demo.weather;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.preference.PreferenceFragmentCompat;

public class SettingsFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.pref_weather);
    }
}
