package com.ra1.aplicaciotemps.cityselector;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class CityDatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "cities.db";
    private final File DB_PATH;
    private final Context context;

    public CityDatabaseHelper(Context context) {
        super(context, DB_NAME, null, 1);
        this.context = context;
        this.DB_PATH = context.getDatabasePath(DB_NAME);
    }

    @Override public void onCreate(SQLiteDatabase db) {}
    @Override public void onUpgrade(SQLiteDatabase db, int i, int i1) {}

    public void initializeDatabase() throws IOException {
        if (!DB_PATH.exists()) {
            this.getReadableDatabase();
            this.close();
            copyDatabase();
        }
    }

    private void copyDatabase() throws IOException {
        InputStream input = context.getAssets().open(DB_NAME);
        OutputStream output = new FileOutputStream(DB_PATH);
        byte[] buffer = new byte[4096];
        int length;
        while ((length = input.read(buffer)) > 0) {
            output.write(buffer, 0, length);
        }
        output.flush();
        output.close();
        input.close();
    }

    public ArrayList<String> searchCities(String query) {
        ArrayList<String> results = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT display_name, lat, lon FROM cities WHERE search_name LIKE ? || '%' LIMIT 50",
                new String[]{query.toLowerCase()});

        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(0);
                double lat = cursor.getDouble(1);
                double lon = cursor.getDouble(2);
                results.add(name + "|" + lat + "|" + lon);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return results;
    }
}