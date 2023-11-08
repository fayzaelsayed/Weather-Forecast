package com.example.weatherforecast.ui.map

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.example.weatherforecast.R
import com.example.weatherforecast.databinding.FragmentMapBinding
import com.example.weatherforecast.datasource.local.FavoritesEntity
import com.example.weatherforecast.datasource.local.WeatherDatabaseDao
import com.example.weatherforecast.utils.BaseFragment
import com.example.weatherforecast.utils.GlobalHelper
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class MapFragment : BaseFragment(false), OnMapReadyCallback {
    private lateinit var binding: FragmentMapBinding
    private lateinit var mapFragment: SupportMapFragment
    private lateinit var googleMap: GoogleMap
    private var formattedLat = ""
    private var formattedLong = ""
    private var sourceFragmentId: Int? = null

    @Inject
    lateinit var globalHelper: GlobalHelper

    @Inject
    lateinit var weatherDatabaseDao: WeatherDatabaseDao


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMapBinding.inflate(inflater, container, false)
        mapFragment =
            childFragmentManager.findFragmentById(R.id.mapView) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val previousBackStackEntry = findNavController().previousBackStackEntry
        sourceFragmentId = previousBackStackEntry?.destination?.id

        return binding.root
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        setMarkerOnMap(map)
    }

    private fun setMarkerOnMap(map: GoogleMap) {
        map.setOnMapLongClickListener { latLng ->
            val snippet = String.format(
                Locale.getDefault(),
                "Lat: %1$.2f, Long: %2$.2f",
                latLng.latitude,
                latLng.longitude
            )
            map.addMarker(MarkerOptions().position(latLng).title("Info").snippet(snippet))

            formattedLat = latLng.latitude.toString()
            formattedLong = latLng.longitude.toString()

            showDialog(latLng.latitude, latLng.longitude)
        }
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

    @SuppressLint("SetTextI18n")
    private fun showDialog(lat: Double, long: Double) {
        val dialogLayout = layoutInflater.inflate(R.layout.dialog, null)
        val textView = dialogLayout.findViewById<TextView>(R.id.tv_city_name)
        val saveButton = dialogLayout.findViewById<Button>(R.id.btn_save)
        val cancelButton = dialogLayout.findViewById<Button>(R.id.btn_cancel)

        textView.text = getCountryName(lat, long) + "," + getCityName(lat, long)

        val builder = AlertDialog.Builder(requireContext()).setView(dialogLayout).create()

        saveButton.setOnClickListener {
            when (sourceFragmentId) {
                R.id.favoritesFragment -> {
                    val favoritesEntity = FavoritesEntity(
                        idF = formattedLat + formattedLat,
                        latF = formattedLat,
                        longF = formattedLong,
                        cityNameF = textView.text.toString()
                    )
                    weatherDatabaseDao.insertFavoriteLocation(favoritesEntity)
                    builder.dismiss()
                    findNavController().navigateUp()
                }
                R.id.homeFragment -> {
                    globalHelper.setSharedPreferences("lat", lat.toString())
                    globalHelper.setSharedPreferences("long", long.toString())
                    builder.dismiss()
                    findNavController().navigateUp()
                }


            }

        }

        cancelButton.setOnClickListener {
            builder.dismiss()
        }

        builder.show()
    }
}
