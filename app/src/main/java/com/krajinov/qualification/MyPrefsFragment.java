package com.krajinov.qualification;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;

public class MyPrefsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);
        final ListPreference listPreference = (ListPreference) getPreferenceManager().findPreference("language");
        listPreference.setTitle(listPreference.getEntry().toString());


        listPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {

                ListPreference listPref = (ListPreference) preference;
                listPref.setValue(newValue.toString());
                String title = listPref.getEntry().toString();
                listPreference.setTitle(title);
                return true;
            }
        });
    }

}
