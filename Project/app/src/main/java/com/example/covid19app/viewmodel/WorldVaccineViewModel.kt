package com.example.covid19app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.covid19app.api.RetrofitInstance
import com.example.covid19app.offlinedata.FileCache
import com.example.covid19app.data.WorldData
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WorldVaccineViewModel(application: Application) : AndroidViewModel(application) {

    private val gson = Gson()
    val worldVaccineData: MutableLiveData<Map<String, Long>> = MutableLiveData()
    val errorMessage: MutableLiveData<String> = MutableLiveData()

    fun loadWorldVaccineData() {
        RetrofitInstance.api.getWorldVaccineCoverage("30", false)
            .enqueue(object : Callback<Map<String, Long>> {
                override fun onResponse(
                    call: Call<Map<String, Long>>,
                    response: Response<Map<String, Long>>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        val data = response.body()!!
                        saveToCache(data)
                        worldVaccineData.postValue(data)
                    } else {
                        loadFromCache()
                    }
                }

                override fun onFailure(call: Call<Map<String, Long>>, t: Throwable) {
                    loadFromCache()
                }
            })
    }

    private fun saveToCache(data: Map<String, Long>) {
        viewModelScope.launch(Dispatchers.IO) {
            FileCache.writeText(getApplication(), "world_vaccine.json", gson.toJson(data))
        }
    }

    private fun loadFromCache() {
        val cached = FileCache.readText(getApplication(), "world_vaccine.json")
        if (cached != null) {
            val type = object : com.google.gson.reflect.TypeToken<Map<String, Double>>() {}.type
            val mapDouble: Map<String, Double> = gson.fromJson(cached, type)
            val mapLong: Map<String, Long> = mapDouble.mapValues { it.value.toLong() }
            worldVaccineData.postValue(mapLong)
        } else {
            errorMessage.postValue("Offline & no cached data")
        }
    }
}
