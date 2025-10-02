
package com.example.covid19app

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import androidx.fragment.app.Fragment
import java.text.SimpleDateFormat
import java.util.*

class SymptomCheckerFragment : Fragment() {

    private val TAG = "SymptomCheckerFragment"

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d(TAG, "onAttach")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "onCreateView")
        val root = inflater.inflate(R.layout.fragment_symptom_checker, container, false)

        val cbFever = root.findViewById<CheckBox>(R.id.cbFever)
        val cbCough = root.findViewById<CheckBox>(R.id.cbCough)
        val cbBreath = root.findViewById<CheckBox>(R.id.cbBreath)
        val btnCheck = root.findViewById<Button>(R.id.btnCheck)
        val tvResult = root.findViewById<TextView>(R.id.tvResult)
        val tvLastCheck = root.findViewById<TextView>(R.id.tvLastCheck)

        val prefs = requireContext().getSharedPreferences("symptom_prefs", Context.MODE_PRIVATE)
        val lastCheck = prefs.getString("last_check", null)
        tvLastCheck.text = if (lastCheck != null) {
            "Lần kiểm tra trước: $lastCheck"
        } else {
            "Chưa có lần kiểm tra nào"
        }

        btnCheck.setOnClickListener {
            var symptomCount = 0
            if (cbFever.isChecked) symptomCount++
            if (cbCough.isChecked) symptomCount++
            if (cbBreath.isChecked) symptomCount++

            tvResult.text = when (symptomCount) {
                0 -> "Không có triệu chứng."
                1 -> "Có một vài triệu chứng, theo dõi thêm."
                else -> "Nhiều triệu chứng, hãy đi khám."
            }

            val date = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
            prefs.edit().putString("last_check", date).apply()
            tvLastCheck.text = "Lần kiểm tra trước: $date"

            Log.d(TAG, "Checked symptoms: $symptomCount at $date")
        }

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated")
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG, "onDestroyView")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy")
    }

    override fun onDetach() {
        super.onDetach()
        Log.d(TAG, "onDetach")
    }
}
