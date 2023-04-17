package com.example.fragment.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class MyDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "download.db"; // 数据库名称
    private static final int DATABASE_VERSION = 1; // 数据库版本号
    private static final String TABLE_NAME = "DownloadTable"; // 表名
    public static final String COLUMN_ID = "_id"; // id 列名
    public static final String COLUMN_DOWN_URL = "url"; // downURL 下载数据必须的url
    public static final String COLUMN_AFFAIR = "affair"; // affair 任务名
    public static final String COLUMN_FILENAME = "filename"; // filename 文件名

    public MyDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 创建表结构
        String sql = "CREATE TABLE " + TABLE_NAME + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_DOWN_URL + " TEXT,"
                + COLUMN_AFFAIR + " TEXT,"
                + COLUMN_FILENAME + " TEXT)";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 更新表结构
        String sql = "DROP TABLE IF EXISTS " + TABLE_NAME;
        db.execSQL(sql);
        onCreate(db);
    }

    public void insertData(String downURL, String affair, String filename) {
        // 插入数据
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_DOWN_URL, downURL);
        values.put(COLUMN_AFFAIR, affair);
        values.put(COLUMN_FILENAME, filename);
        db.insert(TABLE_NAME, null, values);
    }

    public Cursor query() {
        // 查询数据
        SQLiteDatabase db = getReadableDatabase();
        String[] columns = {COLUMN_ID, COLUMN_DOWN_URL, COLUMN_AFFAIR, COLUMN_FILENAME};
        return db.query(TABLE_NAME, columns, null, null, null, null, null);
    }

    public void deleteData(int id) {
        // 删除数据
        SQLiteDatabase db = getWritableDatabase();
        String whereClause = COLUMN_ID + "=?";
        String[] whereArgs = {String.valueOf(id)};
        db.delete(TABLE_NAME, whereClause, whereArgs);
    }

}
