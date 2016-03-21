package com.krajinov.qualification;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MyPrefsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        this.setTitle(R.string.settings);

        setContentView(R.layout.fragment_settings);

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        MyPrefsFragment myPrefsFragment = new MyPrefsFragment();
        fragmentTransaction.replace(android.R.id.content, myPrefsFragment);

        fragmentTransaction.commit();
    }
}
