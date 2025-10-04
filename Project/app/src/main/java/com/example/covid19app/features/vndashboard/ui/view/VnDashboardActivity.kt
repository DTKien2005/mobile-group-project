package com.example.covid19app.features.vndashboard.ui.view

// imports you need:
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.covid19app.R
import com.example.covid19app.SymptomCheckerFragment

class VnDashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.vndashboard)

        // Default screen = Home (stats)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, StatsFragment())
                .commit()
        }

        // Buttons from vndashboard.xml (Home + Symptom Checker exist there:contentReference[oaicite:6]{index=6})
        findViewById<Button>(R.id.btnSymptomChecker).setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, SymptomCheckerFragment())
                .addToBackStack("symptom")
                .commit()
        }

        // If you added an ID to the first button ("Home"), wire it:
        val btnHome = findViewById<Button>(R.id.btnHome) // add id in XML
        btnHome?.setOnClickListener {
            supportFragmentManager.popBackStack(null, androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE)
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, StatsFragment())
                .commit()
        }
    }
}
