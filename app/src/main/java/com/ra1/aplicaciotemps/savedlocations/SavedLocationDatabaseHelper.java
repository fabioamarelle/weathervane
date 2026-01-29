package com.ra1.aplicaciotemps.savedlocations;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SavedLocationDatabaseHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "savedlocations.db";
    public SavedLocationDatabaseHelper(Context context) {
        super(context, DB_NAME, null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE saved_locations(id INTEGER PRIMARY KEY AUTOINCREMENT, nom TEXT, lat TEXT, lon TEXT, is_favorite INTEGER DEFAULT 0)");
        db.execSQL("INSERT INTO saved_locations (id, nom, lat, lon, is_favorite) VALUES (0, 'Ubicació actual', '-', '-', 0)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            try {
                db.execSQL("ALTER TABLE saved_locations ADD COLUMN is_favorite INTEGER DEFAULT 0");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // Inserir ubicació
    public boolean insertLocation (String nom, String lat, String lon) {
        SQLiteDatabase db = this.getReadableDatabase();

        ContentValues values = new ContentValues();
        values.put("nom", nom);
        values.put("lat", lat);
        values.put("lon", lon);

        long columnesAfectades = db.insert("saved_locations",null, values);
        db.close();

        return columnesAfectades != -1;
    }

    public boolean updateCurrentLocation (String nom, String lat, String lon) {
        SQLiteDatabase db = this.getReadableDatabase();

        ContentValues values = new ContentValues();
        values.put("nom", nom);
        values.put("lat", lat);
        values.put("lon", lon);
        values.put("is_favorite", 1);

        long columnesAfectades = db.update("saved_locations", values, "id=?", new String[]{"0"});
        db.close();

        return columnesAfectades != -1;
    }

    public Cursor getLocations() {
        SQLiteDatabase db = this.getReadableDatabase();

        String orderBy = "is_favorite DESC, id ASC";

        return db.query("saved_locations",null,null,
        null,null,null,orderBy);
    }

    // Eliminar ubicacions
    public boolean deleteLocation(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        int columnesAfectades = db.delete("saved_locations","id="+id,null);

        return columnesAfectades != 0;
    }

    public boolean updateFavoriteStatus(int id, boolean isFavorite) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("is_favorite", isFavorite ? 1 : 0);

        int columnesAfectades = db.update("saved_locations", values, "id=?", new String[]{String.valueOf(id)});

        return columnesAfectades > 0;
    }
}