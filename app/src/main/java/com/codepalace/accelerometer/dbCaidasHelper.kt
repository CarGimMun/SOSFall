package com.codepalace.accelerometer

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class dbCaidasHelper (context: Context): SQLiteOpenHelper(context, DATABASE_NAME,null,
    DATABASE_VERSION) {
    companion object{
        private const val DATABASE_NAME = "dbCaidas.db"
        private const val DATABASE_VERSION ="1"
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
            put(COLUMN_acc_x)
        }
    }
}