package com.example.lightnovel

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class databaseHelper(context: Context)
    : SQLiteOpenHelper(context, "novels.db", null, 4) { // Version 4
    override fun onCreate(db: SQLiteDatabase) {
        val createNovelsTable = """
        CREATE TABLE novels(
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            title TEXT,
            author TEXT,
            image INTEGER,
            description TEXT,
            isFavorite INTEGER
        )
        """
        db.execSQL(createNovelsTable)

        val createFavoritesTable = """
        CREATE TABLE favorites(
            username TEXT,
            novel_id INTEGER,
            PRIMARY KEY(username, novel_id)
        )
        """
        db.execSQL(createFavoritesTable)

        val createGenresTable = """
        CREATE TABLE genres(
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            name TEXT UNIQUE
        )
        """
        db.execSQL(createGenresTable)

        val createNovelGenresTable = """
        CREATE TABLE novel_genres(
            novel_id INTEGER,
            genre_id INTEGER,
            PRIMARY KEY(novel_id, genre_id),
            FOREIGN KEY(novel_id) REFERENCES novels(id) ON DELETE CASCADE,
            FOREIGN KEY(genre_id) REFERENCES genres(id) ON DELETE CASCADE
        )
        """
        db.execSQL(createNovelGenresTable)

        // Seed some initial genres
        seedGenres(db)
    }

    private fun seedGenres(db: SQLiteDatabase) {
        val genres = arrayOf("Hành Động", "Phiêu Lưu", "Lãng Mạn", "Kinh Dị", "Hài Hước", "Tiên Hiệp", "Kiếm Hiệp", "Huyền Huyễn")
        for (genre in genres) {
            val values = ContentValues().apply { put("name", genre) }
            db.insertWithOnConflict("genres", null, values, SQLiteDatabase.CONFLICT_IGNORE)
        }
    }

    override fun onUpgrade(
        db: SQLiteDatabase,
        oldVersion: Int,
        newVersion: Int
    ) {
        if (oldVersion < 3) {
            db.execSQL("CREATE TABLE IF NOT EXISTS favorites(username TEXT, novel_id INTEGER, PRIMARY KEY(username, novel_id))")
        }
        if (oldVersion < 4) {
            db.execSQL("CREATE TABLE IF NOT EXISTS genres(id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT UNIQUE)")
            db.execSQL("CREATE TABLE IF NOT EXISTS novel_genres(novel_id INTEGER, genre_id INTEGER, PRIMARY KEY(novel_id, genre_id), FOREIGN KEY(novel_id) REFERENCES novels(id) ON DELETE CASCADE, FOREIGN KEY(genre_id) REFERENCES genres(id) ON DELETE CASCADE)")
            seedGenres(db)
        }
    }

    // --- Genre Operations ---

    fun insertGenre(name: String): Long {
        val db = writableDatabase
        val values = ContentValues().apply { put("name", name) }
        return db.insertWithOnConflict("genres", null, values, SQLiteDatabase.CONFLICT_IGNORE)
    }

    fun getAllGenresWithIds(): ArrayList<TheLoai> {
        val list = ArrayList<TheLoai>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM genres", null)
        if (cursor.moveToFirst()) {
            do {
                list.add(TheLoai(cursor.getInt(0), cursor.getString(1)))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return list
    }

    fun getAllGenres(): List<String> {
        val list = mutableListOf<String>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT name FROM genres", null)
        if (cursor.moveToFirst()) {
            do {
                list.add(cursor.getString(0))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return list
    }

    fun updateGenre(id: Int, newName: String): Int {
        val db = writableDatabase
        val values = ContentValues().apply { put("name", newName) }
        return db.update("genres", values, "id=?", arrayOf(id.toString()))
    }

    fun deleteGenre(id: Int): Int {
        val db = writableDatabase
        db.delete("novel_genres", "genre_id=?", arrayOf(id.toString()))
        return db.delete("genres", "id=?", arrayOf(id.toString()))
    }

    // --- Novel Operations ---

    fun getNovelsByGenre(genreName: String, username: String? = null): ArrayList<Truyen> {
        val list = ArrayList<Truyen>()
        val db = readableDatabase
        val query = if (username != null) {
            """
            SELECT n.*, (SELECT COUNT(*) FROM favorites f WHERE f.novel_id = n.id AND f.username = ?) as userFavorite
            FROM novels n
            JOIN novel_genres ng ON n.id = ng.novel_id
            JOIN genres g ON ng.genre_id = g.id
            WHERE g.name = ?
            """
        } else {
            """
            SELECT n.*, 0 as userFavorite
            FROM novels n
            JOIN novel_genres ng ON n.id = ng.novel_id
            JOIN genres g ON ng.genre_id = g.id
            WHERE g.name = ?
            """
        }
        
        val args = if (username != null) arrayOf(username, genreName) else arrayOf(genreName)
        val cursor = db.rawQuery(query, args)
        if (cursor.moveToFirst()) {
            do {
                list.add(
                    Truyen(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getInt(3),
                        cursor.getString(4),
                        cursor.getInt(6) == 1
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        return list
    }

    fun insertNovel(novel: Truyen): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            if (novel.id != 0) put("id", novel.id)
            put("title", novel.title)
            put("author", novel.author)
            put("image", novel.imageRes)
            put("description", novel.description)
            put("isFavorite", 0) 
        }
        return db.insert("novels", null, values)
    }

    fun getAllNovels(username: String? = null): ArrayList<Truyen> {
        val list = ArrayList<Truyen>()
        val db = readableDatabase
        
        val query = if (username != null) {
            """
            SELECT n.*, (SELECT COUNT(*) FROM favorites f WHERE f.novel_id = n.id AND f.username = ?) as userFavorite
            FROM novels n
            """
        } else {
            "SELECT *, 0 as userFavorite FROM novels"
        }

        val cursor = db.rawQuery(query, if (username != null) arrayOf(username) else null)
        if (cursor.moveToFirst()) {
            do {
                list.add(
                    Truyen(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getInt(3),
                        cursor.getString(4),
                        cursor.getInt(6) == 1 
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
            put("description", novel.description)
        }
        return db.update("novels", values, "id=?", arrayOf(novel.id.toString()))
    }

    fun setFavorite(username: String, novelId: Int, isFavorite: Boolean) {
        val db = writableDatabase
        if (isFavorite) {
            val values = ContentValues().apply {
                put("username", username)
                put("novel_id", novelId)
            }
            db.insertWithOnConflict("favorites", null, values, SQLiteDatabase.CONFLICT_IGNORE)
        } else {
            db.delete("favorites", "username=? AND novel_id=?", arrayOf(username, novelId.toString()))
        }
    }

    fun deleteNovel(id: Int): Int {
        val db = writableDatabase
        db.delete("favorites", "novel_id=?", arrayOf(id.toString()))
        db.delete("novel_genres", "novel_id=?", arrayOf(id.toString()))
        return db.delete("novels", "id=?", arrayOf(id.toString()))
    }

}
