package com.example.weatherforcast.core

import com.example.weatherforcast.BuildConfig

object Constants {
    const val BASE_URL = "https://api.openweathermap.org/data/2.5/"
    const val API_KEY = BuildConfig.API_KEY
    var NIGHTMODE: Boolean? = true
    var LANG = "en"
    var UNITS = "metric"
    var locationLatitude: Double = 0.0
    var locationLongitude: Double = 0.0
    var argumentCityName: String? = null
}