package com.example.lightnovel

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class databaseHelper(context: Context)
    : SQLiteOpenHelper(context, "novels.db", null, 10) { // Tăng version lên 10

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE novels(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                title TEXT,
                author TEXT,
                image INTEGER,
                description TEXT,
                isFavorite INTEGER DEFAULT 0
            )
        """)

        db.execSQL("""
            CREATE TABLE chapters(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                novel_id INTEGER,
                chapter_number INTEGER,
                title TEXT,
                content TEXT,
                FOREIGN KEY(novel_id) REFERENCES novels(id) ON DELETE CASCADE
            )
        """)

        db.execSQL("""
            CREATE TABLE genres(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT UNIQUE
            )
        """)

        db.execSQL("""
            CREATE TABLE novel_genres(
                novel_id INTEGER,
                genre_id INTEGER,
                PRIMARY KEY(novel_id, genre_id),
                FOREIGN KEY(novel_id) REFERENCES novels(id) ON DELETE CASCADE,
                FOREIGN KEY(genre_id) REFERENCES genres(id) ON DELETE CASCADE
            )
        """)

        db.execSQL("""
            CREATE TABLE favorites(
                username TEXT,
                novel_id INTEGER,
                PRIMARY KEY(username, novel_id)
            )
        """)

        db.execSQL("""
            CREATE TABLE read_history(
                username TEXT,
                novel_id INTEGER,
                chapter_number INTEGER DEFAULT 1,
                timestamp DATETIME DEFAULT CURRENT_TIMESTAMP,
                PRIMARY KEY(username, novel_id),
                FOREIGN KEY(novel_id) REFERENCES novels(id) ON DELETE CASCADE
            )
        """)

        db.execSQL("""
            CREATE TABLE comments(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                novel_id INTEGER,
                username TEXT,
                content TEXT,
                timestamp DATETIME DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY(novel_id) REFERENCES novels(id) ON DELETE CASCADE
            )
        """)
        
        seedGenres(db)
    }

    private fun seedGenres(db: SQLiteDatabase) {
        val genres = arrayOf("Hành Động", "Phiêu Lưu", "Lãng Mạn", "Kinh Dị", "Hài Hước", "Tiên Hiệp", "Kiếm Hiệp", "Huyền Huyễn")
        for (genre in genres) {
            val values = ContentValues().apply { put("name", genre) }
            db.insertWithOnConflict("genres", null, values, SQLiteDatabase.CONFLICT_IGNORE)
        }
    }

    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        db.setForeignKeyConstraintsEnabled(true)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 4) {
            db.execSQL("CREATE TABLE IF NOT EXISTS favorites(username TEXT, novel_id INTEGER, PRIMARY KEY(username, novel_id))")
            db.execSQL("CREATE TABLE IF NOT EXISTS genres(id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT UNIQUE)")
            db.execSQL("CREATE TABLE IF NOT EXISTS novel_genres(novel_id INTEGER, genre_id INTEGER, PRIMARY KEY(novel_id, genre_id), FOREIGN KEY(novel_id) REFERENCES novels(id) ON DELETE CASCADE, FOREIGN KEY(genre_id) REFERENCES genres(id) ON DELETE CASCADE)")
            seedGenres(db)
        }
        if (oldVersion < 6) {
            db.execSQL("CREATE TABLE IF NOT EXISTS novel_genres(novel_id INTEGER, genre_id INTEGER, PRIMARY KEY(novel_id, genre_id), FOREIGN KEY(novel_id) REFERENCES novels(id) ON DELETE CASCADE, FOREIGN KEY(genre_id) REFERENCES genres(id) ON DELETE CASCADE)")
            seedGenres(db)
        }
        if (oldVersion < 7) {
            db.execSQL("""
                CREATE TABLE IF NOT EXISTS read_history(
                    username TEXT,
                    novel_id INTEGER,
                    timestamp DATETIME DEFAULT CURRENT_TIMESTAMP,
                    PRIMARY KEY(username, novel_id),
                    FOREIGN KEY(novel_id) REFERENCES novels(id) ON DELETE CASCADE
                )
            """)
        }
        if (oldVersion < 8) {
            db.execSQL("""
                CREATE TABLE IF NOT EXISTS chapters(
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    novel_id INTEGER,
                    chapter_number INTEGER,
                    title TEXT,
                    content TEXT,
                    FOREIGN KEY(novel_id) REFERENCES novels(id) ON DELETE CASCADE
                )
            """)
        }
        if (oldVersion < 9) {
            db.execSQL("""
                CREATE TABLE IF NOT EXISTS comments(
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    novel_id INTEGER,
                    username TEXT,
                    content TEXT,
                    timestamp DATETIME DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY(novel_id) REFERENCES novels(id) ON DELETE CASCADE
                )
            """)
        }
        if (oldVersion < 10) {
            // Thêm cột chapter_number vào read_history nếu chưa có
            try {
                db.execSQL("ALTER TABLE read_history ADD COLUMN chapter_number INTEGER DEFAULT 1")
            } catch (e: Exception) {}
        }
    }

    // --- Comment Operations ---

    fun insertComment(novelId: Int, username: String, content: String): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("novel_id", novelId)
            put("username", username)
            put("content", content)
            put("timestamp", System.currentTimeMillis())
        }
        return db.insert("comments", null, values)
    }

    fun getCommentsForNovel(novelId: Int): ArrayList<Comment> {
        val list = ArrayList<Comment>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM comments WHERE novel_id = ? ORDER BY timestamp DESC", arrayOf(novelId.toString()))
        if (cursor.moveToFirst()) {
            do {
                list.add(Comment(
                    id = cursor.getInt(0),
                    novelId = cursor.getInt(1),
                    username = cursor.getString(2),
                    content = cursor.getString(3),
                    timestamp = cursor.getLong(4)
                ))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return list
    }

    fun deleteComment(id: Int): Int {
        val db = writableDatabase
        return db.delete("comments", "id=?", arrayOf(id.toString()))
    }

    fun updateComment(id: Int, content: String): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("content", content)
        }
        return db.update("comments", values, "id=?", arrayOf(id.toString()))
    }

    // --- Chapter Operations ---

    fun insertChapter(novelId: Int, chapterNumber: Int, title: String, content: String): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("novel_id", novelId)
            put("chapter_number", chapterNumber)
            put("title", title)
            put("content", content)
        }
        return db.insert("chapters", null, values)
    }

    fun updateChapter(chapterId: Int, chapterNumber: Int, title: String, content: String): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("chapter_number", chapterNumber)
            put("title", title)
            put("content", content)
        }
        return db.update("chapters", values, "id=?", arrayOf(chapterId.toString()))
    }

    fun deleteChapter(chapterId: Int): Int {
        val db = writableDatabase
        return db.delete("chapters", "id=?", arrayOf(chapterId.toString()))
    }

    fun getChaptersForNovel(novelId: Int): ArrayList<Chuong> {
        val list = ArrayList<Chuong>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM chapters WHERE novel_id = ? ORDER BY chapter_number ASC", arrayOf(novelId.toString()))
        if (cursor.moveToFirst()) {
            do {
                list.add(Chuong(
                    id = cursor.getInt(0),
                    novelId = cursor.getInt(1),
                    number = cursor.getInt(2),
                    title = cursor.getString(3),
                    content = cursor.getString(4)
                ))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return list
    }

    fun getChapterContent(novelId: Int, chapterNumber: Int): String? {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT content FROM chapters WHERE novel_id = ? AND chapter_number = ?", arrayOf(novelId.toString(), chapterNumber.toString()))
        var content: String? = null
        if (cursor.moveToFirst()) {
            content = cursor.getString(0)
        }
        cursor.close()
        return content
    }

    // --- History Operations ---

    fun addToHistory(username: String, novelId: Int, chapterNumber: Int = 1) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("username", username)
            put("novel_id", novelId)
            put("chapter_number", chapterNumber)
            put("timestamp", System.currentTimeMillis()) 
        }
        db.insertWithOnConflict("read_history", null, values, SQLiteDatabase.CONFLICT_REPLACE)
    }

    fun getLastReadChapter(username: String, novelId: Int): Int {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT chapter_number FROM read_history WHERE username = ? AND novel_id = ?", arrayOf(username, novelId.toString()))
        var chapterNumber = 1
        if (cursor.moveToFirst()) {
            chapterNumber = cursor.getInt(0)
        }
        cursor.close()
        return chapterNumber
    }

    fun getReadHistory(username: String): ArrayList<Truyen> {
        val list = ArrayList<Truyen>()
        val db = readableDatabase
        val query = """
            SELECT n.*, (SELECT COUNT(*) FROM favorites f WHERE f.novel_id = n.id AND f.username = ?) as userFavorite
            FROM novels n
            JOIN read_history rh ON n.id = rh.novel_id
            WHERE rh.username = ?
            ORDER BY rh.timestamp DESC
        """
        val cursor = db.rawQuery(query, arrayOf(username, username))
        if (cursor.moveToFirst()) {
            do {
                list.add(Truyen(
                    id = cursor.getInt(0),
                    title = cursor.getString(1),
                    author = cursor.getString(2),
                    imageRes = cursor.getInt(3),
                    description = cursor.getString(4),
                    isFavorite = cursor.getInt(6) > 0
                ))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return list
    }

    // --- Genre Operations ---

    fun insertGenre(name: String): Long {
        val db = writableDatabase
        val values = ContentValues().apply { put("name", name) }
        return db.insertWithOnConflict("genres", null, values, SQLiteDatabase.CONFLICT_IGNORE)
    }

    fun updateGenre(id: Int, name: String): Int {
        val db = writableDatabase
        val values = ContentValues().apply { put("name", name) }
        return db.update("genres", values, "id=?", arrayOf(id.toString()))
    }

    fun deleteGenre(id: Int): Int {
        val db = writableDatabase
        return db.delete("genres", "id=?", arrayOf(id.toString()))
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

    // --- Novel & Genre Junction Operations ---

    fun setNovelGenres(novelId: Long, genreIds: List<Int>) {
        val db = writableDatabase
        db.delete("novel_genres", "novel_id=?", arrayOf(novelId.toString()))
        for (genreId in genreIds) {
            val values = ContentValues().apply {
                put("novel_id", novelId)
                put("genre_id", genreId)
            }
            db.insert("novel_genres", null, values)
        }
    }

    fun getGenresForNovel(novelId: Int): List<TheLoai> {
        val list = mutableListOf<TheLoai>()
        val db = readableDatabase
        val query = """
            SELECT g.id, g.name 
            FROM genres g
            JOIN novel_genres ng ON g.id = ng.genre_id
            WHERE ng.novel_id = ?
        """
        val cursor = db.rawQuery(query, arrayOf(novelId.toString()))
        if (cursor.moveToFirst()) {
            do {
                list.add(TheLoai(cursor.getInt(0), cursor.getString(1)))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return list
    }

    // --- Novel Operations ---

    fun insertNovel(novel: Truyen, genreIds: List<Int>): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("title", novel.title)
            put("author", novel.author)
            put("image", novel.imageRes)
            put("description", novel.description)
            put("isFavorite", 0)
        }
        val novelId = db.insert("novels", null, values)
        if (novelId != -1L) {
            setNovelGenres(novelId, genreIds)
        }
        return novelId
    }

    fun updateNovel(novel: Truyen, genreIds: List<Int>): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("title", novel.title)
            put("author", novel.author)
            put("image", novel.imageRes)
            put("description", novel.description)
        }
        val result = db.update("novels", values, "id=?", arrayOf(novel.id.toString()))
        setNovelGenres(novel.id.toLong(), genreIds)
        return result
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
                list.add(Truyen(
                    id = cursor.getInt(0),
                    title = cursor.getString(1),
                    author = cursor.getString(2),
                    imageRes = cursor.getInt(3),
                    description = cursor.getString(4),
                    isFavorite = cursor.getInt(6) > 0 
                ))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return list
    }

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
                list.add(Truyen(
                    id = cursor.getInt(0),
                    title = cursor.getString(1),
                    author = cursor.getString(2),
                    imageRes = cursor.getInt(3),
                    description = cursor.getString(4),
                    isFavorite = cursor.getInt(6) > 0 
                ))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return list
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
            db.delete("favorites", "username = ? AND novel_id = ?", arrayOf(username, novelId.toString()))
        }
    }

    fun deleteNovel(id: Int): Int {
        val db = writableDatabase
        db.delete("favorites", "novel_id=?", arrayOf(id.toString()))
        db.delete("novel_genres", "novel_id=?", arrayOf(id.toString()))
        db.delete("read_history", "novel_id=?", arrayOf(id.toString()))
        db.delete("chapters", "novel_id=?", arrayOf(id.toString()))
        db.delete("comments", "novel_id=?", arrayOf(id.toString()))
        return db.delete("novels", "id=?", arrayOf(id.toString()))
    }
}
