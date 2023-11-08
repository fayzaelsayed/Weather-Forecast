package com.example.weatherforecast.repository

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import com.example.weatherforecast.datasource.local.WeatherDatabaseDao
import com.example.weatherforecast.datasource.remote.ApiService
import com.example.weatherforecast.utils.GlobalHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class Repository @Inject constructor(
    private val apiService: ApiService,
    private val weatherDatabaseDao: WeatherDatabaseDao,
    private val globalHelper: GlobalHelper
) {
    val _showProgress = MutableLiveData(true)


    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SimpleDateFormat", "SuspiciousIndentation")
    suspend fun getWeatherFromServerToDatabase(lang: String) {

        withContext(Dispatchers.IO) {
            try {
                val lat = globalHelper.getSharedPreferences("lat", "")
                val long = globalHelper.getSharedPreferences("long", "")
                val response =
                    apiService.getCurrentWeather(
                        lat,
                        long,
                        "06bb2a1666a2ff2793f1f8fb917b6a40",
                        lang
                    )
                if (response.isSuccessful) {
                    val responseDetails = response.body()

                    responseDetails?.let {
                        val entity = responseDetails.fromModelToEntity(lat, long)
                        weatherDatabaseDao.insertWeatherOfLocation(entity)
                        _showProgress.postValue(false)
                    }

                } else {
                    Log.i("cccc", "getCurrentWeather:${response.body()} ")
                    _showProgress.postValue(false)
                }
            } catch (e: Exception) {
                Log.i("vvvvvvvv", "getWeatherFromServerToDatabase:${e.printStackTrace()} ")
                _showProgress.postValue(false)
            }

        }
    }
}