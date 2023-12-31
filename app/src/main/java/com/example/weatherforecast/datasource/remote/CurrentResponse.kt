package com.example.weatherforecast.datasource.remote

import com.example.weatherforecast.datasource.local.WeatherEntity
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class CurrentResponse(
    val lat: Double,
    val lon: Double,
    val timezone: String,

    @SerializedName("timezone_offset")
    val timezoneOffset: Long,

    val current: Current,
    val hourly: List<Current>,
    val daily: List<Daily>,
    val alerts: List<Alert>? = null
) {
    fun fromModelToEntity(latitude : String, longitude :String): WeatherEntity {
        val uid = "${this.lat}${this.lon}"
        return WeatherEntity(
            id = uid,
            lat = latitude,
            lon = longitude,
            timezone = this.timezone,
            timezoneOffset = this.timezoneOffset.toString(),
            current = this.current,
            hourly = this.hourly,
            daily = this.daily
        )
    }
}

@Serializable
data class Alert(
    @SerializedName("sender_name")
    val senderName: String,

    val event: String,
    val start: Long,
    val end: Long,
    val description: String,
    val tags: List<String>
)

@Serializable
data class Current(
    val dt: Long,
    val sunrise: Long? = null,
    val sunset: Long? = null,
    val temp: Double,

    @SerializedName("feels_like")
    val feelsLike: Double,

    val pressure: Long,
    val humidity: Long,

    @SerializedName("dew_point")
    val dewPoint: Double,

    val uvi: Double,
    val clouds: Long,
    val visibility: Long,

    @SerializedName("wind_speed")
    val windSpeed: Double,

    @SerializedName("wind_deg")
    val windDeg: Long,

    val weather: List<Weather>,

    @SerializedName("wind_gust")
    val windGust: Double? = null,

    val pop: Double? = null
)

@Serializable
data class Weather(
    val id: Long,
    val main: String,
    val description: String,
    val icon: String
)

@Serializable
data class Daily(
    val dt: Long,
    val sunrise: Long,
    val sunset: Long,
    val moonrise: Long,
    val moonset: Long,

    @SerializedName("moon_phase")
    val moonPhase: Double,

    val temp: Temp,

    @SerializedName("feels_like")
    val feelsLike: FeelsLike,

    val pressure: Long,
    val humidity: Long,

    @SerializedName("dew_point")
    val dewPoint: Double,

    @SerializedName("wind_speed")
    val windSpeed: Double,

    @SerializedName("wind_deg")
    val windDeg: Long,

    @SerializedName("wind_gust")
    val windGust: Double,

    val weather: List<Weather>,
    val clouds: Long,
    val pop: Double,
    val uvi: Double,
    val rain: Double? = null
)

@Serializable
data class FeelsLike(
    val day: Double,
    val night: Double,
    val eve: Double,
    val morn: Double
)

@Serializable
data class Temp(
    val day: Double,
    val min: Double,
    val max: Double,
    val night: Double,
    val eve: Double,
    val morn: Double
)
