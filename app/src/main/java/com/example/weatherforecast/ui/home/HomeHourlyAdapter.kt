package com.example.weatherforecast.ui.home

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weatherforecast.databinding.WeatherHourlyItemBinding
import com.example.weatherforecast.datasource.remote.Current
import com.example.weatherforecast.utils.CommonFunctions.Companion.handleTemperature
import com.example.weatherforecast.utils.GlobalHelper
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


class HomeHourlyAdapter @Inject constructor(private val globalHelper: GlobalHelper) :
    ListAdapter<Current, HomeHourlyAdapter.WeatherViewHolder>(DiffCallback()) {

    inner class WeatherViewHolder(private val binding: WeatherHourlyItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SimpleDateFormat")
        fun bind(current: Current) {
            binding.apply {

                tvTemperatureHourly.text = handleTemperature(
                    globalHelper.getSharedPreferences("temperature", ""),
                    current.temp
                )
                tvDayHourly.text = SimpleDateFormat("h a").format(Date(current.dt * 1000))
                Glide.with(ivIconHourly)
                    .load("https://openweathermap.org/img/wn/${current.weather[0].icon}@2x.png")
                    .into(ivIconHourly)
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Current>() {
        override fun areItemsTheSame(oldItem: Current, newItem: Current): Boolean {
            return oldItem.dt == newItem.dt
        }

        override fun areContentsTheSame(oldItem: Current, newItem: Current): Boolean {
            return oldItem == newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherViewHolder {
        val binding =
            WeatherHourlyItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WeatherViewHolder(binding)
    }


    override fun onBindViewHolder(holder: WeatherViewHolder, position: Int) {
        val currentItem = getItem(position)
        currentItem?.let {
            holder.bind(currentItem)
        }
    }


}