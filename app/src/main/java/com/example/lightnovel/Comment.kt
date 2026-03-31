package com.example.lightnovel

import java.text.SimpleDateFormat
import java.util.*

data class Comment(
    val id: Int = 0,
    val novelId: Int = 0,
    val username: String,
    val content: String,
    val timestamp: Long
) {
    fun getFormattedDate(): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
}
