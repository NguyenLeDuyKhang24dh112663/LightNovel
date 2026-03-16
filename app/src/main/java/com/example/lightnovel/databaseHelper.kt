package com.example.lightnovel

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class databaseHelper(context: Context)
    : SQLiteOpenHelper(context, "novels.db", null, 1) {
    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """
        CREATE TABLE novels(
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            title TEXT,
            author TEXT,
            image INTEGER,
            isFavorite INTEGER
        )
        """
        db.execSQL(createTable)
    }

    override fun onUpgrade(
        db: SQLiteDatabase,
        oldVersion: Int,
        newVersion: Int
    ) {
        db.execSQL("DROP TABLE IF EXISTS novels")
        onCreate(db)
    }

    fun insertNovel(novel: Truyen): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            if (novel.id != 0) put("id", novel.id)
            put("title", novel.title)
            put("author", novel.author)
            put("image", novel.imageRes)
            put("isFavorite", if (novel.isFavorite) 1 else 0)
        }
        return db.insert("novels", null, values)
    }

    fun getAllNovels(): ArrayList<Truyen> {
        val list = ArrayList<Truyen>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM novels", null)
        if (cursor.moveToFirst()) {
            do {
                list.add(
                    Truyen(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getInt(3),
                        cursor.getInt(4) == 1
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        return list
    }

    fun updateNovels(novel: Truyen): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("title", novel.title)
            put("author", novel.author)
            put("image", novel.imageRes)
            put("isFavorite", if (novel.isFavorite) 1 else 0)
        }
        return db.update("novels", values, "id=?", arrayOf(novel.id.toString()))
    }

    fun deleteNovel(id: Int): Int {
        val db = writableDatabase
        return db.delete("novels", "id=?", arrayOf(id.toString()))
    }

}