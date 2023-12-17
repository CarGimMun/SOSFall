package com.codepalace.accelerometer

import android.annotation.SuppressLint
import android.content.ContentValues
import android.database.DatabaseUtils
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteDatabase.CursorFactory
import android.database.sqlite.SQLiteOpenHelper
import android.icu.text.DecimalFormat
import android.icu.text.SimpleDateFormat
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import kotlin.collections.ArrayList

class DataBase(context: Context?, name: String?, factory: CursorFactory?, version: Int) :
    SQLiteOpenHelper(context, name, factory, version) {
    private val dbR: SQLiteDatabase = readableDatabase
    private val dbW: SQLiteDatabase = writableDatabase

    override fun onCreate(sqLiteDatabase: SQLiteDatabase) {
        val qr1 =
            "create table IF NOT EXISTS USERS(username,email,contact,password,PRIMARY KEY (username))"
        sqLiteDatabase.execSQL(qr1)
        val qr2 =
            "create table IF NOT EXISTS FALLS(username,accX,accY,accZ,date,time, foreign key(username) references USERS(username))"
        sqLiteDatabase.execSQL(qr2)
    }

    override fun onUpgrade(sqLiteDat: SQLiteDatabase?, oldVers: Int, newVer: Int) {
        val UsersDrop = "DROP TABLE IF EXISTS USERS"
        sqLiteDat?.execSQL(UsersDrop)

        val FallsDrop = "DROP TABLE IF EXISTS FALLS"
        sqLiteDat?.execSQL(FallsDrop)

        if (sqLiteDat != null) {
            onCreate(sqLiteDat)
        }
    }

    fun register(username: String?, email: String?, contact: String?, password: String?) {
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
        }
        c.close()
        return result
    }

    fun getContact(username: String?, password: String?): String {
        var phone: String = ""

        val str = arrayOfNulls<String>(2)
        str[0] = username
        str[1] = password

        val cursor: Cursor =
            dbR.rawQuery("select contact from USERS where ?=username and ?=password", str)
        cursor.moveToFirst()

        phone = cursor.getString(cursor.getColumnIndex("contact"))
        cursor.close()
        if (phone == "") {
            phone = "112"
        }
        return phone
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun registraCaida(username: String?, accX: Float?, accY: Float?, accZ: Float?) {
        val cv = ContentValues()
        val dbW = writableDatabase
        val spanish = Locale("es", "ES")
        val dateFormatter = DateTimeFormatter.ofPattern("dd, MMM yyyy")
        val timeFormatter = DateTimeFormatter.ofPattern("hh:mm:ss ", spanish)
        //val standarDate = formatter.parse(LocalDate.now().format(formatter)) //de string a TemporalAcessor
        val date = LocalDate.now().format(dateFormatter)
        val time = LocalTime.now().format(timeFormatter)

        val formato = DecimalFormat("##.##")
        val accX = formato.format(accX)
        val accY = formato.format(accY)
        val accZ = formato.format(accZ)

        cv.apply {
            put("username", username)
            put("accX", accX)
            put("accY", accY)
            put("accZ", accZ)
            put("date", date)
            put("time", time.toString())
        }
        dbW.insert("FALLS", null, cv)
        dbW.close()
    }

    fun getFalls(atr: String, username: String): ArrayList<String> {
        val listaData = ArrayList<String>()
        val cursor: Cursor
        val col: ArrayList<String>
        col = arrayListOf("username", "accX", "accY", "accZ", "date", "time")

        val str = arrayOfNulls<String>(1)
        str[0] = username

        cursor = dbR.rawQuery(
            "select $atr from FALLS where  username = ?  ORDER BY date DESC, time DESC LIMIT 5",
            str
        )
        try {
            if (cursor.count > 0 && cursor.moveToFirst()) {
                do {
                    var iCol: Int = 0
                     do{
                        // Obtener los datos de cada columna (cambia los índices por los nombres de columnas reales)
                        val data = cursor.getString(iCol)
                        listaData.add(data)
                        iCol++
                        var a=cursor.position
                    }while (iCol < 6)
                }while (cursor.moveToNext() && cursor.isAfterLast()==false)//cursor.isAfterLast()==false

            } else {
                listaData.add("No hay registros")
            }
        } finally {
            cursor.close()
            dbR.close()
        }
        return listaData

    }
    fun validUser(username: String?):Boolean{
        var result:Boolean=true
        val str = arrayOfNulls<String>(1)
        str[0] = username

        val c = dbR.rawQuery("select * from USERS where ?=username" ,str)
        if (c.moveToFirst()) {
            result = false
        }
        c.close()
        return result
    }
}