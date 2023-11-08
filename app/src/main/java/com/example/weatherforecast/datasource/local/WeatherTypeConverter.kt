package com.example.weatherforecast.datasource.local

import androidx.room.TypeConverter
import com.example.weatherforecast.datasource.remote.Alert
import com.example.weatherforecast.datasource.remote.Current
import com.example.weatherforecast.datasource.remote.Daily
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class WeatherTypeConverter {

    @TypeConverter
    fun fromCurrentToString(current: Current): String {
        return Json.encodeToString(current)
    }

    @TypeConverter
    fun toCurrentFromString(json: String): Current {
        return Json.decodeFromString(json)
    }

    @TypeConverter
    fun fromHourlyListToString(listOfHourly: List<Current>): String {
        return Json.encodeToString(listOfHourly)
    }

    @TypeConverter
    fun toHourlyListFromString(json: String): List<Current> {
        return Json.decodeFromString(json)
    }

    @TypeConverter
    fun fromDailyListToString(listOfDaily: List<Daily>): String {
        return Json.encodeToString(listOfDaily)
    }

    @TypeConverter
    fun toDailyListFromString(json: String): List<Daily> {
        return Json.decodeFromString(json)
    }

    @TypeConverter
    fun fromAlertListToString(listOfAlert: List<Alert>): String {
        return if (listOfAlert != null) {
            Json.encodeToString(listOfAlert)
        }else{
            ""
        }
    }

    @TypeConverter
    fun toAlertListFromString(json: String): List<Alert> {
        return Json.decodeFromString(json)
    }
}