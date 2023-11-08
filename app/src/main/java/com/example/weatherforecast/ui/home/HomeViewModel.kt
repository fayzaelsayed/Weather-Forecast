package com.example.weatherforecast.ui.home

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.*
import com.example.weatherforecast.datasource.local.WeatherDatabaseDao
import com.example.weatherforecast.datasource.local.WeatherEntity
import com.example.weatherforecast.datasource.remote.ApiService
import com.example.weatherforecast.repository.Repository
import com.example.weatherforecast.utils.GlobalHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val apiService: ApiService,
    private val weatherDatabaseDao: WeatherDatabaseDao,
    private val globalHelper: GlobalHelper,
    private val repository: Repository
) : ViewModel() {
    val showProgress: LiveData<Boolean> = repository._showProgress


    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SimpleDateFormat", "SuspiciousIndentation")
    fun getWeatherFromServerToDatabase(lang: String) {
        viewModelScope.launch {
            repository.getWeatherFromServerToDatabase(lang)
        }
    }

    fun getWeatherFromDatabase(): LiveData<WeatherEntity> {
        return showProgress.switchMap {
            if (!it) {
                weatherDatabaseDao.getWeatherOfLocationByKey(
                    globalHelper.getSharedPreferences("lat", ""),
                    globalHelper.getSharedPreferences("long", "")
                )
            } else {
                null
            }
        }
    }


}