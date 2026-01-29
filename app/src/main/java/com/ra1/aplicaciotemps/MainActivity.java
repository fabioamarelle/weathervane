package com.ra1.aplicaciotemps;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.ra1.aplicaciotemps.openweatherapi.Forecast;
import com.ra1.aplicaciotemps.openweatherapi.Geocoding;
import com.ra1.aplicaciotemps.openweatherapi.Weather;
import com.ra1.aplicaciotemps.utilities.LocationManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    TextView temperatura, clima, rangTemperatura, sensacioTermica;
    ImageView imatgeClima;
    TextView cityTitleText, ubicacioActual, ubicacioActualText;
    ImageButton btnChangeLocation, infoButton;
    View screenView;

    TextView previsioTempMaxima1, previsioTempMaxima2, previsioTempMaxima3;
    TextView previsioTempMinima1, previsioTempMinima2, previsioTempMinima3;
    TextView previsioPluja1, previsioPluja2, previsioPluja3;
    TextView previsioData1, previsioData2, previsioData3;
    ImageView previsioIcona1, previsioIcona2, previsioIcona3;

    ArrayList<String> userCoordinates;
    private LocationManager locationManager;
    private boolean modeManual = false;

    private final ActivityResultLauncher<Intent> citySelectionLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    String cityName = result.getData().getStringExtra("CITY_NAME");
                    double lat = result.getData().getDoubleExtra("LAT", 0);
                    double lon = result.getData().getDoubleExtra("LON", 0);

                    if (cityName != null) {
                        modeManual = true;

                        ubicacioActualText.setText("");
                        cityTitleText.setText(cityName.split(",")[0]);
                        ubicacioActual.setText(getString(R.string.location_selected, cityName));

                        userCoordinates = new ArrayList<>();
                        userCoordinates.add(String.valueOf(lat));
                        userCoordinates.add(String.valueOf(lon));

                        updateWeatherData();
                    }
                }
            });

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) fetchWeatherByLocation();
                else clima.setText(getString(R.string.permission_denied));
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicialització de vistes (igual que abans)
        this.screenView = findViewById(R.id.constraint_layout);
        this.temperatura = findViewById(R.id.temperatura);
        this.clima = findViewById(R.id.clima);
        this.rangTemperatura = findViewById(R.id.rangTemperatura);
        this.imatgeClima = findViewById(R.id.imatgeClima);
        this.sensacioTermica = findViewById(R.id.sensacioTermica);
        this.ubicacioActual = findViewById(R.id.ubicacioActual);
        this.ubicacioActualText = findViewById(R.id.ubicacioActualText);

        this.cityTitleText = findViewById(R.id.cityTitleText);
        this.btnChangeLocation = findViewById(R.id.btnChangeLocation);

        this.previsioTempMaxima1 = findViewById(R.id.previsioTempMaxima1);
        this.previsioTempMaxima2 = findViewById(R.id.previsioTempMaxima2);
        this.previsioTempMaxima3 = findViewById(R.id.previsioTempMaxima3);
        this.previsioTempMinima1 = findViewById(R.id.previsioTempMinima1);
        this.previsioTempMinima2 = findViewById(R.id.previsioTempMinima2);
        this.previsioTempMinima3 = findViewById(R.id.previsioTempMinima3);
        this.previsioPluja1 = findViewById(R.id.previsioPluja1);
        this.previsioPluja2 = findViewById(R.id.previsioPluja2);
        this.previsioPluja3 = findViewById(R.id.previsioPluja3);
        this.previsioData1 = findViewById(R.id.previsioData1);
        this.previsioData2 = findViewById(R.id.previsioData2);
        this.previsioData3 = findViewById(R.id.previsioData3);
        this.previsioIcona1 = findViewById(R.id.previsioIcona1);
        this.previsioIcona2 = findViewById(R.id.previsioIcona2);
        this.previsioIcona3 = findViewById(R.id.previsioIcona3);

        this.infoButton = findViewById(R.id.infoButton);
        infoButton.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, InfoActivity.class)));

        this.locationManager = new LocationManager(this);

        btnChangeLocation.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CitySelectorActivity.class);
            citySelectionLauncher.launch(intent);
        });

        fetchWeatherByLocation();
    }

    private void fetchWeatherByLocation() {
        locationManager.checkPermissionAndGetLocation(requestPermissionLauncher, new LocationManager.LocationResultCallback() {
            @Override
            public void onLocationReady(ArrayList<String> coordinates) {
                if (!modeManual) {
                    ubicacioActualText.setText(getString(R.string.location_current_title));
                    userCoordinates = coordinates;
                    new Thread(() -> {
                        final String cityName = Geocoding.getCityNameByCoordinates(userCoordinates);
                        runOnUiThread(() -> {
                            cityTitleText.setText(cityName);
                            ubicacioActual.setText(getString(R.string.location_current_text, cityName));
                        });
                        updateWeatherData();
                    }).start();
                }
            }

            @Override
            public void onLocationError(String message) {
                runOnUiThread(() -> ubicacioActual.setText(message));
            }
        });
    }

    private void updateWeatherData() {
        if (userCoordinates == null) return;

        new Thread(() -> {
            final String weatherResult = Weather.getWeather(userCoordinates);
            final String forecastResult = Forecast.getForecast(userCoordinates);

            runOnUiThread(() -> {
                showWeather(weatherResult);
                showForecast(forecastResult);
            });
        }).start();
    }

    protected void showWeather(String result) {
        if (result.startsWith("Error") || result.equals("-")) {
            clima.setText(result);
            temperatura.setText(getString(R.string.temp_placeholder));
            rangTemperatura.setText("");
            sensacioTermica.setText("");
            imatgeClima.setImageResource(android.R.drawable.ic_dialog_alert);
            return;
        }

        try {
            JSONObject weatherData = new JSONObject(result);

            long textTemperatura = Math.round(weatherData.getJSONObject("main").getDouble("temp"));
            this.temperatura.setText(getString(R.string.degree_symbol, textTemperatura));

            String textClimaApi = weatherData.getJSONArray("weather").getJSONObject(0).getString("main");

            updateBackgroundAndImage(textClimaApi, imatgeClima);

            this.clima.setText(getLocalizedWeatherName(textClimaApi));

            long textTemperaturaMin = Math.round(weatherData.getJSONObject("main").getDouble("temp_min"));
            long textTemperaturaMax = Math.round(weatherData.getJSONObject("main").getDouble("temp_max"));

            this.rangTemperatura.setText(getString(R.string.temp_range_format, textTemperaturaMin, textTemperaturaMax));

            long textSensacioTermica = Math.round(weatherData.getJSONObject("main").getDouble("feels_like"));
            this.sensacioTermica.setText(getString(R.string.feels_like_format, textSensacioTermica));

        } catch (JSONException e) {
            this.clima.setText(getString(R.string.error_data));
        }
    }

    protected void showForecast(String result) {
        if (result.startsWith("Error") || result.equals("-")) {
            return;
        }
        try {
            JSONObject weatherData = new JSONObject(result);

            double max1 = Math.round(weatherData.getJSONArray("list").getJSONObject(0).getJSONObject("main").getDouble("temp_max"));
            double max2 = Math.round(weatherData.getJSONArray("list").getJSONObject(1).getJSONObject("main").getDouble("temp_max"));
            double max3 = Math.round(weatherData.getJSONArray("list").getJSONObject(2).getJSONObject("main").getDouble("temp_max"));

            double min1 = Math.round(weatherData.getJSONArray("list").getJSONObject(0).getJSONObject("main").getDouble("temp_min"));
            double min2 = Math.round(weatherData.getJSONArray("list").getJSONObject(1).getJSONObject("main").getDouble("temp_min"));
            double min3 = Math.round(weatherData.getJSONArray("list").getJSONObject(2).getJSONObject("main").getDouble("temp_min"));

            int pop1 = (int) (weatherData.getJSONArray("list").getJSONObject(0).getDouble("pop") * 100);
            int pop2 = (int) (weatherData.getJSONArray("list").getJSONObject(1).getDouble("pop") * 100);
            int pop3 = (int) (weatherData.getJSONArray("list").getJSONObject(2).getDouble("pop") * 100);

            String clima1 = weatherData.getJSONArray("list").getJSONObject(0).getJSONArray("weather").getJSONObject(0).getString("main");
            String clima2 = weatherData.getJSONArray("list").getJSONObject(1).getJSONArray("weather").getJSONObject(0).getString("main");
            String clima3 = weatherData.getJSONArray("list").getJSONObject(2).getJSONArray("weather").getJSONObject(0).getString("main");

            this.previsioTempMaxima1.setText(getString(R.string.degree_symbol, (int) max1));
            this.previsioTempMaxima2.setText(getString(R.string.degree_symbol, (int) max2));
            this.previsioTempMaxima3.setText(getString(R.string.degree_symbol, (int) max3));

            this.previsioTempMinima1.setText(getString(R.string.degree_symbol, (int) min1));
            this.previsioTempMinima2.setText(getString(R.string.degree_symbol, (int) min2));
            this.previsioTempMinima3.setText(getString(R.string.degree_symbol, (int) min3));

            this.previsioPluja1.setText(getString(R.string.percentage_symbol, pop1));
            this.previsioPluja2.setText(getString(R.string.percentage_symbol, pop2));
            this.previsioPluja3.setText(getString(R.string.percentage_symbol, pop3));

            Weather.canviarImatgeClima(clima1, previsioIcona1);
            Weather.canviarImatgeClima(clima2, previsioIcona2);
            Weather.canviarImatgeClima(clima3, previsioIcona3);

            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_YEAR, 1);
            previsioData1.setText(String.format(Locale.getDefault(), "%02d/%02d", calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH) + 1));
            calendar.add(Calendar.DAY_OF_YEAR, 1);
            previsioData2.setText(String.format(Locale.getDefault(), "%02d/%02d", calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH) + 1));
            calendar.add(Calendar.DAY_OF_YEAR, 1);
            previsioData3.setText(String.format(Locale.getDefault(), "%02d/%02d", calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH) + 1));

        } catch (JSONException e) {
            this.ubicacioActual.setText(getString(R.string.error_data_details, e.getMessage()));
        }
    }

    private void updateBackgroundAndImage(String apiWeather, ImageView imageView) {
        Weather.canviarImatgeClima(apiWeather, imageView);
        switch (apiWeather) {
            case "Clouds":
            case "Snow":
                this.screenView.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.cloudybackground));
                break;
            case "Clear":
                this.screenView.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.clearbackground));
                break;
            case "Rain":
            case "Drizzle":
            case "Thunderstorm":
                this.screenView.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.rainbackground));
                break;
            default:
                this.screenView.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.cloudybackground));
                break;
        }
    }

    private String getLocalizedWeatherName(String apiWeather) {
        switch (apiWeather) {
            case "Clouds": return getString(R.string.weather_clouds);
            case "Clear": return getString(R.string.weather_clear);
            case "Snow": return getString(R.string.weather_snow);
            case "Rain": return getString(R.string.weather_rain);
            case "Drizzle": return getString(R.string.weather_drizzle);
            case "Thunderstorm": return getString(R.string.weather_thunderstorm);
            default: return getString(R.string.weather_unknown);
        }
    }
}