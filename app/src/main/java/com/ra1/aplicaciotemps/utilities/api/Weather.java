package com.ra1.aplicaciotemps.utilities.api;

import android.widget.ImageView;

import com.ra1.aplicaciotemps.BuildConfig;
import com.ra1.aplicaciotemps.R;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class Weather {
    public static String getWeather(ArrayList<String> coordinates, String unit, String lang){
        try {
            String apiKey = BuildConfig.API_KEY;
            String urlString = "https://api.openweathermap.org/data/2.5/weather?lat=" + coordinates.get(0)
                    + "&lon=" + coordinates.get(1) + "&appid=" + apiKey + "&units=" + unit + "&lang=" + lang;

            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder content = new StringBuilder();
                String inputLine;

                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                in.close();
                return content.toString();
            } else {
                return "Error: Codi HTTP " + responseCode;
            }
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    public static void canviarImatgeClima(String clima, ImageView icona) {
        switch (clima) {
            case "Clouds": icona.setImageResource(R.drawable.weather_icon_cloudy); break;
            case "Clear": icona.setImageResource(R.drawable.weather_icon_clear); break;
            case "Snow": icona.setImageResource(R.drawable.weather_icon_cloudy_snow); break;
            case "Rain": icona.setImageResource(R.drawable.weather_icon_rain); break;
            case "Drizzle": icona.setImageResource(R.drawable.weather_icon_drizzle); break;
            case "Thunderstorm": icona.setImageResource(R.drawable.weather_icon_thunderstorm); break;
            default: icona.setImageResource(R.drawable.weather_icon_cloudy); break;
        }
    }
}