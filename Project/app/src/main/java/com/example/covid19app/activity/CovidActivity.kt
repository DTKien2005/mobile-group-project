package com.example.covid19app.activity

import android.os.Build
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.covid19app.R
import com.example.covid19app.frag.*
import com.example.covid19app.notify.NotificationScheduler
import com.google.android.material.bottomnavigation.BottomNavigationView

class CovidActivity : AppCompatActivity() {

    companion object {
        private const val KEY_CURRENT_ITEM = "key_current_item"
    }

    // Registering permission request for notifications
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
    private lateinit var bottomNav: BottomNavigationView
    private var selectedFragmentIndex: Int = 0 // Default to StatsFragment
    private lateinit var btnNotify: ImageView // Notification button reference
    private lateinit var btnSearch: ImageView // Search button reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dashboard)

        // Set the toolbar as the action bar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Get the selected fragment index passed from MainActivity
        selectedFragmentIndex = intent.getIntExtra("FRAGMENT_INDEX", 0)

        // Set up ViewPager2 to manage fragment navigation (if needed for other tabs)
        pager = findViewById(R.id.viewPager)
        pager.adapter = DashboardPagerAdapter(this)
        pager.offscreenPageLimit = 3
        pager.setCurrentItem(selectedFragmentIndex, false)

        // Set up Bottom Navigation to change fragments based on selection
        bottomNav = findViewById(R.id.bottomNav)
        bottomNav.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.btnStats -> pager.currentItem = 0
                R.id.btnTrend -> pager.currentItem = 1
                R.id.btnSymptomChecker -> pager.currentItem = 2
                R.id.btnVnVac -> pager.currentItem = 3
                R.id.btnWorldVac -> pager.currentItem = 4
                R.id.btnVnVsWorld -> pager.currentItem = 5
                R.id.btnSearch -> pager.currentItem = 6
                else -> return@setOnNavigationItemSelectedListener false
            }
            true
        }

        // Initialize the notification button
        btnNotify = findViewById(R.id.btnNotify)
        btnSearch = findViewById(R.id.btnSearch)

        // Set up the click listener for the notification button
        btnNotify.setOnClickListener {
            // Invoke the permission request if not granted
            requestNotifPermission.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        }

        btnSearch.setOnClickListener {
            // Navigate to the CountrySearchFragment
            pager.currentItem = 6 // The fragment index for the CountrySearchFragment

            // Get the fragment reference and focus on the SearchView
            val fragment = supportFragmentManager.findFragmentByTag("f${pager.currentItem}")
            if (fragment is CountrySearchFragment) {
                fragment.getSearchView().requestFocus()
            }
        }
    }


    // Adapter to manage fragments within the ViewPager (kept for other fragment tabs)
    private class DashboardPagerAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {
        override fun getItemCount(): Int = 7

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> StatsFragment()
                1 -> SymptomCheckerActivity()
                2 -> TrendsFragment()
                3 -> VietnamVaccineFragment()
                4 -> WorldVaccineFragment()
                5 -> CompareFragment()
                6 -> CountrySearchFragment()
                else -> StatsFragment() // Default case
            }
        }
    }
}