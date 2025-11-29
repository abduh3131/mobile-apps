package com.example.lab4_todoapp

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class TaskDB(context: Context) : SQLiteOpenHelper(context, NAME, null, VER) {

    // creates tasks table
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE $T_TASKS (
              $C_ID INTEGER PRIMARY KEY AUTOINCREMENT,
              $C_TITLE TEXT NOT NULL,
              $C_DESC TEXT,
              $C_COLOR TEXT NOT NULL DEFAULT '$DEFAULT_COLOR',
              $C_DONE INTEGER NOT NULL DEFAULT 0,
              $C_DATE TEXT NOT NULL DEFAULT '',
              $C_IMAGEURI TEXT
            )
            """.trimIndent()
        )
    }

    // recreates table on version upgrade
    override fun onUpgrade(db: SQLiteDatabase, oldV: Int, newV: Int) {
        db.execSQL("DROP TABLE IF EXISTS $T_TASKS")
        onCreate(db)
    }

    // inserts new task into DB
    fun insert(task: Task): Long {
        val w = writableDatabase
        val v = ContentValues().apply {
            put(C_TITLE, task.title)
            put(C_DESC, task.description)
            put(C_COLOR, task.color)
            put(C_DONE, if (task.isDone) 1 else 0)
            put(C_DATE, task.date)
            put(C_IMAGEURI, task.imageUri)
        }
        return w.insert(T_TASKS, null, v)
    }

    // loads all tasks from DB
    fun all(): MutableList<Task> {
        val list = mutableListOf<Task>()
        val r = readableDatabase
        val c = r.query(
            T_TASKS,
            arrayOf(C_ID, C_TITLE, C_DESC, C_COLOR, C_DONE, C_DATE, C_IMAGEURI),
            null, null, null, null,
            "$C_ID ASC"
        )
        c.use {
            while (it.moveToNext()) {
                val id = it.getLong(it.getColumnIndexOrThrow(C_ID))
                val title = it.getString(it.getColumnIndexOrThrow(C_TITLE))
                val desc = it.getString(it.getColumnIndexOrThrow(C_DESC)) ?: ""
                val color = it.getString(it.getColumnIndexOrThrow(C_COLOR)) ?: "#FFFFFF"
                val done = it.getInt(it.getColumnIndexOrThrow(C_DONE)) == 1
                val date = it.getString(it.getColumnIndexOrThrow(C_DATE)) ?: ""
                val imageUri = it.getString(it.getColumnIndexOrThrow(C_IMAGEURI))
                list.add(
                    Task(
                        id = id,
                        title = title,
                        description = desc,
                        color = color,
                        isDone = done,
                        date = date,
                        imageUri = imageUri
                    )
                )
            }
        }
        return list
    }

    // updates done state only
    fun setDone(id: Long, done: Boolean): Int {
        val v = ContentValues().apply { put(C_DONE, if (done) 1 else 0) }
        return writableDatabase.update(T_TASKS, v, "$C_ID = ?", arrayOf(id.toString()))
    }

    // updates full task entry
    fun update(task: Task): Int {
        val v = ContentValues().apply {
            put(C_TITLE, task.title)
            put(C_DESC, task.description)
            put(C_COLOR, task.color)
            put(C_DONE, if (task.isDone) 1 else 0)
            put(C_DATE, task.date)
            put(C_IMAGEURI, task.imageUri)
        }
        return writableDatabase.update(T_TASKS, v, "$C_ID = ?", arrayOf(task.id.toString()))
    }

    // removes task from DB
    fun delete(id: Long): Int {
        return writableDatabase.delete(T_TASKS, "$C_ID = ?", arrayOf(id.toString()))
    }

    companion object {
        private const val NAME = "todo_lab4.db"
        private const val VER = 4

        const val T_TASKS = "tasks"
        const val C_ID = "id"
        const val C_TITLE = "title"
        const val C_DESC = "description"
        const val C_COLOR = "color"
        const val C_DONE = "is_done"
        const val C_DATE = "date"
        const val C_IMAGEURI = "imageUri"
        private const val DEFAULT_COLOR = "#FFFFFF"
    }

}
