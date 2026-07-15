package com.example.syadinie_app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "SyadinieApp.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Table 1: Users
        db.execSQL("CREATE TABLE users (id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT UNIQUE, password TEXT)");

        // Table 2: Friends
        db.execSQL("CREATE TABLE friends (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, gender TEXT, hp TEXT, email TEXT, " +
                "addr1 TEXT, addr2 TEXT, addr3 TEXT, addr4 TEXT, image BLOB)");

        // Insert a default user for testing
        db.execSQL("INSERT INTO users (username, password) VALUES ('admin', 'admin123')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS users");
        db.execSQL("DROP TABLE IF EXISTS friends");
        onCreate(db);
    }

    // Auth check
    public boolean checkUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE username=? AND password=?", new String[]{username, password});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    // Insert new friend
    public boolean insertFriend(String name, String gender, String hp, String email,
                                String addr1, String addr2, String addr3, String addr4,
                                byte[] image) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("name", name);
        values.put("gender", gender);
        values.put("hp", hp);
        values.put("email", email);
        values.put("addr1", addr1);
        values.put("addr2", addr2);
        values.put("addr3", addr3);
        values.put("addr4", addr4);
        values.put("image", image);

        long result = db.insert("friends", null, values);

        return result != -1;
    }

    public Cursor getStateStatistics() {

        SQLiteDatabase db = this.getReadableDatabase();

        return db.rawQuery(
                "SELECT addr4, COUNT(*) AS total " +
                        "FROM friends " +
                        "GROUP BY addr4 " +
                        "ORDER BY total DESC",
                null);
    }
    public Cursor getGenderStatistics() {

        SQLiteDatabase db = this.getReadableDatabase();

        return db.rawQuery(
                "SELECT gender, COUNT(*) AS total " +
                        "FROM friends " +
                        "GROUP BY gender " +
                        "ORDER BY total DESC",
                null);
    }

}