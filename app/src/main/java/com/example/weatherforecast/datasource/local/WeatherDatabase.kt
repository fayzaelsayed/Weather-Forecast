package com.example.weatherforecast.datasource.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [WeatherEntity::class, FavoritesEntity::class], version = 1, exportSchema = false)
@TypeConverters(WeatherTypeConverter::class)
abstract class WeatherDatabase : RoomDatabase() {
    abstract val weatherDatabaseDao: WeatherDatabaseDao
}