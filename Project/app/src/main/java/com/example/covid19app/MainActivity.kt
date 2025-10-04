package com.example.covid19app

import android.content.Intent
import android.os.Bundle
import com.example.covid19app.ui.CountryAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Response
import retrofit2.Callback
import android.util.Log
import com.example.covid19app.features.vndashboard.ui.view.VnDashboardActivity



class MainActivity : AppCompatActivity() {

//     private lateinit var recyclerView: RecyclerView
//     private lateinit var adapter: CountryAdapter

//     override fun onCreate(savedInstanceState: Bundle?) {
//         super.onCreate(savedInstanceState)
//         setContentView(R.layout.activity_main)

//         recyclerView = findViewById(R.id.recyclerView)
//         recyclerView.layoutManager = LinearLayoutManager(this)

//         RetrofitClient.api.getCountries().enqueue(object : Callback<List<Country>> {
//             override fun onResponse(call: Call<List<Country>>, response: Response<List<Country>>) {
//                 if (response.isSuccessful) {
//                     val countries = response.body() ?: emptyList()
//                     adapter = CountryAdapter(countries)
//                     recyclerView.adapter = adapter
//                 }
//             }

//             override fun onFailure(call: Call<List<Country>>, t: Throwable) {
//                 Toast.makeText(this@MainActivity, "Lỗi tải dữ liệu", Toast.LENGTH_SHORT).show()
//             }
//         })
    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate called")
        setContentView(R.layout.vndashboard)
        val intent = Intent(this, VnDashboardActivity::class.java)
        startActivity(intent)
        finish()
        Log.d(TAG, "Dashboard button")
    }




    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart called")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume called")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause called")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy called")
    }

    override fun onRestart() {
        super.onRestart()
        Log.d(TAG, "onRestart called")
    }
}
