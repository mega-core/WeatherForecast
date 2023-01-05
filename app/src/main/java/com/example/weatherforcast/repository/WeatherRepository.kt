package com.example.weatherforcast.repository

import com.example.weatherforcast.network.api.WeatherApi
import com.example.weatherforcast.network.model.WeatherData
import retrofit2.Response
import javax.inject.Inject

class WeatherRepository @Inject constructor(
    private val weatherApi: WeatherApi
) {
    suspend fun getWeather(apiKey: String,language: String,units: String, cityName: String):Response<WeatherData> =
        weatherApi.getWeatherData(cityName,apiKey,language,units)
    suspend fun getWeatherByLocation(latitude: Double, longitude: Double, language:String, apiKey: String, units: String): Response<WeatherData> =
        weatherApi.getWeatherLocation(latitude, longitude, language, apiKey, units)
}