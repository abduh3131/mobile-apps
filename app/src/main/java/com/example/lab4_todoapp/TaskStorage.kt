package com.example.lab4_todoapp

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object TaskStorage {

    private const val PREFS_NAME = "todo_prefs"
    private const val KEY_TASKS = "tasks"

    // saves list of tasks into SharedPreferences
    fun saveTasks(context: Context, tasks: List<Task>) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = prefs.edit()
        val json = Gson().toJson(tasks)
        editor.putString(KEY_TASKS, json)
        editor.apply()
    }

    // loads list of tasks from SharedPreferences
    fun loadTasks(context: Context): MutableList<Task> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(KEY_TASKS, null)
        return if (json != null) {
            val type = object : TypeToken<MutableList<Task>>() {}.type
            Gson().fromJson(json, type)
        } else {
            mutableListOf()
        }
    }
}
