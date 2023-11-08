package com.example.weatherforecast.ui.home

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weatherforecast.R
import com.example.weatherforecast.databinding.WeatherDailyItemBinding
import com.example.weatherforecast.datasource.remote.Daily
import com.example.weatherforecast.utils.CommonFunctions.Companion.handleTemperature
import com.example.weatherforecast.utils.CommonFunctions.Companion.handleTemperatureUnit
import com.example.weatherforecast.utils.GlobalHelper
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class HomeDailyAdapter @Inject constructor(private val globalHelper: GlobalHelper) :
    ListAdapter<Daily, HomeDailyAdapter.DailyViewHolder>(DiffCallback()) {

    inner class DailyViewHolder(private val binding: WeatherDailyItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SimpleDateFormat", "SetTextI18n")
        fun bind(daily: Daily, position: Int) {
            binding.apply {
                if (position == 0) {
                    tvDayDaily.text = tvDayDaily.context.resources.getString(R.string.today)
                } else {
                    tvDayDaily.text = SimpleDateFormat("EEE").format(Date(daily.dt * 1000))
                }

                tvTemperatureDaily.text = handleTemperature(
                    globalHelper.getSharedPreferences("temperature", ""),
                    daily.temp.min
                ) + "/" + handleTemperature(
                    globalHelper.getSharedPreferences("temperature", ""),
                    daily.temp.max
                ) + handleTemperatureUnit(globalHelper.getSharedPreferences("temperature", ""), tvTemperatureDaily)

                tvDailyWeatherDescription.text = daily.weather[0].description
                Glide.with(ivIconDaily).load("https://openweathermap.org/img/wn/${daily.weather[0].icon}@2x.png").into(ivIconDaily)
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Daily>() {
        override fun areItemsTheSame(oldItem: Daily, newItem: Daily): Boolean {
            return oldItem.dt == newItem.dt
        }

        override fun areContentsTheSame(oldItem: Daily, newItem: Daily): Boolean {
            return oldItem == newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyViewHolder {
        val binding =
            WeatherDailyItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DailyViewHolder(binding)
    }


    override fun onBindViewHolder(holder: DailyViewHolder, position: Int) {
        val currentItem = getItem(position)
        currentItem?.let {
            holder.bind(currentItem, position)
        }
    }


}