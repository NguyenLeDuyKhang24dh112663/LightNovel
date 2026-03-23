package com.example.lightnovel

data class TheLoai (
    val id: Int,
    val name: String
) {
    override fun toString(): String = name
}