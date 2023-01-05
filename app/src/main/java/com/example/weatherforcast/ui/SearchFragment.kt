package com.example.weatherforcast.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.weatherforcast.R
import com.example.weatherforcast.core.Constants
import com.example.weatherforcast.databinding.FragmentSearchBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment: Fragment(R.layout.fragment_search) {
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    lateinit var listAdapter: ArrayAdapter<String>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater,container,false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val countryList = resources.getStringArray(R.array.countryNames)
        listAdapter = ArrayAdapter(requireContext(),R.layout.exposed_dropdown,countryList)
        binding.listView.adapter = listAdapter
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (countryList.contains(query)){
                    listAdapter.filter.filter(query)
                }else{
                    Toast.makeText(requireContext(), "No Match found ...", Toast.LENGTH_SHORT).show()
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                listAdapter.filter.filter(newText)
                return false
            }
        })
        binding.listView.setOnItemClickListener { adapterView, _, i, _ ->
            val item = adapterView.getItemAtPosition(i)
            Constants.argumentCityName = item.toString()
            findNavController().navigate(R.id.locationFragment)
        }

    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}