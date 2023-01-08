package com.example.weatherforcast.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherforcast.network.model.WeatherData
import com.example.weatherforcast.repository.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class WeatherViewmodel @Inject constructor(
    private val repository: WeatherRepository
): ViewModel() {
    private var _getWeather = MutableLiveData<Response<WeatherData>>()
    val getWeather : LiveData<Response<WeatherData>>
        get() = _getWeather

    fun setWeatherData(units: String, apiKey: String,language: String, cityName: String){
        viewModelScope.launch(IO) {
            try {
                val response = repository.getWeather(apiKey,language,units,cityName)
                _getWeather.postValue(response)
            }catch (ex: Exception){
                Log.d("TAG",ex.message.toString())
            }catch (io: IOException){
                Log.d("TAG",io.message.toString())
            }
        }

    }

    fun getCurrentLocationWeather(latitude: Double, longitude: Double,language:String, apiKey: String, units: String){
        viewModelScope.launch {
            try {
                val response = repository.getWeatherByLocation(latitude,longitude,language,apiKey,units)
                _getWeather.postValue(response)
            }catch (ex: Exception){
                Log.d("ViewModelTAG",ex.message.toString())
            }
        }
    }
}