package com.codepalace.accelerometer

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteDatabase.CursorFactory
import android.database.sqlite.SQLiteOpenHelper

class DataBase(context: Context?, name: String?, factory: CursorFactory?, version: Int) :
    SQLiteOpenHelper(context, name, factory, version) {
    private val  dbR: SQLiteDatabase = readableDatabase
    private val  dbW: SQLiteDatabase = writableDatabase
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
        dbW.insert("USERS", null, cv)
        dbW.close()
    }

    fun login(username: String?, password: String?): Int {
        var result = 0
        val str = arrayOfNulls<String>(2)
        str[0] = username
        str[1] = password
        val c = dbR.rawQuery("select * from USERS where ?=username and ?=password", str)
        if (c.moveToFirst()) {
            result = 1
            c.close()
        }
        return result
    }
    fun getContact(username: String?, password: String?): String{
        var phone:String
        val str = arrayOfNulls<String>(2)
        str[0] = username
        str[1] = password
        val cursor: Cursor=dbR.rawQuery("select contact from USERS where  ?=username and  ?=password ",str)
        cursor.moveToFirst()
        phone=cursor.getString(cursor.getColumnIndex("contact"))
        if( phone==""){
                cursor.close()
                phone="112"
        }
        return phone
    }
    fun registraCaida(accX: Float?, accY: Float?, accZ:Float?,
        hora: Int?, minuto: Int?, dia: Int?, mes: Int?, anio: Int?) {
        val cv = ContentValues()
        cv.put("accX", accX)
        cv.put("accY", accY)
        cv.put("accZ", accZ)
        cv.put("hora", hora)
        cv.put("minuto",minuto)
        cv.put("dia", dia)
        cv.put("mes", mes)
        cv.put("año", anio)
        dbW.insert("FALLS", null, cv)
        dbW.close()
    }

    fun obtenerRegistros(){
        val registros = mutableListOf<String>()
        val db = this.dbR
    }

}