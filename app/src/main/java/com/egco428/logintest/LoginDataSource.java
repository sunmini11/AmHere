package com.egco428.logintest;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pam on 12/12/2016.
 */
public class LoginDataSource {
    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;
    private String[] allCoumns = {MySQLiteHelper.COLUMN_ID, MySQLiteHelper.COLUMN_USERNAME, MySQLiteHelper.COLUMN_PASSWORD};

    public LoginDataSource(Context context) {
        dbHelper = new MySQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    //Add data to login table
    public LoginMessage createLogin(String user, String pass) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_USERNAME, user);
        values.put(MySQLiteHelper.COLUMN_PASSWORD, pass);

        long insertID = database.insert(MySQLiteHelper.TABLE_LOGIN, null, values);
        Cursor cursor = database.query(MySQLiteHelper.TABLE_LOGIN, allCoumns, MySQLiteHelper.COLUMN_ID + " = " + insertID, null, null, null, null);
        cursor.moveToFirst();
        LoginMessage newComment = cursorToComment(cursor);
        cursor.close();

        return newComment;

    }

    //find user's password from table
    public String findpass(String un) {

        Cursor cursor = database.rawQuery("SELECT * FROM " + MySQLiteHelper.TABLE_LOGIN
                + " WHERE " + MySQLiteHelper.COLUMN_USERNAME + "=" + "'" + un + "'", null);
        cursor.moveToFirst();

        if (cursor.getCount() <= 0) {
            cursor.close();
            return "";
        } else {
            LoginMessage newComment = cursorToComment(cursor);
            String p = newComment.getPassword();
            cursor.close();
            return p;
        }

    }

    //find user's name from table
    public String findUsername(String User) {

        Cursor cursor = database.rawQuery("SELECT * FROM " + MySQLiteHelper.TABLE_LOGIN
                + " WHERE " + MySQLiteHelper.COLUMN_USERNAME + "=" + "'" + User + "'", null);
        cursor.moveToFirst();

        if (cursor.getCount() <= 0) {
            cursor.close();
            return User;
        } else {
            return "";
        }

    }


    // open program and load all comment
    public List<LoginMessage> getAllComments() {
        List<LoginMessage> comments = new ArrayList<LoginMessage>();
        Cursor cursor = database.query(MySQLiteHelper.TABLE_LOGIN, allCoumns, null, null, null, null, null);
        cursor.moveToFirst();
        // get record and save in comment
        while (!cursor.isAfterLast()) {
            LoginMessage comment = cursorToComment(cursor);
            comments.add(comment);
            cursor.moveToNext();
        }

        cursor.close();
        return comments;
    }

    public LoginMessage cursorToComment(Cursor cursor) {
        LoginMessage comment = new LoginMessage();
        comment.setId(cursor.getLong(0)); // 0 = first column
        comment.setUsername(cursor.getString(1));
        comment.setPassword(cursor.getString(2));

        return comment;
    }
}
