package com.ra1.aplicaciotemps.views.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Locale;

public class BaseActivity extends AppCompatActivity {
    private String langAtStart;
    private String unitAtStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        applySettings();
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = getSharedPreferences("settings", Context.MODE_PRIVATE);
        langAtStart = prefs.getString("language", Locale.getDefault().getLanguage());
        unitAtStart = prefs.getString("temp_unit", "metric");
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences prefs = getSharedPreferences("settings", Context.MODE_PRIVATE);
        String currentLang = prefs.getString("language", Locale.getDefault().getLanguage());
        String currentUnit = prefs.getString("temp_unit", "metric");

        if (!langAtStart.equals(currentLang) || !unitAtStart.equals(currentUnit)) {
            recreate();
        }
    }

    private void applySettings() {
        SharedPreferences prefs = getSharedPreferences("settings", Context.MODE_PRIVATE);
        String lang = prefs.getString("language", Locale.getDefault().getLanguage());
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.setLocale(locale);
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
    }
}