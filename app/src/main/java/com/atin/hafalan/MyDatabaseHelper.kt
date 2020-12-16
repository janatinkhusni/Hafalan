package com.atin.hafalan

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import org.jetbrains.anko.db.*

//TODO Buat dan atur kelas MyDatabaseHelper

class MyDatabaseHelper(context: Context) : ManagedSQLiteOpenHelper(context, "database_surat.db", null, 1) {

    companion object {
        private var instance: MyDatabaseHelper? = null

        fun getInstance(context: Context): MyDatabaseHelper {
            if (instance == null) {
                instance = MyDatabaseHelper(context)
            }
            return instance as MyDatabaseHelper
        }
    }

    override fun onCreate(db: SQLiteDatabase?) {
        //Buat tabel pada database
        db?.createTable(
            SuratContract.TABLE_SURAT,
            true,
            SuratContract.ID to INTEGER + PRIMARY_KEY + AUTOINCREMENT,
            SuratContract.NO to TEXT,
            SuratContract.NAMA to TEXT,
            SuratContract.ASMA to TEXT,
            SuratContract.AYAT to TEXT,
            SuratContract.ARTI to TEXT,
            SuratContract.PLAY to BLOB,
            SuratContract.DOWNLOAD to BLOB
        )
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.dropTable(SuratContract.TABLE_SURAT, true)
    }
}

val Context.database: MyDatabaseHelper
    get() = MyDatabaseHelper.getInstance(applicationContext)