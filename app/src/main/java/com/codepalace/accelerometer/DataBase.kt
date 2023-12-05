package com.codepalace.accelerometer

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteDatabase.CursorFactory
import android.database.sqlite.SQLiteOpenHelper
import androidx.core.database.getStringOrNull

class DataBase(context: Context?, name: String?, factory: CursorFactory?, version: Int) :
    SQLiteOpenHelper(context, name, factory, version) {
    val  db_r=readableDatabase
    val db_w=writableDatabase
    override fun onCreate(sqLiteDatabase: SQLiteDatabase) {
        val qr1 = "create table USERS(username,email,contact,password)"
        sqLiteDatabase.execSQL(qr1)
    }

    override fun onUpgrade(sqLiteDatabase: SQLiteDatabase, i: Int, i1: Int) {}
    fun register(username: String?, email: String?,contact:String?, password: String?) {
        val cv = ContentValues()
        cv.put("username", username)
        cv.put("email", email)
        cv.put("contact", contact)
        cv.put("password", password)
        db_w.insert("USERS", null, cv)
        db_w.close()
    }

    fun login(username: String?, password: String?): Int {
        var result = 0
        val str = arrayOfNulls<String>(2)
        str[0] = username
        str[1] = password
        val c = db_r.rawQuery("select * from USERS where ?=username and ?=password", str)
        if (c.moveToFirst()) {
            result = 1
        }
        return result
    }
    fun getContact(username: String?, password: String?): String{
        val sqLiteDatabase: SQLiteDatabase
        var phone:String=""
        val str = arrayOfNulls<String>(2)
        str[0] = username
        str[1] = password
        val cursor: Cursor=db_r.rawQuery("select contact from USERS where  ?=username and  ?=password ",str)
        if(cursor != null){
            val col=cursor.columnCount
            val indexx=cursor.getColumnIndex("contact")
            val rows:Int=cursor.count

            if(cursor.moveToFirst()){
                phone=cursor.getString(cursor.getColumnIndex("contact"))
            }else{
                cursor.close()
                phone="112"
            }
        } else{
            phone="112"
        }

        /*if (cursor.moveTo()) {
            result = cursor.getString(1)
            cursor.close()
        }else{
        result="112"
        }*/
        return phone
    }
}