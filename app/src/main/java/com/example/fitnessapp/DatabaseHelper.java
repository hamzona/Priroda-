package com.example.fitnessapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "fitnessapp.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_USER = "users";
    public static final String TABLE_WATER_USAGE = "water_usage";
    public static final String TABLE_PLASTIC_USAGE = "plastic_usage";
    public static final String TABLE_FUEL_USAGE = "fuel_usage";
    public static final String TABLE_ELECTRICITY_USAGE = "electricity_usage";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_LAST_NAME = "last_name";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_PASSWORD = "password";

    private static final String CREATE_TABLE_USER =
            "CREATE TABLE " + TABLE_USER + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_NAME + " TEXT NOT NULL, " +
                    COLUMN_LAST_NAME + " TEXT NOT NULL, " +
                    COLUMN_USERNAME + " TEXT NOT NULL UNIQUE, " +
                    COLUMN_EMAIL + " TEXT NOT NULL UNIQUE, " +
                    COLUMN_PASSWORD + " TEXT NOT NULL);";

    private static final String CREATE_TABLE_WATER_USAGE =
            "CREATE TABLE " + TABLE_WATER_USAGE + " (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "water_usage REAL NOT NULL, " +
                    "date TEXT NOT NULL);";

    private static final String CREATE_TABLE_PLASTIC_USAGE =
            "CREATE TABLE " + TABLE_PLASTIC_USAGE + " (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "plastic_usage INTEGER NOT NULL, " +
                    "date TEXT NOT NULL);";

    private static final String CREATE_TABLE_FUEL_USAGE =
            "CREATE TABLE " + TABLE_FUEL_USAGE + " (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "fuel_usage REAL NOT NULL, " +
                    "date TEXT NOT NULL);";

    private static final String CREATE_TABLE_ELECTRICITY_USAGE =
            "CREATE TABLE " + TABLE_ELECTRICITY_USAGE + " (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "electricity_usage REAL NOT NULL, " +
                    "date TEXT NOT NULL);";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(CREATE_TABLE_USER);
            db.execSQL(CREATE_TABLE_WATER_USAGE);
            db.execSQL(CREATE_TABLE_PLASTIC_USAGE);
            db.execSQL(CREATE_TABLE_FUEL_USAGE);
            db.execSQL(CREATE_TABLE_ELECTRICITY_USAGE);
            Log.d("DatabaseHelper", "Tables created successfully");
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error creating tables: " + e.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WATER_USAGE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PLASTIC_USAGE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FUEL_USAGE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ELECTRICITY_USAGE);
        onCreate(db);
    }

    public long addWaterUsage(double waterUsage) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("water_usage", waterUsage);
        values.put("date", String.valueOf(System.currentTimeMillis())); // Add timestamp
        return db.insert(TABLE_WATER_USAGE, null, values);
    }

    public double getWeeklyWaterUsage() {
        SQLiteDatabase db = this.getReadableDatabase();
        double weeklyWaterUsage = 0;

        String query = "SELECT SUM(water_usage) FROM " + TABLE_WATER_USAGE +
                " WHERE strftime('%W', date, 'unixepoch') = strftime('%W', 'now')";
        try (Cursor cursor = db.rawQuery(query, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                weeklyWaterUsage = cursor.getDouble(0);
            }
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error fetching weekly water usage: " + e.getMessage());
        }
        return weeklyWaterUsage;
    }

    public long addElectricityUsage(double electricityUsage) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("electricity_usage", electricityUsage);
        values.put("date", String.valueOf(System.currentTimeMillis())); // Add timestamp
        return db.insert(TABLE_ELECTRICITY_USAGE, null, values);
    }

    public long addFuelUsage(double fuelUsage) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("fuel_usage", fuelUsage);
        values.put("date", String.valueOf(System.currentTimeMillis())); // Add timestamp
        return db.insert(TABLE_FUEL_USAGE, null, values);
    }

    public long addPlasticUsage(int plasticUsage) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("plastic_usage", plasticUsage);
        values.put("date", String.valueOf(System.currentTimeMillis())); // Add timestamp
        return db.insert(TABLE_PLASTIC_USAGE, null, values);
    }

    public long addUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, user.getName());
        values.put(COLUMN_LAST_NAME, user.getLastName());
        values.put(COLUMN_USERNAME, user.getUsername());
        values.put(COLUMN_EMAIL, user.getEmail());
        values.put(COLUMN_PASSWORD, user.getPassword());
        long result = db.insert(TABLE_USER, null, values);
        db.close();
        return result;
    }

    public User getUserByUsername(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USER, null, COLUMN_USERNAME + "=?",
                new String[]{username}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            User user = new User();
            user.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
            user.setName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)));
            user.setLastName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LAST_NAME)));
            user.setUsername(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME)));
            user.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)));
            user.setPassword(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD)));
            cursor.close();
            return user;
        }

        if (cursor != null) {
            cursor.close();
        }
        return null;
    }

    public User getUserByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USER, null, COLUMN_EMAIL + "=?",
                new String[]{email}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            User user = new User();
            user.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
            user.setName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)));
            user.setLastName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LAST_NAME)));
            user.setUsername(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME)));
            user.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)));
            user.setPassword(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD)));
            cursor.close();
            return user;
        }

        if (cursor != null) {
            cursor.close();
        }
        return null;
    }
}
