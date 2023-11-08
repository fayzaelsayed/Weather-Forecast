package com.example.weatherforecast.ui.home

import android.annotation.SuppressLint
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weatherforecast.databinding.FragmentHomeBinding
import com.example.weatherforecast.datasource.local.WeatherEntity
import com.example.weatherforecast.datasource.remote.Current
import com.example.weatherforecast.datasource.remote.Daily
import com.example.weatherforecast.utils.BaseFragment
import com.example.weatherforecast.utils.CommonFunctions.Companion.handleTemperature
import com.example.weatherforecast.utils.CommonFunctions.Companion.handleTemperatureUnit
import com.example.weatherforecast.utils.CommonFunctions.Companion.handleWindSpeed
import com.example.weatherforecast.utils.GlobalHelper
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : BaseFragment(true) {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var viewModel: HomeViewModel
    private lateinit var hourlyWeatherList: MutableList<Current>
    private lateinit var hourlyAdapter: HomeHourlyAdapter
    private lateinit var dailyWeatherList: MutableList<Daily>
    private lateinit var dailyAdapter: HomeDailyAdapter
    private var weatherEntity: WeatherEntity? = null



    @Inject
    lateinit var globalHelper: GlobalHelper

    @SuppressLint("SimpleDateFormat", "WeekBasedYear")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        hourlyWeatherList = ArrayList()
        dailyWeatherList = ArrayList()

        setUpAdapter()

        binding.swipeRefreshLayout.setOnRefreshListener {
            binding.swipeRefreshLayout.isRefreshing = true
            viewModel.getWeatherFromServerToDatabase(
                globalHelper.getSharedPreferences("language", "")
            )
        }

        viewModel.getWeatherFromServerToDatabase(
            globalHelper.getSharedPreferences("language", "")
        )
        viewModel.getWeatherFromDatabase().observe(viewLifecycleOwner) {
            it?.let {

                binding.weather = it
                displayCurrentData(it)
                binding.swipeRefreshLayout.isRefreshing = false
            }
        }



        return binding.root
    }


    private fun setUpAdapter() {
        hourlyAdapter = HomeHourlyAdapter(globalHelper)
        binding.rvTodayHourly.layoutManager =
            LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false)
        binding.rvTodayHourly.adapter = hourlyAdapter
        dailyAdapter = HomeDailyAdapter(globalHelper)
        binding.rvDaily.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        binding.rvDaily.adapter = dailyAdapter

    }


    @SuppressLint("SimpleDateFormat")
    private fun displayCurrentData(it: WeatherEntity) {
        binding.weather = it
        binding.progressBar.visibility = View.GONE
        binding.nsvHome.visibility = View.VISIBLE
        // hourlyWeatherList.addAll(it.hourly)
        val hourlyList = ArrayList<Current>()
        hourlyList.addAll(it.hourly)
        hourlyAdapter.submitList(hourlyList)
        // dailyWeatherList.addAll(it.daily)
        val dailyList = ArrayList<Daily>()
        dailyList.addAll(it.daily)
        dailyAdapter.submitList(dailyList)
        binding.lat = globalHelper.getSharedPreferences("lat", "")
        binding.longi =  globalHelper.getSharedPreferences("long", "")
        binding.unit = globalHelper.getSharedPreferences("temperature", "")
        binding.windUnit = globalHelper.getSharedPreferences("wind", "")
        binding.tvSunrise.text = SimpleDateFormat("h a").format(Date(it.current.sunrise!! * 1000))
        binding.tvSunset.text =  SimpleDateFormat("h a").format(Date(it.current.sunset!! * 1000))
        Glide.with(binding.ivCurrentWeatherDescription)
            .load("https://openweathermap.org/img/wn/${it.current.weather[0].icon}@2x.png")
            .into(binding.ivCurrentWeatherDescription)
    }

    private fun getCityName(latitude: Double, longitude: Double): String {
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        var cityName = ""
        try {
            val addresses: MutableList<Address>? = geocoder.getFromLocation(latitude, longitude, 1)
            if (addresses!!.isNotEmpty()) {
                cityName = addresses[0].subAdminArea
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return cityName
    }

    private fun getCountryName(latitude: Double, longitude: Double): String {
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        var countryName = ""
        try {
            val addresses: MutableList<Address>? = geocoder.getFromLocation(latitude, longitude, 1)
            if (addresses!!.isNotEmpty()) {
                countryName = addresses[0].adminArea
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return countryName
    }

}