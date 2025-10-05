package com.example.covid19app

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.covid19app.frag.StatsFragment
import com.example.covid19app.frag.TrendsFragment

class DashboardPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
    override fun getItemCount() = 3
    override fun createFragment(position: Int): Fragment = when (position) {
        0 -> StatsFragment()
        1 -> SymptomCheckerActivity()   // this class already extends Fragment
        2 -> TrendsFragment()
        else -> StatsFragment()
    }
}