package com.example.weatherforecast.ui

import android.Manifest
import android.content.IntentSender
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.example.weatherforecast.R
import com.example.weatherforecast.databinding.ActivityMainBinding
import com.example.weatherforecast.utils.GlobalHelper
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import dagger.hilt.android.AndroidEntryPoint
import java.io.IOException
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var appBarConfig: AppBarConfiguration
    private var destinationName = "home"

    private var isExpanded = false

    private lateinit var locationRequest: LocationRequest
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    @Inject
    lateinit var globalHelper: GlobalHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        val defaultLang = if (Locale.getDefault().language == "ar") {
            "ar"
        } else {
            "en"
        }
        val language = globalHelper.getSharedPreferences("language", defaultLang)
        if (!language.isNullOrEmpty()) {
            setLocale(language)
        }
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(this)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.myNavHostFragment) as NavHostFragment
        navController = navHostFragment.navController

        appBarConfig = AppBarConfiguration(
            setOf(
                R.id.homeFragment,
                R.id.settingsFragment,
                R.id.alertsFragment,
                R.id.favoritesFragment,
                R.id.homeFavoriteFragment
            )
        )
        // Set up the Toolbar
        setSupportActionBar(binding.toolbar)

        binding.fab.setOnClickListener {
            when (destinationName) {
                "home" -> {
                    if (isExpanded) {
                        binding.optionsLayout.visibility = View.GONE
                    } else {
                        binding.optionsLayout.visibility = View.VISIBLE
                    }
                    isExpanded = !isExpanded
                }
                "favorite" -> {
                    navController.navigate(R.id.action_favoritesFragment_to_mapFragment)
                    binding.optionsLayout.visibility = View.INVISIBLE
                }
                "alerts" -> {
                    Toast.makeText(this, "alerts", Toast.LENGTH_LONG).show()
                }
                "settings" -> {
                    Toast.makeText(this, "settings", Toast.LENGTH_LONG).show()
                }
            }
        }

        binding.fabMap.setOnClickListener {
            navController.navigate(R.id.action_homeFragment_to_mapFragment)
            binding.optionsLayout.visibility = View.GONE
        }

        binding.fabCurrentLocation.setOnClickListener {
            checkLocationPermission()
            this.recreate()
        }
        // Set the textView to the toolbar to be the title
        navController.addOnDestinationChangedListener { _, destination, _ ->
            supportActionBar?.setDisplayShowTitleEnabled(false)
            when (destination.id) {
                R.id.homeFragment -> {
                    binding.tvToolbarTitle.text = getString(R.string.home)
                    destinationName = "home"
                    changeFabIcon(R.drawable.location)
                    performFlipAnimation()
                }

                R.id.favoritesFragment -> {
                    binding.tvToolbarTitle.text = getString(R.string.favorites)
                    destinationName = "favorite"
                    changeFabIcon(R.drawable.add)
                    performFlipAnimation()

                }
                R.id.alertsFragment -> {
                    binding.tvToolbarTitle.text = getString(R.string.alerts)
                    destinationName = "alerts"
                    changeFabIcon(R.drawable.add)
                    performFlipAnimation()
                    binding.optionsLayout.visibility = View.GONE
                }
                R.id.settingsFragment -> {
                    binding.tvToolbarTitle.text = getString(R.string.settings)
                    destinationName = "settings"
                    changeFabIcon(R.drawable.location)
                    performFlipAnimation()
                }

            }
        }

        binding.bottomNavigationView.setupWithNavController(navController)
        setupActionBarWithNavController(navController, appBarConfig)
        binding.bottomNavigationView.background = null
        binding.bottomNavigationView.menu.getItem(2).isEnabled = false


    }


    fun isBottomAppBarAndActionBarVisible(isVisible: Boolean) {
        if (isVisible) {
            binding.bottomAppBar.visibility = View.VISIBLE
            binding.fab.visibility = View.VISIBLE
            binding.toolbar.visibility = View.VISIBLE
        } else {
            binding.bottomAppBar.visibility = View.GONE
            binding.fab.visibility = View.GONE
            binding.toolbar.visibility = View.GONE
        }
    }

    private fun changeFabIcon(icon: Int) {
        binding.fab.setImageResource(icon)
    }

    private fun performFlipAnimation() {
        YoYo.with(Techniques.FlipInX)
            .duration(700)
            .interpolate(AccelerateInterpolator())
            .playOn(binding.fab)
    }

    private fun setLocale(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val resources = this.resources
        val configuration = resources.configuration
        configuration.setLocale(locale)
        resources.updateConfiguration(configuration, resources.displayMetrics)
        val layoutDirection = if (languageCode == "ar") {
            View.LAYOUT_DIRECTION_RTL
        } else {
            View.LAYOUT_DIRECTION_LTR
        }
        this.window.decorView.layoutDirection = layoutDirection
        globalHelper.setSharedPreferences("language", languageCode)

    }

    private fun checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            checkGPS()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                100
            )
        }
    }

    private fun checkGPS() {
        locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 5000
        locationRequest.fastestInterval = 2000

        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        builder.setAlwaysShow(true)

        val result = LocationServices.getSettingsClient(
            this.applicationContext
        ).checkLocationSettings(builder.build())

        result.addOnCompleteListener {
            try {
                //on
                val response = it.getResult(
                    ApiException::class.java
                )
                getUserLocation()
            } catch (e: ApiException) {
                //off
                e.printStackTrace()
                when (e.statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try {
                        //send request for enable GPS
                        val resolveApiException = e as ResolvableApiException
                        resolveApiException.startResolutionForResult(this, 200)
                    } catch (sendIntentException: IntentSender.SendIntentException) {

                    }
                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                        //settings is unavailable

                    }
                }
            }
        }
    }

    private fun getUserLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationProviderClient.lastLocation.addOnSuccessListener {
            if (it != null) {
                try {
                    //val geocoder = Geocoder(requireContext(), Locale.getDefault())
                    //val address = geocoder.getFromLocation(it.latitude, it.longitude, 1)
                    globalHelper.setSharedPreferences("lat", it.latitude.toString())
                    globalHelper.setSharedPreferences("long", it.longitude.toString())
                } catch (e: IOException) {
                }
            }
        }
    }
}