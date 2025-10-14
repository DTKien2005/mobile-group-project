package com.example.covid19app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.covid19app.api.RetrofitInstance
import com.example.covid19app.data.VaccineResponseData
import com.example.covid19app.offlinedata.FileCache
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class VietnamVaccineViewModel(application: Application) : AndroidViewModel(application) {

    private val gson = Gson()
    private val _vaccineData = MutableLiveData<VaccineResponseData>()
    val vaccineData: LiveData<VaccineResponseData> get() = _vaccineData
    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun loadVietnamVaccineData() {
        RetrofitInstance.api.getVietnamVaccineCoverage("30", false)
            .enqueue(object : Callback<VaccineResponseData> {
                override fun onResponse(
                    call: Call<VaccineResponseData>,
                    response: Response<VaccineResponseData>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        val data = response.body()!!
                        viewModelScope.launch(Dispatchers.IO) {
                            FileCache.writeText(getApplication(), "vn_vaccine.json", gson.toJson(data))
                        }
                        _vaccineData.postValue(data)
                    } else {
                        loadFromCache()
                    }
                }

                override fun onFailure(call: Call<VaccineResponseData>, t: Throwable) {
                    loadFromCache()
                }
            })
    }

    private fun loadFromCache() {
        val cached = FileCache.readText(getApplication(), "vn_vaccine.json")
        if (cached != null) {
            val obj = gson.fromJson(cached, VaccineResponseData::class.java)
            _vaccineData.postValue(obj)
        } else {
            _errorMessage.postValue("Offline & no cached data")
        }
    }
}

