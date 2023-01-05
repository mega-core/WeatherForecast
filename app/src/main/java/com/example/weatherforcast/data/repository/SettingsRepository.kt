package com.example.weatherforcast.data.repository

import androidx.lifecycle.LiveData
import com.example.weatherforcast.data.query.DarkMode
import com.example.weatherforcast.data.query.LanguageData
import com.example.weatherforcast.data.query.SettingsDao
import com.example.weatherforcast.data.query.Units
import com.example.weatherforcast.data.table.SettingsDB
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepository @Inject constructor(private val settingsDao: SettingsDao) {
    val getDataSettings:LiveData<List<SettingsDB>>? = settingsDao.getAllSettings()

    suspend fun addNewSettings(settingsDB: SettingsDB){
        settingsDao.insertSettings(settingsDB)
    }

    fun updateDarkMode(darkMode: DarkMode){
        settingsDao.updateDarkMode(darkMode)
    }

    fun updateLanguage(languageData: LanguageData){
        settingsDao.updateLanguage(languageData)
    }

    fun updateUserUnit(units: Units){
        settingsDao.updateUserUnit(units)
    }


}