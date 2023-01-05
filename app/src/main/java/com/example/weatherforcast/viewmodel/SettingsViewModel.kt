package com.example.weatherforcast.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.weatherforcast.data.query.DarkMode
import com.example.weatherforcast.data.query.LanguageData
import com.example.weatherforcast.data.query.Units
import com.example.weatherforcast.data.repository.SettingsRepository
import com.example.weatherforcast.data.room.AppDatabase
import com.example.weatherforcast.data.table.SettingsDB
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    var allSettings: LiveData<List<SettingsDB>>? = null
    private val repository: SettingsRepository

    init {
        val settingsDao = AppDatabase.getDatabase(application).settingsDao()
        repository = SettingsRepository(settingsDao)
        allSettings = repository.getDataSettings
    }

    fun addNewSettings(settingsDB: SettingsDB) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addNewSettings(settingsDB)
        }
    }

    fun updateUserUnit(units: Units) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateUserUnit(units)
        }
    }

    fun updateDarkMode(darkMode: DarkMode) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateDarkMode(darkMode)
        }
    }

    fun updateLanguage(languageData: LanguageData) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateLanguage(languageData)
        }
    }



}