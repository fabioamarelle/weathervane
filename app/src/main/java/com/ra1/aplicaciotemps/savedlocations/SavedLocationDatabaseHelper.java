package com.ra1.aplicaciotemps.savedlocations;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SavedLocationDatabaseHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "savedlocations.db";
    private final Context context;

    public SavedLocationDatabaseHelper(Context context) {
        super(context, DB_NAME, null, 1);
        this.context = context;
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE saved_locations(id INTEGER PRIMARY KEY AUTOINCREMENT, nom TEXT, lat TEXT, lon TEXT)");
    }

    // Actualització de la base de dades
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS saved_locations");
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

    // Obtenir ubicacions
    public Cursor getLocations() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(
            "saved_locations",null,null,null,null,null,null
        );
    }

    // Eliminar ubicacions
    public boolean deleteLocation(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        int columnesAfectades = db.delete("saved_locations","id="+id,null);

        return columnesAfectades != 0;
    }

}
