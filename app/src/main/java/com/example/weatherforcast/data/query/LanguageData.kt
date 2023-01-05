package com.example.weatherforcast.data.query

import androidx.room.PrimaryKey

data class LanguageData(
    @PrimaryKey
    val id: Int = 0,
    val language: String
)
