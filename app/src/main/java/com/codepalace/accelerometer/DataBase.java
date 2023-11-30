package com.codepalace.accelerometer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DataBase extends SQLiteOpenHelper {

    public DataBase(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String qr1="create table USERS(username,email,password)";
        sqLiteDatabase.execSQL(qr1);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
    public void register(String username,String email, String password){
        ContentValues cv= new ContentValues();
        cv.put("username",username);
        cv.put("email",email);
        cv.put("password",password);
        SQLiteDatabase db= getWritableDatabase();
        db.insert("USERS",null,cv);
        db.close();
    }
    public int login(String username,String password){
        int result=0;
        SQLiteDatabase db= getReadableDatabase();
        String str[]= new String[2];
        str[0]=username;
        str[1]=password;
        Cursor c=db.rawQuery("select * from USERS where ?=username and ?=password",str);
        if(c.moveToFirst()){
            result=1;
        }
        return result;
    }
}
