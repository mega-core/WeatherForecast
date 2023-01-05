package com.example.weatherforcast.data.query

import androidx.room.PrimaryKey

data class Units(
    @PrimaryKey
    val id: Int = 0,
    val unit: String
)
