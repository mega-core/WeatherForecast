package com.example.weatherforcast.data.query

import androidx.room.PrimaryKey


data class DarkMode(
    @PrimaryKey
    val id: Int = 0,
    val was_dark: Boolean
)
