package com.example.weatherforecast.utils

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.widget.TextView
import com.example.weatherforecast.R
import java.util.*
import kotlin.math.roundToInt


class CommonFunctions {
    companion object {
        fun handleTemperature(unit: String?, temp: Double): String {
            return when (unit) {
                "Celsius" -> {
                    temp.minus(273.15).roundToInt().toString()
                }
                "Fahrenheit" -> {
                    val fahrenheit = (temp - 273.15) * 9 / 5 + 32
                    val roundedFahrenheit = fahrenheit.toInt()
                    "$roundedFahrenheit"
                }
                else -> {
                    temp.roundToInt().toString()
                }
            }
        }

        fun handleTemperatureUnit(unit: String?,textView: TextView): String {
            return when (unit) {
                "Celsius" -> {
                    textView.context.resources.getString(R.string.celsius_text)
                }
                "Fahrenheit" -> {
                    textView.context.resources.getString(R.string.fahrenheit_text)
                }
                else -> {
                    textView.context.resources.getString(R.string.kelvin_text)
                }
            }

        }

        fun handleWindSpeed(unit: String?, windSpeed: Double?): String {
            val mphConversionFactor = 2.23694
            return when (unit) {
                "mile" -> {
                    val speed = windSpeed!! * mphConversionFactor
                    "${speed.roundToInt()} m/h"
                }
                else -> {
                    "${windSpeed?.roundToInt()} m/s"
                }
            }
        }


        fun getCityName(latitude: Double?, longitude: Double?,context: Context): String {
            val geocoder = Geocoder(context, Locale.getDefault())
            var cityName = ""
            try {
                val addresses: MutableList<Address>? = geocoder.getFromLocation(latitude!!, longitude!!, 1)
                if (addresses!!.isNotEmpty()) {
                    cityName = addresses[0].subAdminArea
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return cityName
        }

        fun getCountryName(latitude: Double?, longitude: Double?, context: Context): String {
            val geocoder = Geocoder(context, Locale.getDefault())
            var countryName = ""
            try {
                val addresses: MutableList<Address>? = geocoder.getFromLocation(latitude!!, longitude!!, 1)
                if (addresses!!.isNotEmpty()) {
                    countryName = addresses[0].adminArea
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return countryName
        }
    }





}