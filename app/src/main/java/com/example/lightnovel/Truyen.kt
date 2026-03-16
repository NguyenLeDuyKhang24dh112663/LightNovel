package com.example.lightnovel

data class Truyen (
    val id: Int,
    val title: String,
    val author: String,
    val imageRes: Int,
    val isFavorite: Boolean = false
)
