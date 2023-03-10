package com.example.weatherforcast.ui

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
        val arg = arguments?.getString("cityName")
        val circularProgressDrawable = CircularProgressDrawable(requireContext())
        circularProgressDrawable.strokeWidth = 5f
        circularProgressDrawable.centerRadius = 30f
        circularProgressDrawable.start()
        if (arg != null)
            searchWeather(arg.toString(), circularProgressDrawable)
        getCurrentLocationWeatherData(circularProgressDrawable)
        binding.reloadImageButton.setOnClickListener {
            findNavController().navigate(R.id.locationFragment)
            getCurrentLocationWeatherData(circularProgressDrawable)
        }
        if (Constants.argumentCityName != null)
            searchWeather(Constants.argumentCityName!!, circularProgressDrawable)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    @SuppressLint("SetTextI18n")
    private fun getCurrentLocationWeatherData(circularProgressDrawable: CircularProgressDrawable) {
        if (Constants.locationLatitude != 0.0) {
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
                Toast.makeText(context, io.message.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun searchWeather(
        cityName: String,
        circularProgressDrawable: CircularProgressDrawable
    ) {
        try {
            viewModel.setWeatherData(
                Constants.UNITS,
                Constants.API_KEY,
                Constants.LANG,
                cityName
            )
            viewModel(circularProgressDrawable)
            viewModel.getWeather.observe(viewLifecycleOwner) {
                if (it.code() == 404) {
                    Constants.argumentCityName = null
                    Toast.makeText(context, "City not found!", Toast.LENGTH_SHORT).show()
                }
            }

        } catch (io: IOException) {
            Toast.makeText(this.context, io.message.toString(), Toast.LENGTH_SHORT).show()
        } catch (ex: Exception) {
            Toast.makeText(this.context, "", Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun viewModel(circularProgressDrawable: CircularProgressDrawable) {
        viewModel.getWeather.observe(viewLifecycleOwner) {
            if (it.body() != null) {
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
        }
    }


}