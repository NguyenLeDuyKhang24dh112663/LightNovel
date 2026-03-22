package com.example.lightnovel

data class Account(
    val id: Int = 0,
    val surname: String,
    val firstName: String,
    val username: String,
    val email: String,
    val phone: String? = null,
    val dob: String,
    val gender: String,
    val password: String
)
