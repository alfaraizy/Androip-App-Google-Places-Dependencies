package com.example.lenovo.nearbylocation;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by LENOVO on 22/06/2018.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";

    public static final String DATABASE_NAME = "Nearby.db";
    public static final String TABLE_NAME = "places_table";
    /*public static final String col_1 = "geometry";
    public static final String col_2 = "name";
    public static final String col_3 = "address";
    public static final String col_4 = "open_hour";
    public static final String col_5 = "photo";*/
    public static final String col_1 = "review";

    public DatabaseHelper(Context context) {
        super(context, TABLE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String createTable = "CREATE TABLE " + TABLE_NAME + "("+ col_1 + " PRIMARYKEY)";
        //        col_2 + " TEXT," +
        //        col_3 + " TEXT," +
        //        col_4 + " TEXT," +
        //        col_5 + " TEXT," +
        //        col_6 + " TEXT)";

        sqLiteDatabase.execSQL(createTable);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);

    }

    public boolean addData(String item){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(col_1, item);

        Log.d(TAG, "addData: Adding " + item + " to "+ TABLE_NAME);
        long result = db.insert(TABLE_NAME, null, contentValues);

        //if date as inserted incorrectly it will return -1
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }


        public Cursor getData(){
            SQLiteDatabase db = this.getWritableDatabase();
            String query = "SELECT * FROM " + TABLE_NAME;
            Cursor data = db.rawQuery(query, null);
            return data;
        }

    }



