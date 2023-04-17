package com.example.fragment.Utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class DatabaseManager {
    private static DatabaseManager instance;
    private static MyDatabaseHelper dbHelper;
    private SQLiteDatabase database;

    private DatabaseManager(Context context) {
        dbHelper = new MyDatabaseHelper(context);
    }

    public static synchronized void initialize(Context context) {
        if (instance == null) {
            instance = new DatabaseManager(context);
        }
    }

    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException(DatabaseManager.class.getSimpleName() +
                    " is not initialized, call initialize() method first.");
        }

        return instance;
    }

    public SQLiteDatabase openDatabase() {
        if (database == null || !database.isOpen()) {
            database = dbHelper.getWritableDatabase();
        }

        return database;
    }

    public void closeDatabase() {
        if (database != null && database.isOpen()) {
            database.close();
        }
    }

    // insert, query, delete, update methods...
}
