package com.ra1.aplicaciotemps;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class Forecast {
    public static String getForecast(ArrayList<String> coordinates) {
        try {
            String apiKey = "cc990afb60fd6a4a42b6614fdf9091cb";
            String urlString = "https://api.openweathermap.org/data/2.5/forecast?units=metric&lat=" +
                coordinates.get(0)+"&lon="+coordinates.get(1)+"&appid="+apiKey;

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

    public static String getForecast(String cityName){
        return getForecast(Geocoding.getCoordinatesByCityName(cityName));
    }
}

