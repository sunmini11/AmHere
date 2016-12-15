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
public class ContDataSource {
    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;
    private String[] allCoumns = {MySQLiteHelper.COLUMN_IDCONT ,MySQLiteHelper.COLUMN_USERNAMECONT,MySQLiteHelper.COLUMN_NUMBER,MySQLiteHelper.COLUMN_NAME};

    public ContDataSource(Context context){
        dbHelper = new MySQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close(){
        dbHelper.close();
    }

    public ContMessage createCont(String user,String number,String name){
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_USERNAMECONT,user);
        values.put(MySQLiteHelper.COLUMN_NUMBER,number);
        values.put(MySQLiteHelper.COLUMN_NAME,name);

        long insertID = database.insert(MySQLiteHelper.TABLE_CONT,null,values);
        Cursor cursor = database.query(MySQLiteHelper.TABLE_CONT,allCoumns,MySQLiteHelper.COLUMN_IDCONT+" = "+insertID,null,null,null,null);
        cursor.moveToFirst();
        ContMessage newComment = cursorToComment(cursor);
        cursor.close();

        return newComment;

    }


    // delete first item
    public void deleteComment(ContMessage comment){
        long id = comment.getId();
        System.out.println("Comment deleted with id: "+id);
        database.delete(MySQLiteHelper.TABLE_CONT,MySQLiteHelper.COLUMN_IDCONT+" = "+id,null); // (comment,_id = id,null)
    }


    // open program and load all comment

//    public String findpass(String un){
//
//        Cursor cursor = database.rawQuery("SELECT * FROM " + MySQLiteHelper.TABLE_LOGIN
//                + " WHERE " + MySQLiteHelper.COLUMN_USERNAME + "=" + "'"+un+"'", null);
//        cursor.moveToFirst();
//
//        if(cursor.getCount() <= 0){
//            cursor.close();
//            return "";
//        }
//        else {
//            LoginMessage newComment = cursorToComment(cursor);
//            String p = newComment.getPassword();
//            cursor.close();
//            return p;
//        }
//
//    }

    public List<ContMessage> getAllComments(String un){
        List<ContMessage> comments = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM " + MySQLiteHelper.TABLE_CONT
               + " WHERE " + MySQLiteHelper.COLUMN_USERNAMECONT + "=" + "'"+un+"'", null);;
        cursor.moveToFirst();
        // get record and save in comment
        while(!cursor.isAfterLast()){
            ContMessage comment = cursorToComment(cursor);
            comments.add(comment);
            cursor.moveToNext();
        }

        cursor.close();
        return comments;

    }

    public Cursor findnumber(String un){

        Cursor cursor = database.rawQuery("SELECT * FROM " + MySQLiteHelper.TABLE_CONT
                + " WHERE " + MySQLiteHelper.COLUMN_USERNAMECONT + "=" + "'"+un+"'", null);
        return cursor;

    }

    public ContMessage cursorToComment(Cursor cursor){
        ContMessage comment = new ContMessage();
        comment.setId(cursor.getLong(0)); // 0 = first column
        comment.setUsername(cursor.getString(1));
        comment.setNumber(cursor.getString(2));
        comment.setName(cursor.getString(3));

        return comment;
    }
}
