package com.example.weatherforecast.ui.home

import android.annotation.SuppressLint
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.example.weatherforecast.utils.CommonFunctions.Companion.getCityName
import com.example.weatherforecast.utils.CommonFunctions.Companion.getCountryName
import com.example.weatherforecast.utils.CommonFunctions.Companion.handleTemperature
import com.example.weatherforecast.utils.CommonFunctions.Companion.handleTemperatureUnit
import com.example.weatherforecast.utils.CommonFunctions.Companion.handleWindSpeed
import java.text.SimpleDateFormat
import java.util.*

object BindingAdapters {
    @SuppressLint("SimpleDateFormat", "WeekBasedYear")
    @JvmStatic
    @BindingAdapter("customPattern")
    fun setCustomPattern(textView: TextView, dt: Long) {
        textView.text = SimpleDateFormat("EEE.MMM YYY   h:mm a").format(Date(dt * 1000))
    }

    @SuppressLint("SetTextI18n")
    @JvmStatic
    @BindingAdapter("customLat", "customLong")
    fun setCustomCityName(textView: TextView, lat: String?, longi: String?) {
        textView.text = getCountryName(
            lat?.toDouble(),
            longi?.toDouble(),
            textView.context
        ) + ", " + getCityName(lat?.toDouble(), longi?.toDouble(), textView.context)
    }

    @JvmStatic
    @BindingAdapter("customUnit", "customTemp")
    fun setCustomTemperature(textView: TextView, unit: String? = "Celsius", temp: Double) {
        textView.text = handleTemperature(unit, temp)
    }

    @JvmStatic
    @BindingAdapter("customTextUnit")
    fun setCustomUnit(textView: TextView, unit: String? = "Celsius") {
        textView.text = handleTemperatureUnit(unit, textView)
    }


    @JvmStatic
    @BindingAdapter("customWindUnit", "customWindSpeed")
    fun setCustomWindSpeed(textView: TextView, unit: String?, windSpeed: Double?) {
        textView.text = handleWindSpeed(unit, windSpeed)
    }


}
