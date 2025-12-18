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
import com.ra1.aplicaciotemps.openweatherapi.Weather;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SavedLocationAdapter extends RecyclerView.Adapter<SavedLocationAdapter.MyViewHolder> {

    private List<SavedLocation> savedLocationList;

    public SavedLocationAdapter(List<SavedLocation> savedLocationList) {
        this.savedLocationList = savedLocationList;
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

        holder.relativeLayout.setOnClickListener(v -> {
            Context context = v.getContext();

            if (context instanceof Activity) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("CITY_NAME", savedLocation.getName());
                returnIntent.putExtra("LAT", Double.parseDouble(savedLocation.getLat()));
                returnIntent.putExtra("LON", Double.parseDouble(savedLocation.getLon()));
                ((Activity) context).setResult(Activity.RESULT_OK, returnIntent);
                ((Activity) context).finish();
            }
        });

        new Thread(() -> {
            try {
                ArrayList<String> coordinates = new ArrayList<>();
                coordinates.add(savedLocation.getLat());
                coordinates.add(savedLocation.getLon());

                String result = Weather.getWeather(coordinates);

                if (result != null) {
                    JSONObject weatherData = new JSONObject(result);
                    long textTemperatura = Math.round(weatherData.getJSONObject("main").getDouble("temp"));
                    String textClima = weatherData.getJSONArray("weather").getJSONObject(0).getString("main");

                    holder.deleteButton.setOnClickListener(v -> deleteItem(holder, savedLocation.getId()));

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

    private void deleteItem(MyViewHolder holder, int id) {
        SavedLocationDatabaseHelper savedLocationDB = new SavedLocationDatabaseHelper(holder.itemView.getContext());
        savedLocationDB.deleteLocation(id);
        savedLocationList.removeIf(savedLocation -> savedLocation.getId() == id);
        this.notifyDataSetChanged();
    }

    private void updateBackground(MyViewHolder holder, String clima) {
        Context context = holder.itemView.getContext();
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
