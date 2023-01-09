package com.example.weatherforcast.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.AnimationDrawable
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.example.weatherforcast.R
import com.example.weatherforcast.core.Constants
import com.example.weatherforcast.data.table.SettingsDB
import com.example.weatherforcast.databinding.ActivityMainBinding
import com.example.weatherforcast.viewmodel.SettingsViewModel
import com.example.weatherforcast.viewmodel.WeatherViewmodel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.AndroidEntryPoint
import java.io.IOException

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModelSettings: SettingsViewModel
    private val viewModel by viewModels<WeatherViewmodel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
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
                if (it[0].darkMode == true && AppCompatDelegate.getDefaultNightMode() != AppCompatDelegate.MODE_NIGHT_NO)
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                if (it[0].darkMode == false && AppCompatDelegate.getDefaultNightMode() != AppCompatDelegate.MODE_NIGHT_YES)
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

            }


        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        getLocation()
        setContentView(binding.root)
        dynamicBackground()


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
                        Constants.locationLatitude = location.latitude
                        Constants.locationLongitude = location.longitude

                        val circularProgressDrawable = CircularProgressDrawable(this)
                        circularProgressDrawable.strokeWidth = 5f
                        circularProgressDrawable.centerRadius = 30f
                        circularProgressDrawable.start()
                        getCurrentLocationWeatherData(circularProgressDrawable)
                        viewModel(circularProgressDrawable)
                    }
                }
            } else {
                Toast.makeText(this, "Please turn on location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }
    }

    private fun getCurrentLocationWeatherData(circularProgressDrawable: CircularProgressDrawable) {
        try {
            viewModel.getCurrentLocationWeather(
                Constants.locationLatitude,
                Constants.locationLongitude,
                Constants.LANG,
                Constants.API_KEY,
                Constants.UNITS
            )
            viewModel(circularProgressDrawable)
        } catch (io: IOException) {
            Toast.makeText(this, io.message.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun viewModel(circularProgressDrawable: CircularProgressDrawable) {

        if (findViewById<TextView>(R.id.imageView) != null) {

            val homeFragmentTemperature: TextView = findViewById(R.id.home_fragment_temperature)
            val cityNameTextView: TextView = findViewById(R.id.city_name_text_view)
            val countryCodeTextView: TextView = findViewById(R.id.country_code_text_view)
            val feelsLikeTextView: TextView = findViewById(R.id.feels_like_text_view)
            val humidityTextView: TextView = findViewById(R.id.humidity_text_view)
            val maxTempTextView: TextView = findViewById(R.id.max_temp_text_view)
            val minTempTextView: TextView = findViewById(R.id.min_temp_text_view)
            val pressureTextView: TextView = findViewById(R.id.pressure_text_view)
            val cloudsTextView: TextView = findViewById(R.id.clouds_text_view)
            val descriptionTypeTextView: TextView = findViewById(R.id.description_type_text_view)
            val imageView: ImageView = findViewById(R.id.imageView)

            viewModel.getWeather.observe(this) {
                if (it.body() != null) {
                    val data = it.body()!!
                    homeFragmentTemperature.text = "${data.main.temp.toInt()}"
                    cityNameTextView.text = data.name
                    countryCodeTextView.text = data.sys.country
                    feelsLikeTextView.text = "Feels Like: ${data.main.feels_like.toInt()}"
                    humidityTextView.text = "Humidity: ${data.main.humidity}%"
                    maxTempTextView.text =
                        "Max Temperature: ${data.main.temp_max.toInt()}"
                    minTempTextView.text =
                        "Min Temperature: ${data.main.temp_min.toInt()}"
                    pressureTextView.text = "Pressure: ${data.main.pressure}"
                    cloudsTextView.text = data.weather[0].main
                    descriptionTypeTextView.text = data.weather[0].description

                    Glide
                        .with(this)
                        .load(Uri.parse("https://openweathermap.org/img/wn/${data.weather[0].icon}@2x.png"))
                        .centerCrop()
                        .placeholder(circularProgressDrawable)
                        .into(imageView)
                }
            }
        }
    }

}