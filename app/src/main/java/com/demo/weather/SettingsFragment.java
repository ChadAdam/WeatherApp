package com.demo.weather;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;

import com.demo.weather.sync.SunshineSyncUtils;
import com.demo.weather.utils.PreferenceLoc;
import com.demo.weather.utils.WeatherContract;

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {
    private void setPreferenceSum( Preference p , Object v){
        String value = v.toString();
        String key = p.getKey();
        if(p instanceof ListPreference){
            ListPreference listPreference = (ListPreference)p;
            int index = listPreference.findIndexOfValue(value);
            if(index >=0){
                p.setSummary(listPreference.getEntries()[index]);
            }
        }
        else{p.setSummary(value);}
    }
    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.pref_weather);
        SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        int count = preferenceScreen.getPreferenceCount();
        for(int i =0;i<count;i++){
            Preference p = preferenceScreen.getPreference(i);
            // Only Edit text and List Pref
            String v = sharedPreferences.getString(p.getKey(), "");
            setPreferenceSum(p , v);
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);

    }
    @Override
    public void onStart() {
        super.onStart();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        Activity activity = getActivity();
        if(s.equals("location")){
            PreferenceLoc.resetLocationCoordinates(activity);
            SunshineSyncUtils.startImmediateSync(activity);
        }
        else if (s.equals("units")){
            activity.getContentResolver().notifyChange(WeatherContract.WeatherEntry.CONTENT_URI, null);
        }

        Preference p = findPreference(s);
        if(p !=null){
            if(!(p instanceof CheckBoxPreference)){
            setPreferenceSum(p , sharedPreferences.getString(p.getKey(), ""));
        }}
    }
}
