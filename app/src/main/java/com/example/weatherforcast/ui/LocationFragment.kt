package com.example.weatherforcast.ui

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.example.weatherforcast.R
import com.example.weatherforcast.core.Constants
import com.example.weatherforcast.databinding.FragmentLocationBinding
import com.example.weatherforcast.viewmodel.WeatherViewmodel
import dagger.hilt.android.AndroidEntryPoint
import java.io.IOException

@Suppress("DEPRECATION")
@AndroidEntryPoint
class LocationFragment : Fragment(R.layout.fragment_location) {
    private val viewModel by viewModels<WeatherViewmodel>()
    private var _binding: FragmentLocationBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLocationBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onResume() {
        super.onResume()



        Handler().postDelayed({
            if (Constants.NIGHTMODE != null)
                if (Constants.NIGHTMODE!!)
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                else
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }, 300)

        val circularProgressDrawable = CircularProgressDrawable(requireContext())
        circularProgressDrawable.strokeWidth = 5f
        circularProgressDrawable.centerRadius = 30f
        circularProgressDrawable.start()


        getCurrentLocationWeatherData(circularProgressDrawable)
        binding.reloadImageButton.setOnClickListener {
            findNavController().navigate(R.id.locationFragment)
            getCurrentLocationWeatherData(circularProgressDrawable)
        }


        if (Constants.argumentCityName != null)
            searchWeather(Constants.argumentCityName!!)


    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    @SuppressLint("SetTextI18n")
    private fun getCurrentLocationWeatherData(circularProgressDrawable: CircularProgressDrawable) {
        try {
            viewModel.getCurrentLocationWeather(
                Constants.locationLatitude,
                Constants.locationLongitude,
                Constants.LANG,
                Constants.API_KEY,
                Constants.UNITS
            )
            viewModel.getWeather.observe(viewLifecycleOwner) {
                val data = it.body()!!
                binding.homeFragmentTemperature.text =
                    "${data.main.temp.toInt()}"
                binding.cityNameTextView.text = data.name
                binding.countryCodeTextView.text = data.sys.country
                binding.feelsLikeTextView.text = "Feels Like: ${data.main.feels_like.toInt()}"
                binding.humidityTextView.text = "Humidity: ${data.main.humidity}%"
                binding.maxTempTextView.text =
                    "Max Temperature: ${data.main.temp_max.toInt()}"
                binding.minTempTextView.text =
                    "Min Temperature: ${data.main.temp_min.toInt()}"
                binding.pressureTextView.text = "Pressure: ${data.main.pressure}"
                binding.cloudsTextView.text = data.weather[0].main
                binding.descriptionTypeTextView.text = data.weather[0].description

                Glide
                    .with(this)
                    .load(Uri.parse("https://openweathermap.org/img/wn/${data.weather[0].icon}@2x.png"))
                    .centerCrop()
                    .placeholder(circularProgressDrawable)
                    .into(binding.imageView)


            }
        } catch (io: IOException) {
            Toast.makeText(context, io.message.toString(), Toast.LENGTH_SHORT).show()
        }
    }


    @SuppressLint("SetTextI18n")
    private fun searchWeather(cityName: String) {
        try {

            viewModel.setWeatherData(
                Constants.UNITS,
                Constants.API_KEY,
                Constants.LANG,
                cityName
            )

            viewModel.getWeather.observe(viewLifecycleOwner) {
                val data = it.body()!!
                binding.homeFragmentTemperature.text =
                    "${data.main.temp.toInt()}"
                binding.cityNameTextView.text = data.name
                binding.countryCodeTextView.text = data.sys.country
                binding.feelsLikeTextView.text =
                    "Feels Like: ${data.main.feels_like.toInt()}"
                binding.humidityTextView.text = "Humidity: ${data.main.humidity}%"
                binding.maxTempTextView.text =
                    "Max Temperature: ${data.main.temp_max.toInt()}"
                binding.minTempTextView.text =
                    "Min Temperature: ${data.main.temp_min.toInt()}"
                binding.pressureTextView.text = "Pressure: ${data.main.pressure}"
                binding.cloudsTextView.text = data.weather[0].main
                binding.descriptionTypeTextView.text = data.weather[0].description
            }
        } catch (io: IOException) {
            Toast.makeText(context, io.message.toString(), Toast.LENGTH_SHORT).show()
        }
    }


}