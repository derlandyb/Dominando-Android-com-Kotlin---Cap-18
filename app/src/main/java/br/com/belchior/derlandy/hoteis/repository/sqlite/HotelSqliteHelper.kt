package br.com.belchior.derlandy.hoteis.repository.sqlite

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class HotelSqliteHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(sqliteDatabase: SQLiteDatabase) {
        sqliteDatabase.execSQL(
            "CREATE TABLE $TABLE_HOTEL (" +
                    "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "$COLUMN_NAME TEXT NOT NULL, " +
                    "$COLUMN_ADDRESS TEXT, " +
                    "$COLUMN_RATING REAL)"
        )
    }

    override fun onUpgrade(sqliteDatabase: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }
}