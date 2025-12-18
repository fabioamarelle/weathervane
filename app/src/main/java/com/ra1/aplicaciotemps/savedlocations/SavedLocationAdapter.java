package com.ra1.aplicaciotemps.savedlocations;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.ra1.aplicaciotemps.R;
import com.ra1.aplicaciotemps.openweatherapi.Geocoding;
import com.ra1.aplicaciotemps.openweatherapi.Weather;
import com.ra1.aplicaciotemps.utilities.LocationManager;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SavedLocationAdapter extends RecyclerView.Adapter<SavedLocationAdapter.MyViewHolder> {

    private List<SavedLocation> savedLocationList;
    private LocationManager locationManager;
    private Context context;

    public SavedLocationAdapter(Context context, List<SavedLocation> savedLocationList) {
        this.context = context;
        this.savedLocationList = savedLocationList;
        this.locationManager = new LocationManager(context);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.saved_location, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        SavedLocation savedLocation = savedLocationList.get(position);

        holder.nomCiutat.setText(savedLocation.getName());

        // --- NEW LOGIC STARTS HERE ---

        if (savedLocation.getId() == 0) {
            holder.deleteButton.setVisibility(View.GONE);
            handleCurrentLocationWeather(holder, savedLocation);

        } else {
            holder.deleteButton.setVisibility(View.VISIBLE);

            ArrayList<String> coordinates = new ArrayList<>();
            coordinates.add(savedLocation.getLat());
            coordinates.add(savedLocation.getLon());
            fetchWeather(coordinates, holder);
        }

        holder.relativeLayout.setOnClickListener(v -> {
            if (context instanceof Activity) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("CITY_NAME", savedLocation.getName());
                try {
                    returnIntent.putExtra("LAT", Double.parseDouble(savedLocation.getLat()));
                    returnIntent.putExtra("LON", Double.parseDouble(savedLocation.getLon()));
                    ((Activity) context).setResult(Activity.RESULT_OK, returnIntent);
                    ((Activity) context).finish();
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        });

        if (holder.deleteButton.getVisibility() == View.VISIBLE) {
            holder.deleteButton.setOnClickListener(v -> deleteItem(holder, savedLocation.getId(), position));
        }
    }

    private void handleCurrentLocationWeather(MyViewHolder holder, SavedLocation savedLocation) {
        locationManager.getLastLocation(new LocationManager.LocationResultCallback() {
            @Override
            public void onLocationReady(ArrayList<String> coordinates) {
                SavedLocationDatabaseHelper savedLocationDB = new SavedLocationDatabaseHelper(context);
                String cityName = Geocoding.getCityNameByCoordinates(coordinates);

                savedLocationDB.updateCurrentLocation(cityName, coordinates.get(0), coordinates.get(1));

                savedLocation.setName("Ubicació actual");
                savedLocation.setLat(coordinates.get(0));
                savedLocation.setLon(coordinates.get(1));

                holder.itemView.post(() -> holder.nomCiutat.setText("Ubicació actual"));

                fetchWeather(coordinates, holder);
            }

            @Override
            public void onLocationError(String message) {}
        });
    }

    private void fetchWeather(ArrayList<String> coordinates, MyViewHolder holder) {
        new Thread(() -> {
            try {
                String result = Weather.getWeather(coordinates);

                if (result != null) {
                    JSONObject weatherData = new JSONObject(result);
                    long textTemperatura = Math.round(weatherData.getJSONObject("main").getDouble("temp"));
                    String textClima = weatherData.getJSONArray("weather").getJSONObject(0).getString("main");

                    holder.itemView.post(() -> {
                        holder.temperaturaCiutat.setText(textTemperatura + "º");
                        Weather.canviarImatgeClima(textClima, holder.climaCiutat);
                        updateBackground(holder, textClima);
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void deleteItem(MyViewHolder holder, int id, int position) {
        SavedLocationDatabaseHelper savedLocationDB = new SavedLocationDatabaseHelper(context);
        savedLocationDB.deleteLocation(id);

        savedLocationList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, savedLocationList.size());
    }

    private void updateBackground(MyViewHolder holder, String clima) {
        int drawableId;
        switch (clima) {
            case "Clear": drawableId = R.drawable.clearcard; break;
            case "Rain":
            case "Drizzle":
            case "Thunderstorm": drawableId = R.drawable.raincard; break;
            default: drawableId = R.drawable.cloudycard; break;
        }
        holder.relativeLayout.setBackground(ContextCompat.getDrawable(context, drawableId));
    }

    @Override
    public int getItemCount() {
        return savedLocationList.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView nomCiutat, temperaturaCiutat;
        ImageView climaCiutat;
        ImageButton deleteButton;
        RelativeLayout relativeLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            nomCiutat = itemView.findViewById(R.id.nom_ciutat);
            temperaturaCiutat = itemView.findViewById(R.id.temperatura_ciutat);
            climaCiutat = itemView.findViewById(R.id.clima_ciutat);
            deleteButton = itemView.findViewById(R.id.delete_button);
            relativeLayout = itemView.findViewById(R.id.relative_layout);
        }
    }
}