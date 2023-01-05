package com.example.weatherforcast.network.api

import com.example.weatherforcast.network.model.WeatherData
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import javax.inject.Singleton

@Singleton
interface WeatherApi {
    @GET("weather")
    suspend fun getWeatherData(
        @Query("q") cityName: String,
        @Query("appid") API_KEY: String,
        @Query("lang") language: String,
        @Query("units") Units: String
    ):Response<WeatherData>
    @GET("weather")
    suspend fun getWeatherLocation(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("lang") language: String,
        @Query("appid") API_KEY: String,
        @Query("units") Units: String
    ): Response<WeatherData>
}