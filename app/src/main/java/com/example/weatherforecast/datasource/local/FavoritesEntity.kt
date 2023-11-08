package com.example.weatherforecast.datasource.local

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "favorites_table")
data class FavoritesEntity(
    @PrimaryKey(autoGenerate = false)
    val idF: String,
    val latF: String,
    val longF :String,
    val cityNameF: String
) : Parcelable {
    override fun toString(): String {
        return "FavoritesEntity(idF='$idF', latF='$latF', longF='$longF', cityNameF='$cityNameF')"
    }

}
