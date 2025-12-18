package com.ra1.aplicaciotemps;

import android.widget.ImageView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class Weather {
    public static String getWeather(String location) {
        try {
            String apiKey = "cc990afb60fd6a4a42b6614fdf9091cb";
            String urlString = "https://api.openweathermap.org/data/2.5/weather?q=" + location +
                    "&appid=" + apiKey + "&units=Metric" + "&lang=ca";

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

    public static String getWeather(ArrayList<String> coordinates){
        try {
            String apiKey = "cc990afb60fd6a4a42b6614fdf9091cb";
            String urlString = "https://api.openweathermap.org/data/2.5/weather?lat="+coordinates.get(0)
                    +"&lon="+coordinates.get(1)+"&appid=" + apiKey + "&units=Metric" + "&lang=ca";

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
            case "Clouds": icona.setImageResource(R.drawable.cloudy); break;
            case "Clear": icona.setImageResource(R.drawable.clear_day); break;
            case "Snow": icona.setImageResource(R.drawable.cloudy_with_snow_light); break;
            case "Rain": icona.setImageResource(R.drawable.showers_rain); break;
            case "Drizzle": icona.setImageResource(R.drawable.drizzle); break;
            case "Thunderstorm": icona.setImageResource(R.drawable.strong_thunderstorms); break;
            default: icona.setImageResource(R.drawable.cloudy); break;
        }
    }
}
