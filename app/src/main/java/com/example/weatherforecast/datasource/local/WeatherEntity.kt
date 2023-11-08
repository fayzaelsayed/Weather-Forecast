package com.example.weatherforecast.datasource.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.weatherforecast.datasource.remote.Current
import com.example.weatherforecast.datasource.remote.Daily
import com.google.gson.annotations.SerializedName


@Entity(tableName = "weather_table")
data class WeatherEntity(
    @PrimaryKey(autoGenerate = false)
    val id: String,
    val lat: String,
    val lon: String,
    val timezone: String,

    @SerializedName("timezone_offset")
    val timezoneOffset: String,

    val current: Current,
    val hourly: List<Current>,
    val daily: List<Daily>
)