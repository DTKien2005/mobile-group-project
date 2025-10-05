package com.example.covid19app

import android.content.Intent
import android.os.Bundle
import android.util.Log
// import androidx.activity.ComponentActivity
// import androidx.activity.compose.setContent
// import androidx.activity.enableEdgeToEdge
// import androidx.compose.foundation.layout.fillMaxSize
// import androidx.compose.ui.Modifier
// import com.example.covid19app.ui.theme.Covid19AppTheme
// import androidx.navigation.compose.rememberNavController
// import androidx.navigation.compose.NavHost
// import androidx.navigation.compose.composable

import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//         Log.d(TAG, "onCreate(Bundle?) called")
//         enableEdgeToEdge()
//         setContent {
//             Covid19AppTheme {
//                 val navController = rememberNavController()

//                 NavHost(
//                     navController = navController,
//                     startDestination = "vaccine",
//                     modifier = Modifier.fillMaxSize()
//                 ) {
//                     composable("vaccine") {VaccineCoverageScreen()}
//                     composable("world") {WorldScreen()}
//                     composable("compare") {CompareScreen()} 
//                 }
//             }
//         }
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
