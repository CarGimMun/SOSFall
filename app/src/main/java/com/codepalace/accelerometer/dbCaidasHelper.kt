package com.codepalace.accelerometer

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class dbCaidasHelper (context: Context): SQLiteOpenHelper(context, DATABASE_NAME,null,
    DATABASE_VERSION) {
    companion object{
        private const val DATABASE_NAME = "dbCaidas.db"
        private const val DATABASE_VERSION =1
        private const val TABLE_NAME ="caidas"
        private const val COLUMN_id= "id"
        private const val COLUMN_acc_x= "acc_x"
        private const val COLUMN_acc_y= "acc_y"
        private const val COLUMN_acc_z= "acc_z"
        private const val COLUMN_minuto= "minuto"
        private const val COLUMN_hora= "hora"
        private const val COLUMN_dia= "dia"
        private const val COLUMN_mes= "mes"
        private const val COLUMN_ano= "ano"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTableQuery = "CREATE TABLE $TABLE_NAME($COLUMN_id INTEGER PRIMARY KEY, $COLUMN_acc_x FLOAT, $COLUMN_acc_y FLOAT," +
                "$COLUMN_acc_z FLOAT,$COLUMN_minuto INTEGER, $COLUMN_hora INTEGER, $COLUMN_dia INTEGER, $COLUMN_mes INTEGER, $COLUMN_ano INTEGER)"
        db?.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        val dropTableQuery="DROP TABLE IF EXISTS $TABLE_NAME"
        db?.execSQL(dropTableQuery)
        onCreate(db)
    }

    fun registroCaidas(caidas: Caidas){
        val db = writableDatabase
        val values=ContentValues().apply {
            put(COLUMN_acc_x,caidas.acc_x)
            put(COLUMN_acc_y,caidas.acc_y)
            put(COLUMN_acc_z,caidas.acc_z)
            put(COLUMN_minuto,caidas.minuto)
            put(COLUMN_acc_y,caidas.hora)
            put(COLUMN_dia,caidas.dia)
            put(COLUMN_mes,caidas.mes)
            put(COLUMN_ano,caidas.ano)
        }
        db.insert(TABLE_NAME, null,values)
        db.close()
    }
    fun get5registros():List<Caidas>{
        val listaCaidas= mutableListOf<Caidas>()
        val db=readableDatabase
        val query="SELECT * FROM $TABLE_NAME LIMIT 5"
        val cursor=db.rawQuery(query,null)
        while(cursor.moveToNext()) {
            val id =cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_id))
            val acc_x =cursor.getFloat(cursor.getColumnIndexOrThrow(COLUMN_acc_x))
            val acc_y =cursor.getFloat(cursor.getColumnIndexOrThrow(COLUMN_acc_y))
            val acc_z =cursor.getFloat(cursor.getColumnIndexOrThrow(COLUMN_acc_z))
            val minuto =cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_minuto))
            val hora =cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_hora))
            val dia =cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_dia))
            val mes =cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_mes))
            val ano =cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ano))

            val caida=Caidas(id,acc_x,acc_y,acc_z,minuto,hora,dia,mes,ano)
            listaCaidas.add(caida)
        }
        cursor.close()
        db.close()
        return listaCaidas
    }
}