package com.example.covid19app.frag
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.covid19app.R
import com.example.covid19app.adapter.CountryAdapter
import com.example.covid19app.api.RetrofitInstance
import com.example.covid19app.data.Country
import com.example.covid19app.offlinedata.FileCache
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CountrySearchFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CountryAdapter
    private val gson by lazy { Gson() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_searchcountry, container, false)
        recyclerView = v.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = CountryAdapter(emptyList())
        recyclerView.adapter = adapter

        fetchCountries()
        return v
    }

    private fun fetchCountries() {
        RetrofitInstance.api.getCountries().enqueue(object : Callback<List<Country>> {
            override fun onResponse(call: Call<List<Country>>, response: Response<List<Country>>) {
                if (response.isSuccessful) {
                    val countries = response.body().orEmpty()
                    lifecycleScope.launch(Dispatchers.IO) {
                        FileCache.writeText(requireContext(), "countries.json", gson.toJson(countries))
                    }
                    showCountries(countries)
                } else {
                    loadCountriesFromCacheOrToast()
                }
            }

            override fun onFailure(call: Call<List<Country>>, t: Throwable) {
                loadCountriesFromCacheOrToast()
            }
        })
    }

    private fun loadCountriesFromCacheOrToast() {
        val cached = FileCache.readText(requireContext(), "countries.json")
        if (cached != null) {
            val list = gson.fromJson(cached, Array<Country>::class.java).toList()
            showCountries(list)
        } else {
            Toast.makeText(requireContext(), "Offline & no cache available", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showCountries(list: List<Country>) {
        adapter = CountryAdapter(list)
        recyclerView.adapter = adapter
        // Keep your existing SearchView filtering logic if you have one
    }
}