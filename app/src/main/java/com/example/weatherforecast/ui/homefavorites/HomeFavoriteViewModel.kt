package com.example.weatherforecast.ui.homefavorites

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.*
import com.example.weatherforecast.datasource.local.WeatherDatabaseDao
import com.example.weatherforecast.datasource.local.WeatherEntity
import com.example.weatherforecast.datasource.remote.ApiService
import com.example.weatherforecast.utils.GlobalHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeFavoriteViewModel @Inject constructor(
    private val apiService: ApiService,
    private val weatherDatabaseDao: WeatherDatabaseDao,
    private val globalHelper: GlobalHelper
) : ViewModel() {
    private var _showProgress = MutableLiveData(true)
    val showProgress: LiveData<Boolean>
        get() = _showProgress


    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SimpleDateFormat", "SuspiciousIndentation")
    fun getWeatherFromServerToDatabase(lat: String, long: String, lang: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = apiService.getCurrentWeather(
                lat, long,
                "06bb2a1666a2ff2793f1f8fb917b6a40", lang
            )
            if (response.isSuccessful) {
                val responseDetails = response.body()

                responseDetails?.let {
                    val uid = "${it.lat}${it.lon}"
                    val entity = WeatherEntity(
                        id = uid,
                        lat = lat,
                        lon = long,
                        timezone = it.timezone,
                        timezoneOffset = it.timezoneOffset.toString(),
                        current = it.current,
                        hourly = it.hourly,
                        daily = it.daily
                    )
                    weatherDatabaseDao.insertWeatherOfLocation(entity)
                    _showProgress.postValue(false)

                }

            } else {
                Log.i("cccc", "getCurrentWeather:${response.body()} ")
                _showProgress.postValue(false)
            }
        }
    }

    fun getWeatherFromDatabase(lat: String, long: String): LiveData<WeatherEntity> {
        return _showProgress.switchMap {
            if (!it) {
                weatherDatabaseDao.getWeatherOfLocationByKey(
                    lat, long
                )
            } else {
                null
            }
        }
    }


}