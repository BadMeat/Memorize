package com.dolan.user.memorize;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Created by Badmeat on 05/03/2018.
 */

public class MyDbManager extends SQLiteOpenHelper {

    private static int VERSION = 7;
    private static String DBNAME = "memory";
    private static String TABLE_NAME = "content";
    private static String COL_ID = "idR";
    private static String COL_KODE = "id";
    private static String COL_DESKRIPSI = "deskripsi";
    private static String COL_TANGGAL = "tanggal";
    private static String COL_BULAN = "bulan";
    private static String COL_LIKED = "liked";

    public MyDbManager(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DBNAME, factory, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_NAME + "(" + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COL_KODE + " TEXT ," + COL_DESKRIPSI + " TEXT," + COL_TANGGAL + " TEXT," + COL_BULAN + " TEXT," + COL_LIKED + " INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public boolean AddData(Content e) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_KODE, e.getKode());
        values.put(COL_DESKRIPSI, e.getDeskripsi());
        values.put(COL_TANGGAL, e.getTanggal());
        values.put(COL_LIKED, e.getLiked());
        long result = database.insert(TABLE_NAME, null, values);
        if (result == -1) {
            return false;
        }
        return true;
    }

    public Cursor ShowTanggal(String bulan) {
        SQLiteDatabase database = this.getWritableDatabase();
        return database.rawQuery("SELECT " + COL_TANGGAL + " FROM " + DBNAME + " WHERE " + COL_BULAN + " = '" + bulan + "'", null);
    }

    public Cursor ShowData(String tanggal) {
        SQLiteDatabase database = this.getWritableDatabase();
        return database.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COL_TANGGAL + " = '" + tanggal + "'", null);
    }

    public Cursor ShowDataAll() {
        SQLiteDatabase database = this.getWritableDatabase();
        return database.rawQuery("SELECT * FROM " + TABLE_NAME + " ORDER BY " + COL_ID + " DESC", null);
    }

    public List<Content> GetContent(String des) {
        SQLiteDatabase database = this.getWritableDatabase();
        List<Content> contentList = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COL_DESKRIPSI + " LIKE '%" + des + "%'", null);
        while (cursor.moveToNext()) {
            Content content = new Content();
            content.setKode(cursor.getString(1));
            content.setDeskripsi(cursor.getString(2));
            content.setTanggal(cursor.getString(3));
            content.setLiked(Integer.valueOf(cursor.getString(5)));
            contentList.add(content);
        }
        return contentList;
    }

    public Integer DeleteData(Content e) {
        SQLiteDatabase database = this.getWritableDatabase();
        return database.delete(TABLE_NAME, "ID = ?", new String[]{e.getKode()});
    }

    public Boolean UpdateData(Content e) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_DESKRIPSI, e.getDeskripsi());
        values.put(COL_LIKED, e.getLiked());
        Log.d(TAG, "Des: " + e.getDeskripsi());
        Log.d(TAG, "Liked: " + e.getLiked());
        Log.d(TAG, "Kode: " + e.getKode());
        int result = database.update(TABLE_NAME, values, COL_KODE + " = ?", new String[]{e.getKode()});
        if (result > 0) {
            return true;
        }
        return false;
    }
}
