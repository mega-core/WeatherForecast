package com.example.weatherforcast.network.model

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.parcelize.Parcelize

@Parcelize
data class Clouds(
    @Json(name = "all")
    val allTypes: Int
):Parcelable