package com.example.covid19app.activity

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
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
import com.example.covid19app.frag.VaccineCoverageFragment
import com.example.covid19app.frag.WorldVaccineFragment
import com.example.covid19app.frag.CountrySearchFragment
import com.example.covid19app.notify.NotificationScheduler

class VnDashboardActivity : AppCompatActivity() {

    companion object {
        private const val KEY_CURRENT_ITEM = "key_current_item"
    }

    // Ask for POST_NOTIFICATIONS on Android 13+
    private val requestNotifPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted || Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            // Android 12+: ensure we have exact-alarm consent
            if (NotificationScheduler.requestExactAlarmIfNeeded(this)) {
                // The system settings screen was opened; don't schedule yet
                return@registerForActivityResult
            }
            NotificationScheduler.scheduleDailyReminder(this, hour = 9, minute = 0)
            Toast.makeText(this, "Daily notification set for 9:00 AM", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Notification permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    private lateinit var pager: ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.vndashboard)

        // --- ViewPager2 setup ---
        pager = findViewById(R.id.viewPager)
        pager.adapter = DashboardPagerAdapter(this)
        pager.offscreenPageLimit = 2

        // Restore previously selected page (if any)
        val initialIndex = savedInstanceState?.getInt(KEY_CURRENT_ITEM) ?: 0
        pager.setCurrentItem(initialIndex, false)

        // --- Bell button: tap = daily 9:00, long-press = 60s test ---
        // (Preserved behavior)
        findViewById<ImageButton>(R.id.btnNotify).apply {
            setOnClickListener {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    requestNotifPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
                    return@setOnClickListener
                }
                if (NotificationScheduler.requestExactAlarmIfNeeded(this@VnDashboardActivity)) return@setOnClickListener
                NotificationScheduler.scheduleDailyReminder(this@VnDashboardActivity, 9, 0)
                Toast.makeText(this@VnDashboardActivity, "Daily notification set for 9:00 AM", Toast.LENGTH_SHORT).show()
            }
            setOnLongClickListener {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    requestNotifPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
                    return@setOnLongClickListener true
                }
                if (NotificationScheduler.requestExactAlarmIfNeeded(this@VnDashboardActivity)) return@setOnLongClickListener true
                NotificationScheduler.scheduleTestIn(this@VnDashboardActivity, 60)
                Toast.makeText(this@VnDashboardActivity, "Test notification in 60 seconds", Toast.LENGTH_SHORT).show()
                true
            }
        }

        // --- Bottom buttons jump to pages instead of fragment transactions ---
        findViewById<Button>(R.id.btnHome).setOnClickListener { pager.currentItem = 0 }
        findViewById<Button>(R.id.btnSymptomChecker).setOnClickListener { pager.currentItem = 1 }
        findViewById<Button>(R.id.btnTrend).setOnClickListener { pager.currentItem = 2 }
        findViewById<Button>(R.id.btnVnVac).setOnClickListener { pager.currentItem = 3 }
        findViewById<Button>(R.id.btnWorldVac).setOnClickListener { pager.currentItem = 4 }
        findViewById<Button>(R.id.btnVnVsWorld).setOnClickListener { pager.currentItem = 5 }
        findViewById<Button>(R.id.btnSearch).setOnClickListener { pager.currentItem = 6 }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (::pager.isInitialized) {
            outState.putInt(KEY_CURRENT_ITEM, pager.currentItem)
        }
    }

    // Simple pager with your three screens
    private class DashboardPagerAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {
        override fun getItemCount(): Int = 7
        override fun createFragment(position: Int): Fragment = when (position) {
            0 -> StatsFragment()
            1 -> SymptomCheckerActivity()
            2 -> TrendsFragment()
            3 -> VaccineCoverageFragment()
            4 -> WorldVaccineFragment()
            5 -> CompareFragment()
            6 -> CountrySearchFragment()
            else -> StatsFragment()
        }
    }
}