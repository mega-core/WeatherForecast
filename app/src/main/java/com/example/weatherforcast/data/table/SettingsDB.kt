package com.example.weatherforcast.data.table

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "settings")
data class SettingsDB(
    @PrimaryKey val id: Int = 0,
    @ColumnInfo(name = "was_dark") val darkMode: Boolean?,
    @ColumnInfo(name = "language") val language: String?,
    @ColumnInfo(name = "unit") val userPreferredUnit: String?
)