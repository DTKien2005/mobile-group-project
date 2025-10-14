package com.example.covid19app.activity

import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.covid19app.R
import com.example.covid19app.frag.CompareFragment
import com.example.covid19app.frag.StatsFragment
import com.example.covid19app.frag.TrendsFragment
import com.example.covid19app.frag.VietnamVaccineFragment
import com.example.covid19app.frag.WorldVaccineFragment
import com.example.covid19app.frag.CountrySearchFragment
import com.example.covid19app.notify.NotificationScheduler

class CovidActivity : AppCompatActivity() {

    companion object {
        private const val KEY_CURRENT_ITEM = "key_current_item"
    }

    private val requestNotifPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted || Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            if (NotificationScheduler.requestExactAlarmIfNeeded(this)) {
                return@registerForActivityResult
            }
            NotificationScheduler.scheduleDailyReminder(this, hour = 9, minute = 0)
            Toast.makeText(this, "Daily notification set for 9:00 AM", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Notification permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    private lateinit var pager: ViewPager2
    private var selectedFragmentIndex: Int = 0 // Default to StatsFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dashboard)

        // Get the selected fragment index passed from MainActivity
        selectedFragmentIndex = intent.getIntExtra("FRAGMENT_INDEX", 0)

        // --- ViewPager2 setup ---
        pager = findViewById(R.id.viewPager)
        pager.adapter = DashboardPagerAdapter(this)
        pager.offscreenPageLimit = 2

        // Set the initial fragment based on the passed index
        pager.setCurrentItem(selectedFragmentIndex, false)

        // Bottom buttons that change the current fragment page
        findViewById<Button>(R.id.btnHome).setOnClickListener { pager.currentItem = 0 }
        findViewById<Button>(R.id.btnSymptomChecker).setOnClickListener { pager.currentItem = 1 }
        findViewById<Button>(R.id.btnTrend).setOnClickListener { pager.currentItem = 2 }
        findViewById<Button>(R.id.btnVnVac).setOnClickListener { pager.currentItem = 3 }
        findViewById<Button>(R.id.btnWorldVac).setOnClickListener { pager.currentItem = 4 }
        findViewById<Button>(R.id.btnVnVsWorld).setOnClickListener { pager.currentItem = 5 }
        findViewById<Button>(R.id.btnSearch).setOnClickListener { pager.currentItem = 6 }
    }

    // Adapter to manage fragments within the ViewPager
    private class DashboardPagerAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {
        override fun getItemCount(): Int = 7 // There are 7 fragments

        override fun createFragment(position: Int): Fragment = when (position) {
            0 -> StatsFragment()
            1 -> SymptomCheckerActivity() // Assuming SymptomCheckerActivity is a fragment
            2 -> TrendsFragment()
            3 -> VietnamVaccineFragment()
            4 -> WorldVaccineFragment()
            5 -> CompareFragment()
            6 -> CountrySearchFragment()
            else -> StatsFragment() // Default case
        }
    }
}