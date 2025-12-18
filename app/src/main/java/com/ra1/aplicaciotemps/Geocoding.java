package com.ra1.aplicaciotemps;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class Geocoding {
    public static ArrayList<String> getCoordinatesByCityName(String cityName){
        try {
            String apiKey = "cc990afb60fd6a4a42b6614fdf9091cb";
            String urlString = "https://api.openweathermap.org/geo/1.0/direct?q=" + cityName
                    + "&appid=" + apiKey;

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



                JSONArray jsonArray = new JSONArray(content.toString());
                JSONObject firstLocation = jsonArray.getJSONObject(0);;

                ArrayList<String> al = new ArrayList<>();
                al.add(String.valueOf(firstLocation.getDouble("lat")));
                al.add(String.valueOf(firstLocation.getDouble("lon")));
                return al;
            }
        } catch (Exception e) {
            return new ArrayList<>();
        }
        return new ArrayList<>();
    }

    public static String getCityNameByCoordinates(ArrayList<String> coordinates){
        try {
            String apiKey = "cc990afb60fd6a4a42b6614fdf9091cb";
            String urlString = "https://api.openweathermap.org/geo/1.0/reverse?lat="+
                    coordinates.get(0)+"&lon="+coordinates.get(1)+"&appid="+ apiKey;


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

                JSONArray jsonArray = new JSONArray(content.toString());
                JSONObject firstLocation = jsonArray.getJSONObject(0);;

                return firstLocation.getString("name");
            }
        } catch (Exception e) {
            return e.getMessage();
        }
        return "";
    }
}
