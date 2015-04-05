package com.example.gracelu.myproject1;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Grace.Lu on 2015/3/21.
 */
public class DataBaseHelper extends SQLiteOpenHelper{
    private static DataBaseHelper mInstance;
    private static String mDatabaseNameString = "BugetS";

    public static DataBaseHelper GetInstance(Context context) {
        if (mInstance == null) {
            mInstance = new DataBaseHelper(context);
        }
        return mInstance;
    }

    private DataBaseHelper(Context context) {
        super(context, mDatabaseNameString, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(SqlCommands.createSpendString);
            Log.d("grace", "DB Init.");
        }catch (Exception ex)
        {
            Log.e("grace error",ex.toString());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
