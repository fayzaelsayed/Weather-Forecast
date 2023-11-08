package com.example.weatherforecast.datasource.local

import androidx.lifecycle.LiveData
import androidx.room.*
import retrofit2.http.GET

@Dao
interface WeatherDatabaseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertWeatherOfLocation(weather: WeatherEntity)

    @Query("SELECT * FROM weather_table WHERE lat =:lat AND lon= :lon")
    fun getWeatherOfLocationByKey(lat: String, lon: String): LiveData<WeatherEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFavoriteLocation(favoritesEntity: FavoritesEntity)

    @Query("SELECT * FROM favorites_table")
    fun getAllFavoriteLocations(): LiveData<List<FavoritesEntity>>

    @Delete
    fun deleteFavoriteLocationById(favoritesEntity: FavoritesEntity)

    @Query("SELECT * FROM favorites_table WHERE latF =:lat AND longF= :lon")
    fun getFavoriteLocationByKey(lat: String, lon: String): LiveData<FavoritesEntity>
}