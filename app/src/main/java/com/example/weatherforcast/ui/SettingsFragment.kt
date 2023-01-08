package com.example.weatherforcast.ui

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.weatherforcast.R
import com.example.weatherforcast.data.query.DarkMode
import com.example.weatherforcast.data.query.LanguageData
import com.example.weatherforcast.data.query.Units
import com.example.weatherforcast.databinding.FragmentSettingsBinding
import com.example.weatherforcast.viewmodel.SettingsViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment : Fragment(R.layout.fragment_settings) {
    private lateinit var viewModel: SettingsViewModel
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        viewModel = ViewModelProvider(this)[SettingsViewModel::class.java]


        binding.Units.setOnClickListener {
            MaterialAlertDialogBuilder(view.context)
                .setItems(R.array.units_types) { _, i ->
                    when (i) {
                        0 -> {
                            viewModel.updateUserUnit(Units(unit = "imperial"))

                        }
                        1 -> {
                            viewModel.updateUserUnit(Units(unit = "metric"))
                        }
                        2 -> {
                            viewModel.updateUserUnit(Units(unit = "standard"))
                        }
                    }
                }
                .show()
        }


        binding.Language.setOnClickListener {
            val dialogLanguage = MaterialAlertDialogBuilder(view.context)
                .setView(R.layout.dialog_language_layout)
                .show()
            val languageList = resources.getStringArray(R.array.language)
            val adapter = ArrayAdapter(view.context, R.layout.exposed_dropdown, languageList)
            val languageCode = resources.getStringArray(R.array.languageCode)
            val languageListView = dialogLanguage.findViewById(R.id.language_list_view) as ListView?
            languageListView?.adapter = adapter
            languageListView?.setOnItemClickListener { adapterView, view, i, _ ->
                val language = adapterView.getItemAtPosition(i)
                val item = languageCode[i]
                Toast.makeText(view.context, "$language selected", Toast.LENGTH_SHORT).show()
                viewModel.updateLanguage(LanguageData(language = item))
                dialogLanguage.dismiss()
            }
        }

    }


    override fun onResume() {
        super.onResume()


        binding.darkModeLayoutButton.setOnClickListener {
            if (!isDarkMode()) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                viewModel.updateDarkMode(DarkMode(was_dark = true))
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                viewModel.updateDarkMode(DarkMode(was_dark = false))
            }

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun isDarkMode(): Boolean {
        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return currentNightMode == Configuration.UI_MODE_NIGHT_YES
    }
}