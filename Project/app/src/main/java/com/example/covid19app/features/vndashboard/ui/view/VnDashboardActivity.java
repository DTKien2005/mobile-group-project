package com.example.covid19app.features.vndashboard.ui.view;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.covid19app.R;
import com.example.covid19app.features.vndashboard.data.api.CovidApiService;
import com.example.covid19app.features.vndashboard.data.model.CovidStats;

public class VnDashboardActivity extends AppCompatActivity {

    private TextView tvUpdated, tvCountry, tvCases, tvTodayCases, tvDeaths, tvTodayDeaths,
            tvRecovered, tvTodayRecovered, tvActive, tvCritical, tvTests, tvPopulation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vndashboard);

        // Gán view từ layout
        tvUpdated = findViewById(R.id.tvUpdated);
        tvCountry = findViewById(R.id.tvCountry);
        tvCases = findViewById(R.id.tvCases);
        tvTodayCases = findViewById(R.id.tvTodayCases);
        tvDeaths = findViewById(R.id.tvDeaths);
        tvTodayDeaths = findViewById(R.id.tvTodayDeaths);
        tvRecovered = findViewById(R.id.tvRecovered);
        tvTodayRecovered = findViewById(R.id.tvTodayRecovered);
        tvActive = findViewById(R.id.tvActive);
        tvCritical = findViewById(R.id.tvCritical);
        tvTests = findViewById(R.id.tvTests);
        tvPopulation = findViewById(R.id.tvPopulation);

        // Gọi API qua Service
        CovidApiService.fetchCovidStats(this, new CovidApiService.CovidCallback() {
            @Override
            public void onSuccess(CovidStats stats) {
                // Update UI (trên main thread)
                tvUpdated.setText("Updated: " + stats.getUpdated());
                tvCountry.setText("Country: " + stats.getCountry());
                tvCases.setText("Cases: " + stats.getCases());
                tvTodayCases.setText("Today Cases: " + stats.getTodayCases());
                tvDeaths.setText("Deaths: " + stats.getDeaths());
                tvTodayDeaths.setText("Today Deaths: " + stats.getTodayDeaths());
                tvRecovered.setText("Recovered: " + stats.getRecovered());
                tvTodayRecovered.setText("Today Recovered: " + stats.getTodayRecovered());
                tvActive.setText("Active: " + stats.getActive());
                tvCritical.setText("Critical: " + stats.getCritical());
                tvTests.setText("Tests: " + stats.getTests());
                tvPopulation.setText("Population: " + stats.getPopulation());

            }

            @Override
            public void onError(String errorMessage) {
                Log.e("VnDashboardActivity", "API error: " + errorMessage);
                tvCountry.setText("Error!!!");
            }
        });
    }
}
