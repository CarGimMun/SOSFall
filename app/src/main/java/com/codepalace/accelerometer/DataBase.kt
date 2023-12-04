package com.codepalace.accelerometer

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteDatabase.CursorFactory
import android.database.sqlite.SQLiteOpenHelper
import android.text.Editable
import android.widget.Toast
import java.text.NumberFormat

class DataBase(context: Context?, name: String?, factory: CursorFactory?, version: Int) :
    SQLiteOpenHelper(context, name, factory, version) {
    override fun onCreate(sqLiteDatabase: SQLiteDatabase) {
        val qr1 = "create table USERS(username,email,contact,password)"
        sqLiteDatabase.execSQL(qr1)
    }

    override fun onUpgrade(sqLiteDatabase: SQLiteDatabase, i: Int, i1: Int) {}
    fun register(username: String?, email: String?, contact:Int?, password: String?) {
        val cv = ContentValues()
        cv.put("username", username)
        cv.put("email", email)
        cv.put("contact", contact)
        cv.put("password", password)

        val db = writableDatabase
        db.insert("USERS", null, cv)
        db.close()
    }

    fun login(username: String?, password: String?): Int {
        var result = 0
        val db = readableDatabase
        val str = arrayOfNulls<String>(2)
        str[0] = username
        str[1] = password
        val c = db.rawQuery("select * from USERS where ?=username and ?=password", str)
        if (c.moveToFirst()) {
            result = 1
        }
        return result
    }
}