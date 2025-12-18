package com.ra1.aplicaciotemps.utilities;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import androidx.activity.result.ActivityResultLauncher;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;

public class LocationManager {

    private final FusedLocationProviderClient fusedLocationProviderClient;
    private final Context context;

    public interface LocationResultCallback {
        void onLocationReady(ArrayList<String> coordinates);
        void onLocationError(String message);
    }

    public LocationManager(Context context) {
        this.context = context;
        this.fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
    }

    public void checkPermissionAndGetLocation(
            ActivityResultLauncher<String> requestPermissionLauncher,
            LocationResultCallback callback) {

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        } else {
            getLastLocation(callback);
        }
    }

    public void getLastLocation(LocationResultCallback callback) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(location -> {
                        if (location != null) {
                            ArrayList<String> coordinates = new ArrayList<>();
                            coordinates.add(String.valueOf(location.getLatitude()));
                            coordinates.add(String.valueOf(location.getLongitude()));
                            callback.onLocationReady(coordinates);
                        } else {
                            callback.onLocationError("No s'han trobat dades d'ubicació.");
                        }
                    })
                    .addOnFailureListener(e -> {
                        callback.onLocationError("Error al obtenir ubicació: " + e.getMessage());
                    });
        } else {
            callback.onLocationError("Permís de localització no concedit.");
        }
    }
}