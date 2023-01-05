package com.example.weatherforcast.data.query

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.weatherforcast.data.table.SettingsDB


@Dao
interface SettingsDao {
    @Query("select * from settings")
    fun getAllSettings(): LiveData<List<SettingsDB>>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSettings(settingsDB: SettingsDB)


    @Update(entity = SettingsDB::class)
    fun updateDarkMode(vararg was_dark: DarkMode)

    @Update(entity = SettingsDB::class)
    fun updateLanguage(vararg language: LanguageData)

    @Update(entity = SettingsDB::class)
    fun updateUserUnit(vararg unit: Units)
}