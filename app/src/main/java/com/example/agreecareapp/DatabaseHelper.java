package com.example.agreecareapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "AgreeCareUserDB.db";
    public static final int DB_VERSION = 3;

    // Users table
    public static final String TABLE_NAME_USERS = "users";
    public static final String COL_1 = "ID";
    public static final String COL_2 = "NAME";
    public static final String COL_3 = "EMAIL";
    public static final String COL_4 = "PASSWORD";
    public static final String COL_5 = "PHONE";
    public static final String COL_6 = "IMAGE_URI";

    // Reminders table
    public static final String TABLE_NAME_REMINDERS = "reminders";
    public static final String REM_COL_1 = "ID";
    public static final String REM_COL_2 = "TEXT";
    public static final String REM_COL_3 = "TIMESTAMP";
    public static final String REM_COL_4 = "USER_ID";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createUsersTable = "CREATE TABLE " + TABLE_NAME_USERS + " (" +
                COL_1 + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_2 + " TEXT NOT NULL, " +
                COL_3 + " TEXT NOT NULL, " +
                COL_4 + " TEXT NOT NULL, " +
                COL_5 + " TEXT, " +
                COL_6 + " TEXT)";
        db.execSQL(createUsersTable);
        Log.d("DB_CREATE", "Users table created: " + createUsersTable);

        String createRemindersTable = "CREATE TABLE " + TABLE_NAME_REMINDERS + " (" +
                REM_COL_1 + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                REM_COL_2 + " TEXT NOT NULL, " +
                REM_COL_3 + " INTEGER NOT NULL, " +
                REM_COL_4 + " TEXT NOT NULL)";
        db.execSQL(createRemindersTable);
        Log.d("DB_CREATE", "Reminders table created: " + createRemindersTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 3) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_REMINDERS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_USERS);
            onCreate(db);
            Log.d("DB_UPGRADE", "Database upgraded from version " + oldVersion + " to " + newVersion);
        }
    }

    // Insert a new user
    public boolean insertUser(String name, String email, String password, String phone, String imageUri) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(COL_2, name);
            values.put(COL_3, email);
            values.put(COL_4, password);
            values.put(COL_5, phone);
            values.put(COL_6, imageUri);

            long result = db.insert(TABLE_NAME_USERS, null, values);
            if (result == -1) {
                Log.e("DB_INSERT", "Insert failed for user: " + email);
                return false;
            } else {
                Log.i("DB_INSERT", "Insert successful, row ID: " + result);
                return true;
            }
        } catch (Exception e) {
            Log.e("DB_ERROR", "Insert Exception: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Insert a new reminder
    public boolean insertReminder(String text, long timestamp, String userId) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(REM_COL_2, text);
            values.put(REM_COL_3, timestamp);
            values.put(REM_COL_4, userId);

            long result = db.insert(TABLE_NAME_REMINDERS, null, values);
            if (result == -1) {
                Log.e("DB_INSERT", "Insert failed for reminder: " + text);
                return false;
            } else {
                Log.i("DB_INSERT", "Reminder inserted, row ID: " + result);
                return true;
            }
        } catch (Exception e) {
            Log.e("DB_ERROR", "Insert Reminder Exception: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Update an existing user by ID
    public boolean updateUser(int id, String name, String email, String phone, String imageUri) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(COL_2, name);
            values.put(COL_3, email);
            values.put(COL_5, phone);
            values.put(COL_6, imageUri);

            int result = db.update(TABLE_NAME_USERS, values, COL_1 + "=?", new String[]{String.valueOf(id)});
            Log.d("DB_UPDATE", "Update result: " + result);
            return result > 0;
        } catch (Exception e) {
            Log.e("DB_ERROR", "Update Exception: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Update password for an existing user by ID
    public boolean updatePassword(int id, String newPassword) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(COL_4, newPassword);

            int result = db.update(TABLE_NAME_USERS, values, COL_1 + "=?", new String[]{String.valueOf(id)});
            Log.d("DB_UPDATE_PASSWORD", "Password update result for ID " + id + ": " + result);
            return result > 0;
        } catch (Exception e) {
            Log.e("DB_ERROR", "Password Update Exception: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Get user by email
    public Cursor getUserByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_NAME_USERS, null, COL_3 + "=?", new String[]{email}, null, null, null);
    }

    // Get user by ID
    public Cursor getUserById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_NAME_USERS, null, COL_1 + "=?", new String[]{String.valueOf(id)}, null, null, null);
    }

    // Check if email exists
    public boolean isEmailExists(String email) {
        Cursor cursor = getUserByEmail(email);
        boolean exists = cursor != null && cursor.getCount() > 0;
        if (cursor != null) cursor.close();
        return exists;
    }

    // Get all reminders for a user
    public Cursor getRemindersByUserId(String userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_NAME_REMINDERS, null, REM_COL_4 + "=?", new String[]{userId}, null, null, REM_COL_3 + " DESC");
    }

    // Get the last inserted user's ID
    public long getLastInsertedUserId() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT last_insert_rowid() FROM " + TABLE_NAME_USERS, null);
        long lastId = -1;
        if (cursor.moveToFirst()) {
            lastId = cursor.getLong(0);
        }
        cursor.close();
        return lastId;
    }

    // Get the last inserted reminder's ID
    public long getLastInsertedReminderId() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT last_insert_rowid() FROM " + TABLE_NAME_REMINDERS, null);
        long lastId = -1;
        if (cursor.moveToFirst()) {
            lastId = cursor.getLong(0);
        }
        cursor.close();
        return lastId;
    }
}