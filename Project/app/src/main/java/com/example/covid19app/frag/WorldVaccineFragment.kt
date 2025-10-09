package com.example.covid19app.frag

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.covid19app.R
import com.example.covid19app.viewmodel.WorldVaccineViewModel

class WorldVaccineFragment : Fragment() {

    private lateinit var viewModel: WorldVaccineViewModel
    private var tvWorld: TextView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_world, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tvWorld = view.findViewById(R.id.tvWorld)
        viewModel = ViewModelProvider(this).get(WorldVaccineViewModel::class.java)

        viewModel.worldVaccineData.observe(viewLifecycleOwner, { data ->
            renderWorld(data)
        })

        viewModel.errorMessage.observe(viewLifecycleOwner, { message ->
            tvWorld?.text = message
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        })

        viewModel.loadWorldVaccineData()
    }

    private fun renderWorld(data: Map<String, Long>) {
        // entries -> Set, so convert to list and sort by date
        val inputFormats = listOf(
            java.text.SimpleDateFormat("M/d/yy", java.util.Locale.ENGLISH),
            java.text.SimpleDateFormat("MM/dd/yy", java.util.Locale.ENGLISH)
        )

        fun parse(d: String) = inputFormats.firstNotNullOfOrNull { f ->
            runCatching { f.parse(d) }.getOrNull()
        }?.time ?: Long.MIN_VALUE

        val latest7 = data
            .entries
            .toList()
            .sortedBy { parse(it.key) }   // chronological
            .takeLast(7)

        val text = buildString {
            append("World Vaccine Coverage (Latest 7 days):\n")
            latest7.forEach { (date, value) -> append("$date → $value doses\n") }
        }
        tvWorld?.text = text
    }
}
