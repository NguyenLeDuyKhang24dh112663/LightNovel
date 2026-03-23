package com.example.lightnovel

data class Truyen (
    val id: Int,
    val title: String,
    val author: String,
    val imageRes: Int,
    val description: String?,
    val isFavorite: Boolean = false,
    val genreId: Int = 0 // Thêm ID thể loại
)
