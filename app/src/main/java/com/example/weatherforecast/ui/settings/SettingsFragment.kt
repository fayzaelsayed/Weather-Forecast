package com.example.weatherforecast.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.weatherforecast.R
import com.example.weatherforecast.databinding.FragmentSettingsBinding
import com.example.weatherforecast.utils.GlobalHelper
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : Fragment() {
    private lateinit var binding: FragmentSettingsBinding

    @Inject
    lateinit var globalHelper: GlobalHelper
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)

        binding.cvSettingsLanguage.setBackgroundResource(R.drawable.gradient_background_cards)
        binding.cvSettingsTemperature.setBackgroundResource(R.drawable.gradient_background_cards)
        binding.cvSettingsWindSpeed.setBackgroundResource(R.drawable.gradient_background_cards)


//        if (globalHelper.getSharedPreferences("location", "") != null) {
//            if (globalHelper.getSharedPreferences("location", "") == "GPS") {
//                binding.rbGps.isChecked = true
//            } else if (globalHelper.getSharedPreferences("location", "") == "Map") {
//                binding.rbMap.isChecked = true
//            }
//        }

        val defaultLang = Locale.getDefault().language
        if (defaultLang == "ar") {
            binding.rbArabic.isChecked = true
        } else if (defaultLang == "en") {
            binding.rbEnglish.isChecked = true
        }

        if (globalHelper.getSharedPreferences("language", "") == "en") {
            binding.rbEnglish.isChecked = true

        } else if (globalHelper.getSharedPreferences("language", "") == "ar") {
            binding.rbArabic.isChecked = true
        }




        if (globalHelper.getSharedPreferences("temperature", "") == "Celsius") {
            binding.rbCelsius.isChecked = true
        } else if (globalHelper.getSharedPreferences("temperature", "") == "Kelvin") {
            binding.rbKelvin.isChecked = true
        } else if (globalHelper.getSharedPreferences("temperature", "") == "Fahrenheit") {
            binding.rbFahrenheit.isChecked = true
        }


        if (globalHelper.getSharedPreferences("wind", "") == "meter") {
            binding.rbMeterPerSec.isChecked = true
        } else if (globalHelper.getSharedPreferences("wind", "") == "mile") {
            binding.rbMilePerHour.isChecked = true
        }


//        binding.rgLocation.setOnCheckedChangeListener { _, id ->
//            when (id) {
//                R.id.rb_gps ->globalHelper.setSharedPreferences("location", "GPS")
//                R.id.rb_map -> {
//                globalHelper.setSharedPreferences("location", "Map")
//                findNavController().navigate(R.id.action_settingsFragment_to_mapFragment)
//                }
//            }
//        }
//        binding.apply {
//            rbMap.setOnClickListener {
//                globalHelper.setSharedPreferences("location", "Map")
//                findNavController().navigate(R.id.action_settingsFragment_to_mapFragment)
//            }
//            rbGps.setOnClickListener {
//                globalHelper.setSharedPreferences("location", "GPS")
//            }
//        }

        binding.rgLanguage.setOnCheckedChangeListener { _, id ->
            when (id) {
                R.id.rb_english -> {
                    setLocale("en")
                }
                R.id.rb_arabic -> {
                    setLocale("ar")
                }
            }
        }



        binding.rgTemperature.setOnCheckedChangeListener { _, id ->
            when (id) {
                R.id.rb_celsius -> globalHelper.setSharedPreferences("temperature", "Celsius")
                R.id.rb_kelvin -> globalHelper.setSharedPreferences("temperature", "Kelvin")
                R.id.rb_fahrenheit -> globalHelper.setSharedPreferences("temperature", "Fahrenheit")
            }
        }

        binding.rgWindSpeed.setOnCheckedChangeListener { _, id ->
            when (id) {
                R.id.rb_meter_per_sec -> globalHelper.setSharedPreferences("wind", "meter")
                R.id.rb_mile_per_hour -> globalHelper.setSharedPreferences("wind", "mile")
            }
        }




        return binding.root
    }

    private fun setLocale(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val resources = requireActivity().resources
        val configuration = resources.configuration
        configuration.setLocale(locale)
        resources.updateConfiguration(configuration, resources.displayMetrics)
        val layoutDirection = if (languageCode == "ar") {
            View.LAYOUT_DIRECTION_RTL
        } else {
            View.LAYOUT_DIRECTION_LTR
        }
        requireActivity().window.decorView.layoutDirection = layoutDirection
        globalHelper.setSharedPreferences("language", languageCode)
        requireActivity().recreate()
    }
//    private fun checkSharedPreferences(key: String, value:String, default:String){
//        when (globalHelper.getSharedPreferences(key, default)) {
//            value -> binding.rbEnglish.isChecked = true
//        } else if (globalHelper.getSharedPreferences("language", "") == "ar") {
//            binding.rbArabic.isChecked = true
//        }
//    }
}