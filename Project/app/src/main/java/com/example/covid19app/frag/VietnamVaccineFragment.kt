package com.example.covid19app.frag

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.covid19app.R
import com.example.covid19app.data.VaccineResponseData
import com.example.covid19app.viewmodel.VietnamVaccineViewModel
import java.text.SimpleDateFormat
import java.util.*

class VietnamVaccineFragment : Fragment() {

    private val vietnamVaccineViewModel: VietnamVaccineViewModel by viewModels()
    private var tvVaccine: TextView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_vaccine_coverage, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tvVaccine = view.findViewById(R.id.tvVaccine)

        // Observe the vaccine data
        vietnamVaccineViewModel.vaccineData.observe(viewLifecycleOwner, Observer {
            renderVaccine(it)
        })

        // Observe any error messages
        vietnamVaccineViewModel.errorMessage.observe(viewLifecycleOwner, Observer { message ->
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        })

        // Load vaccine data
        vietnamVaccineViewModel.loadVietnamVaccineData()
    }

    private fun renderVaccine(data: VaccineResponseData) {
        // entries -> Set, so convert to list and sort by date
        val inputFormats = listOf(
            SimpleDateFormat("M/d/yy", Locale.ENGLISH),
            SimpleDateFormat("MM/dd/yy", Locale.ENGLISH)
        )

        fun parse(d: String) = inputFormats.firstNotNullOfOrNull { f ->
            runCatching { f.parse(d) }.getOrNull()
        }?.time ?: Long.MIN_VALUE

        val latest7 = data.timeline
            .entries
            .toList()
            .sortedBy { parse(it.key) }   // chronological
            .takeLast(7)

        val text = buildString {
            append("Vietnam Vaccine Coverage (Latest 7 days):\n")
            latest7.forEach { (date, value) -> append("$date → $value doses\n") }
        }
        tvVaccine?.text = text
    }
}
