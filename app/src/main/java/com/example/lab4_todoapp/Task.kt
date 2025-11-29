package com.example.lab4_todoapp

// simple data class that stores all task info
data class Task(
    val id: Long? = null,
    val title: String,
    val description: String,
    val color: String = "#FFFFFF",
    val isDone: Boolean = false,
    val date: String = "",
    val imageUri: String? = null
)
