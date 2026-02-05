package com.ra1.aplicaciotemps.views.activities;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ra1.aplicaciotemps.database.SelectableCitiesDatabaseHelper;
import com.ra1.aplicaciotemps.R;
import com.ra1.aplicaciotemps.database.models.SavedLocation;
import com.ra1.aplicaciotemps.database.SavedLocationDatabaseHelper;
import com.ra1.aplicaciotemps.views.adapters.SavedLocationAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CitySelectorActivity extends BaseActivity {

    private AutoCompleteTextView searchAuto;
    private SelectableCitiesDatabaseHelper dbHelper;
    private ArrayAdapter<String> adapter;
    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_selector);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.title_activity_city_selector);
        }

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        List<SavedLocation> savedLocationList = new ArrayList<>();
        SavedLocationDatabaseHelper db = new SavedLocationDatabaseHelper(this);
        Cursor c = db.getLocations();

        if (c.getCount() > 0) {
            c.moveToFirst();
            do {
                int id = c.getInt(0);
                String nom = c.getString(1);
                String lat = c.getString(2);
                String lon = c.getString(3);
                int isFavoriteInt = 0;
                if (c.getColumnCount() > 4) {
                    isFavoriteInt = c.getInt(4);
                }
                SavedLocation location = new SavedLocation(id, nom, lat, lon, false);
                location.setFavorite(isFavoriteInt == 1);
                savedLocationList.add(location);
            } while (c.moveToNext());
        }
        c.close();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        SavedLocationAdapter savedLocationAdapter = new SavedLocationAdapter(this, savedLocationList);
        recyclerView.setAdapter(savedLocationAdapter);

        searchAuto = findViewById(R.id.searchCityAuto);
        searchAuto.setHint(R.string.search_hint);
        dbHelper = new SelectableCitiesDatabaseHelper(this);

        new Thread(() -> {
            try {
                dbHelper.initializeDatabase();
            } catch (IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(CitySelectorActivity.this, R.string.error_db_init, Toast.LENGTH_LONG).show());
            }
        }).start();

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, new ArrayList<>()) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text = view.findViewById(android.R.id.text1);
                String fullText = getItem(position);
                if (fullText != null && fullText.contains("|")) {
                    text.setText(fullText.split("\\|")[0]);
                }
                return view;
            }
        };
        searchAuto.setAdapter(adapter);

        searchAuto.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                handler.removeCallbacksAndMessages(null);
            }
            @Override public void afterTextChanged(Editable s) {
                final String query = s.toString().trim();
                if (query.length() < 2) return;
                handler.postDelayed(() -> runSearch(query), 200);
            }
        });

        searchAuto.setOnItemClickListener((parent, view, position, id) -> {
            String selection = adapter.getItem(position);
            if (selection != null) {
                String[] parts = selection.split("\\|");
                SavedLocationDatabaseHelper savedLocationDB = new SavedLocationDatabaseHelper(this);
                savedLocationDB.insertLocation(parts[0], parts[1], parts[2]);
                Intent returnIntent = new Intent();
                returnIntent.putExtra("CITY_NAME", parts[0]);
                returnIntent.putExtra("LAT", Double.parseDouble(parts[1]));
                returnIntent.putExtra("LON", Double.parseDouble(parts[2]));
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });
    }

    private void runSearch(String query) {
        new Thread(() -> {
            ArrayList<String> results = dbHelper.searchCities(query);
            runOnUiThread(() -> {
                adapter.clear();
                adapter.addAll(results);
                adapter.notifyDataSetChanged();
                if (results.size() > 0 && !searchAuto.isPopupShowing()) {
                    searchAuto.showDropDown();
                }
            });
        }).start();
    }
}