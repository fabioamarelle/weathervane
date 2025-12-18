package com.ra1.aplicaciotemps;

import android.app.Activity;
import android.content.Intent;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CitySelectorActivity extends AppCompatActivity {

    private AutoCompleteTextView searchAuto;
    private CityDatabaseHelper dbHelper;
    private ArrayAdapter<String> adapter;
    private Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_selector);


        // Ciutats guardades
        RecyclerView recyclerView = findViewById(R.id.recyclerView);

        List<SavedLocation> savedLocationList = new ArrayList<>();

        savedLocationList.add(new SavedLocation("Valls", "41.286",
            "1.250"));

        savedLocationList.add(new SavedLocation("Tarragona", "41.117",
            "1.250"));

        savedLocationList.add(new SavedLocation("Bràfim", "41.269",
            "1.341"));

        savedLocationList.add(new SavedLocation("New York", "40.7143",
            "-74.006"));

        savedLocationList.add(new SavedLocation("São Paulo", "-23.5475",
                "-46.6361"));

        savedLocationList.add(new SavedLocation("Buenos Aires", "-34.6132",
                "-58.3772"));

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        SavedLocationAdapter savedLocationAdapter = new SavedLocationAdapter(savedLocationList);
        recyclerView.setAdapter(savedLocationAdapter);



        // Buscador de ciutats
        searchAuto = findViewById(R.id.searchCityAuto);
        dbHelper = new CityDatabaseHelper(this);

        new Thread(() -> {
            try { dbHelper.initializeDatabase(); }
            catch (IOException e) { e.printStackTrace(); }
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

            @Override
            public void afterTextChanged(Editable s) {
                final String query = s.toString().trim();
                if (query.length() < 2) return;

                handler.postDelayed(() -> runSearch(query), 200);
            }
        });

        searchAuto.setOnItemClickListener((parent, view, position, id) -> {
            String selection = adapter.getItem(position);
            String[] parts = selection.split("\\|");

            Intent returnIntent = new Intent();
            returnIntent.putExtra("CITY_NAME", parts[0]);
            returnIntent.putExtra("LAT", Double.parseDouble(parts[1]));
            returnIntent.putExtra("LON", Double.parseDouble(parts[2]));

            setResult(Activity.RESULT_OK, returnIntent);
            finish();
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