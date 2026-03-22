package com.example.lightnovel

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class AccountDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, "accounts.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """
            CREATE TABLE accounts (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                surname TEXT,
                firstName TEXT,
                username TEXT UNIQUE,
                email TEXT UNIQUE,
                phone TEXT,
                dob TEXT,
                gender TEXT,
                password TEXT
            )
        """
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS accounts")
        onCreate(db)
    }

    fun insertAccount(account: Account): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("surname", account.surname)
            put("firstName", account.firstName)
            put("username", account.username)
            put("email", account.email)
            put("phone", account.phone)
            put("dob", account.dob)
            put("gender", account.gender)
            put("password", account.password)
        }
        return db.insert("accounts", null, values)
    }

    fun updateAccount(account: Account, oldUsername: String): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("surname", account.surname)
            put("firstName", account.firstName)
            put("username", account.username)
            put("email", account.email)
            put("phone", account.phone)
            put("dob", account.dob)
            put("gender", account.gender)
            put("password", account.password)
        }
        return db.update("accounts", values, "username=?", arrayOf(oldUsername))
    }

    fun checkUser(username: String, email: String): Boolean {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM accounts WHERE username=? OR email=?",
            arrayOf(username, email)
        )
        val exists = cursor.count > 0
        cursor.close()
        return exists
    }

    fun login(username: String, pass: String): Boolean {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM accounts WHERE username=? AND password=?",
            arrayOf(username, pass)
        )
        val success = cursor.count > 0
        cursor.close()
        return success
    }

    fun getAccountByUsername(username: String): Account? {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM accounts WHERE username=?", arrayOf(username))
        var account: Account? = null
        if (cursor.moveToFirst()) {
            account = Account(
                id = cursor.getInt(0),
                surname = cursor.getString(1),
                firstName = cursor.getString(2),
                username = cursor.getString(3),
                email = cursor.getString(4),
                phone = cursor.getString(5),
                dob = cursor.getString(6),
                gender = cursor.getString(7),
                password = cursor.getString(8)
            )
        }
        cursor.close()
        return account
    }
}
