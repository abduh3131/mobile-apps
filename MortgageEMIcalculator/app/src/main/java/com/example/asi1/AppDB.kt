// app/src/main/java/com/example/asi1/AppDB.kt
package com.example.asi1

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class AppDb(ctx: Context) : SQLiteOpenHelper(ctx, "asi1.db", null, 1) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS expenses (
                _id INTEGER PRIMARY KEY AUTOINCREMENT,
                label TEXT NOT NULL,
                amount REAL NOT NULL,
                recurring INTEGER NOT NULL
            )
            """
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) { }

    fun insertExpense(label: String, amount: Double, recurring: Int) {
        val values = ContentValues().apply {
            put("label", label)
            put("amount", amount)
            put("recurring", recurring)
        }
        writableDatabase.insert("expenses", null, values)
    }

    fun deleteExpense(id: Long) {
        writableDatabase.delete("expenses", "_id=?", arrayOf(id.toString()))
    }

    fun getAllExpenses(): Cursor {
        return readableDatabase.query(
            "expenses",
            arrayOf("_id", "label", "amount", "recurring"),
            null, null, null, null,
            "_id DESC"
        )
    }

    fun computeTotals(): Pair<Double, Double> {
        val c = readableDatabase.rawQuery("SELECT recurring, SUM(amount) FROM expenses GROUP BY recurring", null)
        var recurring = 0.0
        var variable = 0.0
        while (c.moveToNext()) {
            val rec = c.getInt(0)
            val sum = c.getDouble(1)
            if (rec == 1) recurring = sum else variable = sum
        }
        c.close()
        return Pair(recurring, variable)
    }
}
