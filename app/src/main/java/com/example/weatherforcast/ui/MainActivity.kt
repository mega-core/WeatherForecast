package com.example.weatherforcast.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.AnimationDrawable
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS
import android.widget.Toast
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import com.example.weatherforcast.R
import com.example.weatherforcast.core.Constants
import com.example.weatherforcast.core.Constants.locationLatitude
import com.example.weatherforcast.core.Constants.locationLongitude
import com.example.weatherforcast.data.table.SettingsDB
import com.example.weatherforcast.databinding.ActivityMainBinding
import com.example.weatherforcast.viewmodel.SettingsViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var viewModelSettings: SettingsViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)



        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        getLocation()
        dynamicBackground()

        viewModelSettings = ViewModelProvider(this)[SettingsViewModel::class.java]


        viewModelSettings.allSettings?.observe(this) {
            it.ifEmpty {
                viewModelSettings.addNewSettings(
                    SettingsDB(
                        darkMode = true,
                        language = "en",
                        userPreferredUnit = "metric"
                    )
                )

            }
            if (it.isNotEmpty()) {
                Constants.NIGHTMODE = it[0].darkMode
                Constants.LANG = it[0].language.toString()
                Constants.UNITS = it[0].userPreferredUnit.toString()
            }


        }




        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController


        binding.bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.locationFragmentMenu -> {
                    navController.navigate(R.id.locationFragment)
                }
                R.id.searchFragmentMenu -> {
                    navController.navigate(R.id.searchFragment)
                }
                R.id.settingsFragmentMenu -> {
                    navController.navigate(R.id.settingsFragment)
                }
            }
            true

        }

    }



     
    private fun dynamicBackground() {
        val constraintLayout: CoordinatorLayout = findViewById(R.id.main_layout)
        val animationDrawable: AnimationDrawable = constraintLayout.background as AnimationDrawable
        animationDrawable.setEnterFadeDuration(3000)
        animationDrawable.setExitFadeDuration(2000)
        animationDrawable.start()
    }

    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            103
        )
    }

    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == 103) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLocation()
            }
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    @SuppressLint("MissingPermission", "SetTextI18n")
    private fun getLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                fusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
                    val location: Location? = task.result
                    if (location != null) {
                        binding.apply {
                            locationLatitude = location.latitude
                            locationLongitude = location.longitude
                        }
                    }
                }
            } else {
                Toast.makeText(this, "Please turn on location", Toast.LENGTH_LONG).show()
                val intent = Intent(ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }
    }
}