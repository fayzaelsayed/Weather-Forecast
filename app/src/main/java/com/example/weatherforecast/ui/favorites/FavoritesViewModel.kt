package com.example.weatherforecast.ui.favorites

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherforecast.datasource.local.FavoritesEntity
import com.example.weatherforecast.datasource.local.WeatherDatabaseDao
import com.example.weatherforecast.utils.GlobalHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val weatherDatabaseDao: WeatherDatabaseDao,
    private val globalHelper: GlobalHelper
) : ViewModel() {

    fun getAllFavoritesPlacesFromDatabase() :LiveData<List<FavoritesEntity>>{
        return weatherDatabaseDao.getAllFavoriteLocations()
    }

    fun deleteFavoritePlaceByIdFromDatabase(favoriteEntity: FavoritesEntity){
        viewModelScope.launch(Dispatchers.IO) {
            weatherDatabaseDao.deleteFavoriteLocationById(favoriteEntity)
        }

    }
}