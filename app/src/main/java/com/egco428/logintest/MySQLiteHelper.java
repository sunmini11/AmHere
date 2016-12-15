package com.egco428.logintest;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by pam on 12/12/2016.
 */
public class MySQLiteHelper extends SQLiteOpenHelper {

    public static final String TABLE_LOGIN = "login"; // Table's Name
    public static final String COLUMN_ID = "_id"; // column's Name
    public static final String COLUMN_USERNAME = "username"; // column's Name
    public static final String COLUMN_PASSWORD = "password";

    public static final String TABLE_CONT = "contact"; // Table's Name
    public static final String COLUMN_IDCONT = "_id"; // column's Name
    public static final String COLUMN_USERNAMECONT = "username"; // column's Name
    public static final String COLUMN_NUMBER = "phone_number";
    public static final String COLUMN_NAME = "contName";

    private static final String DATABASE_NAME = "login.db"; // file Database Name
    private static final int DATABASE_VERSION = 1;

    // Database creation sql statement
    private static final String CREATE_TABLE_LOGIN = "create table "
            + TABLE_LOGIN + "(" + COLUMN_ID
            + " integer primary key autoincrement, " + COLUMN_USERNAME + " text not null, " + COLUMN_PASSWORD   + " text not null);";

    private static final String CREATE_TABLE_CONT = "create table "
            + TABLE_CONT + "(" + COLUMN_IDCONT
            + " integer primary key autoincrement, " + COLUMN_USERNAMECONT + " text not null, " + COLUMN_NUMBER + " text not null, " + COLUMN_NAME + " text not null);";

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE_LOGIN);
        database.execSQL(CREATE_TABLE_CONT);// execute SQL statement
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(MySQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOGIN);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONT);
        onCreate(db);
    }

}
