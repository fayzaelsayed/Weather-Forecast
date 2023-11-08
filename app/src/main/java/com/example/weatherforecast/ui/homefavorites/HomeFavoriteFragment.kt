package com.example.weatherforecast.ui.homefavorites

import android.annotation.SuppressLint
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weatherforecast.databinding.FragmentHomeFavoriteBinding
import com.example.weatherforecast.datasource.local.WeatherEntity
import com.example.weatherforecast.datasource.remote.Current
import com.example.weatherforecast.datasource.remote.Daily
import com.example.weatherforecast.ui.home.HomeDailyAdapter
import com.example.weatherforecast.ui.home.HomeHourlyAdapter
import com.example.weatherforecast.utils.CommonFunctions
import com.example.weatherforecast.utils.CommonFunctions.Companion.handleTemperature
import com.example.weatherforecast.utils.GlobalHelper
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class HomeFavoriteFragment : Fragment() {
    private lateinit var binding: FragmentHomeFavoriteBinding
    private lateinit var viewModel: HomeFavoriteViewModel
    private lateinit var hourlyWeatherList: MutableList<Current>
    private lateinit var hourlyAdapter: HomeHourlyAdapter
    private lateinit var dailyWeatherList: MutableList<Daily>
    private lateinit var dailyAdapter: HomeDailyAdapter

    private lateinit var args: HomeFavoriteFragmentArgs

    @Inject
    lateinit var globalHelper: GlobalHelper

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeFavoriteBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[HomeFavoriteViewModel::class.java]
        args = HomeFavoriteFragmentArgs.fromBundle(requireArguments())

        hourlyWeatherList = ArrayList()
        dailyWeatherList = ArrayList()

        setUpAdapter()
        binding.swipeRefreshLayout.setOnRefreshListener {
            binding.swipeRefreshLayout.isRefreshing = true
            viewModel.getWeatherFromServerToDatabase(
                args.currentPlace.latF,
                args.currentPlace.longF,
                globalHelper.getSharedPreferences("language", "")
            )


        }

        viewModel.getWeatherFromServerToDatabase(
            args.currentPlace.latF,
            args.currentPlace.longF,
            globalHelper.getSharedPreferences("language", "")
        )


        viewModel.getWeatherFromDatabase(
            args.currentPlace.latF,
            args.currentPlace.longF
        ).observe(viewLifecycleOwner)
        {
            it?.let {
                displayCurrentData(it)

            }
            binding.swipeRefreshLayout.isRefreshing = false

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

    @SuppressLint("SetTextI18n", "SimpleDateFormat", "WeekBasedYear")
    private fun displayCurrentData(it: WeatherEntity) {
        hourlyWeatherList.addAll(it.hourly)
        hourlyAdapter.submitList(hourlyWeatherList)
        dailyWeatherList.addAll(it.daily)
        dailyAdapter.submitList(dailyWeatherList)
        binding.tvCurrentDate.text =
            SimpleDateFormat("EEE.MMM YYY   h:mm a").format(Date(it.current.dt * 1000))
        binding.tvCityName.text = getCityName(
            args.currentPlace.latF.toDouble(),
            args.currentPlace.longF.toDouble()
        ) + ", " + getCountryName(
            args.currentPlace.latF.toDouble(),
            args.currentPlace.longF.toDouble()
        )
        binding.tvTemp.text =
            handleTemperature(
                globalHelper.getSharedPreferences("temperature", ""),
                it.current.temp
            )
        binding.tvTempUnit.text =
            CommonFunctions.handleTemperatureUnit(
                globalHelper.getSharedPreferences(
                    "temperature",
                    ""
                ), binding.tvTempUnit
            )
        binding.tvWindSpeed.text =
            CommonFunctions.handleWindSpeed(
                globalHelper.getSharedPreferences("wind", ""),
                it.current.windSpeed
            )
        binding.tvWeatherDescription.text = it.current.weather[0].description.toString()
        binding.tvHumidity.text = "${it.current.humidity} %"
        binding.tvPressure.text = "${it.current.pressure} hpa"
        binding.tvCloud.text = "${it.current.clouds} %"
        binding.tvUv.text = "${it.current.uvi}"
        binding.tvVisibility.text = "${it.current.visibility} m"
        binding.tvSunrise.text =
            SimpleDateFormat("h a").format(Date(it.current.sunrise!! * 1000))
        binding.tvSunset.text =
            SimpleDateFormat("h a").format(Date(it.current.sunset!! * 1000))

        Glide.with(binding.ivCurrentWeatherDescription)
            .load("https://openweathermap.org/img/wn/${it.current.weather[0].icon}@2x.png")
            .into(binding.ivCurrentWeatherDescription)
        binding.progressBar.visibility = View.GONE
        binding.nsvHome.visibility = View.VISIBLE
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