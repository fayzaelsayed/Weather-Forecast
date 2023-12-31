package com.example.weatherforecast.ui.splash

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.navigation.fragment.findNavController
import com.example.weatherforecast.R
import com.example.weatherforecast.databinding.FragmentSplashBinding
import com.example.weatherforecast.utils.BaseFragment
import com.example.weatherforecast.utils.GlobalHelper
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import dagger.hilt.android.AndroidEntryPoint
import java.io.IOException
import javax.inject.Inject

@AndroidEntryPoint
class SplashFragment : BaseFragment(false) {
    private lateinit var binding: FragmentSplashBinding
    private lateinit var textAnimation: Animation
    private lateinit var layoutAnimation: Animation
    private lateinit var locationRequest: LocationRequest
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var lat = ""
    private var long = ""
    private var firstVisit = true

    @Inject
    lateinit var globalHelper: GlobalHelper
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSplashBinding.inflate(inflater, container, false)
        textAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.fall_down)
        layoutAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.bottom_to_top)

        Handler().postDelayed({
            binding.ivLogo.visibility = View.VISIBLE
            binding.ivLogo.animation = layoutAnimation
            Handler().postDelayed({
                binding.tvWeather.visibility = View.VISIBLE
                binding.tvForecast.visibility = View.VISIBLE
                binding.tvWeather.animation = textAnimation
                binding.tvForecast.animation = textAnimation
            }, 500)
        }, 1000)


        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())

        lat = globalHelper.getSharedPreferences("lat", "")
        long = globalHelper.getSharedPreferences("long", "")

        if (lat.isNotEmpty() && long.isNotEmpty()) {
            Handler().postDelayed({
                val action = R.id.action_splashFragment_to_homeFragment
                findNavController().navigate(action)
            }, 3000)
        } else {
            checkLocationPermission()
        }



        return binding.root
    }


    private fun checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            checkGPS()
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1
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
            requireActivity().applicationContext
        ).checkLocationSettings(builder.build())

        result.addOnCompleteListener {
            try {
                //on
                getUserLocation()
            } catch (e: ApiException) {
                //off
                e.printStackTrace()
                when (e.statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try {
                        //send request for enable GPS
                        val resolveApiException = e as ResolvableApiException
                        resolveApiException.startResolutionForResult(requireActivity(), 200)
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
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationProviderClient.lastLocation.addOnSuccessListener {
            if (it != null) {
                try {
                    globalHelper.setSharedPreferences("lat", it.latitude.toString())
                    globalHelper.setSharedPreferences("long", it.longitude.toString())


                    findNavController().navigate(R.id.action_splashFragment_to_homeFragment)
                } catch (e: IOException) {
                }
            }
        }
    }

    private fun showDialog() {
        Log.i("ggggggggg", "showDialog: dialog ")
        val dialogLayout = layoutInflater.inflate(R.layout.dialog, null)
        val textView = dialogLayout.findViewById<TextView>(R.id.tv_city_name)
        val saveButton = dialogLayout.findViewById<Button>(R.id.btn_save)
        val cancelButton = dialogLayout.findViewById<Button>(R.id.btn_cancel)

        textView.text = textView.context.resources.getText(R.string.warning)

        val builder = AlertDialog.Builder(requireActivity()).setView(dialogLayout).create()

        saveButton.text = saveButton.context.resources.getString(R.string.ok)
        saveButton.setOnClickListener {
            builder.dismiss()
            val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri = Uri.fromParts("package", requireActivity().packageName, null)
            intent.data = uri
            startActivity(intent)
        }

        cancelButton.setOnClickListener {
            builder.dismiss()
            requireActivity().finish()
        }

        builder.show()
    }

    override fun onResume() {
        super.onResume()
        if (firstVisit) {
            firstVisit = false
        } else {
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                checkGPS()
            } else {
                showDialog()
            }
        }
    }
}