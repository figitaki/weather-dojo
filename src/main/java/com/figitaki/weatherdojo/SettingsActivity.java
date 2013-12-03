package com.figitaki.weatherdojo;

import android.os.Bundle;
import android.preference.PreferenceActivity;


public class SettingsActivity extends PreferenceActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);

        getActionBar().setDisplayHomeAsUpEnabled(true);
    }
}