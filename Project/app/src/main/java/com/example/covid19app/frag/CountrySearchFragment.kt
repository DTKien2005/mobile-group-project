package com.example.covid19app.frag

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.covid19app.activity.CountryAdapter
import com.example.covid19app.R
import com.example.covid19app.api.RetrofitInstance
import com.example.covid19app.data.Country
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CountrySearchFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CountryAdapter
    private lateinit var searchView: SearchView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Reuse activity_main.xml as the fragment layout
        val view = inflater.inflate(R.layout.fragment_searchcountry, container, false)

        recyclerView = view.findViewById(R.id.recyclerView)
        searchView = view.findViewById(R.id.searchView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        fetchCountries()

        return view
    }

    private fun fetchCountries() {
        RetrofitInstance.api.getCountries().enqueue(object : Callback<List<Country>> {
            override fun onResponse(call: Call<List<Country>>, response: Response<List<Country>>) {
                if (response.isSuccessful) {
                    val countries = response.body() ?: emptyList()
                    adapter = CountryAdapter(countries)
                    recyclerView.adapter = adapter

                    // Enable live search
                    searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                        override fun onQueryTextSubmit(query: String?): Boolean {
                            adapter.filter(query.orEmpty())
                            return true
                        }

                        override fun onQueryTextChange(newText: String?): Boolean {
                            adapter.filter(newText.orEmpty())
                            return true
                        }
                    })
                } else {
                    Toast.makeText(requireContext(), "Không thể tải dữ liệu", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Country>>, t: Throwable) {
                Toast.makeText(requireContext(), "Lỗi tải dữ liệu: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}