package com.ra1.aplicaciotemps.utilities.api;

import com.ra1.aplicaciotemps.BuildConfig;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class Forecast {
    public static String getForecast(ArrayList<String> coordinates, String unit, String lang) {
        try {
            String apiKey = BuildConfig.API_KEY;
            String urlString = "https://api.openweathermap.org/data/2.5/forecast?units=" + unit +
                    "&lang=" + lang + "&lat=" + coordinates.get(0) + "&lon=" + coordinates.get(1) +
                    "&appid=" + apiKey;

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
}