package com.example.covid19app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.covid19app.api.RetrofitInstance
import com.example.covid19app.data.CovidStatsData
import com.example.covid19app.offlinedata.FileCache
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StatsViewModel(application: Application) : AndroidViewModel(application) {

    private val gson = Gson()

    // LiveData to expose stats to the fragment
    private val _stats = MutableLiveData<CovidStatsData>()
    val stats: LiveData<CovidStatsData> get() = _stats

    // Fetch stats from API or cache if offline
    fun loadStats() {
        RetrofitInstance.api.getVietnamStats()
            .enqueue(object : Callback<CovidStatsData> {
                override fun onResponse(call: Call<CovidStatsData>, response: Response<CovidStatsData>) {
                    val stats = response.body()
                    if (stats != null) {
                        // Cache the data when fetched successfully
                        viewModelScope.launch(Dispatchers.IO) {
                            FileCache.writeText(getApplication(), "vn_stats.json", gson.toJson(stats))
                        }
                        _stats.postValue(stats)
                    } else {
                        // If the API returns null, try to load from cache
                        loadFromCache()
                    }
                }

                override fun onFailure(call: Call<CovidStatsData>, t: Throwable) {
                    // On failure, try to load from cache
                    loadFromCache()
                }
            })
    }

    // Load stats from cache
    private fun loadFromCache() {
        val cached = FileCache.readText(getApplication(), "vn_stats.json")
        if (cached != null) {
            val stats = gson.fromJson(cached, CovidStatsData::class.java)
            _stats.postValue(stats)
        }
    }
}
