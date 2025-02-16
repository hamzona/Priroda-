package com.example.fitnessapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "fitnessapp.db";
    private static final int DATABASE_VERSION = 2;

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
    public static final String COLUMN_AMOUNT = "amount";
    public static final String COLUMN_DATE = "date";

    private static final String CREATE_TABLE_USER =
            "CREATE TABLE " + TABLE_USER + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_NAME + " TEXT NOT NULL, " +
                    COLUMN_LAST_NAME + " TEXT NOT NULL, " +
                    COLUMN_USERNAME + " TEXT NOT NULL UNIQUE, " +
                    COLUMN_EMAIL + " TEXT NOT NULL UNIQUE, " +
                    COLUMN_PASSWORD + " TEXT NOT NULL);";

    private static final String CREATE_TABLE_USAGE =
            "CREATE TABLE %s (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_AMOUNT + " REAL NOT NULL, " + // ✅ OVO MORA BITI KORIŠTENO U INSERT UPITIMA
                    COLUMN_DATE + " INTEGER NOT NULL);";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(CREATE_TABLE_USER);
            db.execSQL(String.format(CREATE_TABLE_USAGE, TABLE_WATER_USAGE));
            db.execSQL(String.format(CREATE_TABLE_USAGE, TABLE_PLASTIC_USAGE));
            db.execSQL(String.format(CREATE_TABLE_USAGE, TABLE_FUEL_USAGE));
            db.execSQL(String.format(CREATE_TABLE_USAGE, TABLE_ELECTRICITY_USAGE));
            Log.d("DatabaseHelper", "Tables created successfully");
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error creating tables: " + e.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            addAmountColumn(db, TABLE_WATER_USAGE);
            addAmountColumn(db, TABLE_PLASTIC_USAGE);
            addAmountColumn(db, TABLE_FUEL_USAGE);
            addAmountColumn(db, TABLE_ELECTRICITY_USAGE);
        }
    }

    private void addAmountColumn(SQLiteDatabase db, String tableName) {
        Cursor cursor = db.rawQuery("PRAGMA table_info(" + tableName + ")", null);
        boolean columnExists = false;

        while (cursor.moveToNext()) {
            String columnName = cursor.getString(1);
            if (COLUMN_AMOUNT.equals(columnName)) {
                columnExists = true;
                break;
            }
        }
        cursor.close();

        if (!columnExists) {
            try {
                db.execSQL("ALTER TABLE " + tableName + " ADD COLUMN " + COLUMN_AMOUNT + " REAL DEFAULT 0");
                Log.d("DatabaseHelper", "Added column " + COLUMN_AMOUNT + " to " + tableName);
            } catch (Exception e) {
                Log.e("DatabaseHelper", "Error adding column to " + tableName + ": " + e.getMessage());
            }
        }
    }

    public long addUsage(String tableName, double amount) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_AMOUNT, amount);
        values.put(COLUMN_DATE, System.currentTimeMillis() / 1000);
        return db.insert(tableName, null, values);
    }

    public long addUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, user.getName());
        values.put(COLUMN_LAST_NAME, user.getLastName());
        values.put(COLUMN_USERNAME, user.getUsername());
        values.put(COLUMN_EMAIL, user.getEmail());
        values.put(COLUMN_PASSWORD, user.getPassword());

        long result = -1;
        try {
            result = db.insert(TABLE_USER, null, values);
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error inserting user: " + e.getMessage());
        }
        db.close();
        return result;
    }


    public double getWeeklyUsage(String tableName) {
        SQLiteDatabase db = this.getReadableDatabase();
        double usage = 0;

        String query = "SELECT SUM(" + COLUMN_AMOUNT + ") FROM " + tableName +
                " WHERE date >= strftime('%s', 'now', '-7 days')";
        try (Cursor cursor = db.rawQuery(query, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                usage = cursor.getDouble(0);
            }
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error fetching weekly usage from " + tableName + ": " + e.getMessage());
        }
        return usage;
    }

    public long addWaterUsage(double waterUsage) {
        return addUsage(TABLE_WATER_USAGE, waterUsage);
    }

    public long addElectricityUsage(double electricityUsage) {
        return addUsage(TABLE_ELECTRICITY_USAGE, electricityUsage);
    }

    public long addFuelUsage(double fuelUsage) {
        return addUsage(TABLE_FUEL_USAGE, fuelUsage);
    }

    public long addPlasticUsage(int plasticUsage) {
        return addUsage(TABLE_PLASTIC_USAGE, plasticUsage);
    }

    public double getWeeklyWaterUsage() {
        return getWeeklyUsage(TABLE_WATER_USAGE);
    }

    public double getWeeklyElectricityUsage() {
        return getWeeklyUsage(TABLE_ELECTRICITY_USAGE);
    }

    public double getWeeklyFuelUsage() {
        return getWeeklyUsage(TABLE_FUEL_USAGE);
    }

    public User getUserByUsername(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USER, null, COLUMN_USERNAME + "=?",
                new String[]{username}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            User user = extractUserFromCursor(cursor);
            cursor.close();
            return user;
        }
        return null;
    }

    public User getUserByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USER, null, COLUMN_EMAIL + "=?",
                new String[]{email}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            User user = extractUserFromCursor(cursor);
            cursor.close();
            return user;
        }
        return null;
    }

    private User extractUserFromCursor(Cursor cursor) {
        User user = new User();
        user.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
        user.setName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)));
        user.setLastName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LAST_NAME)));
        user.setUsername(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME)));
        user.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)));
        user.setPassword(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD)));
        return user;
    }
}
