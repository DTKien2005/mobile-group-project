package com.example.covid19app.offlinedata

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object DataSeeder {
    private const val PREFS = "seed_prefs"
    private const val KEY_SEEDED = "seeded_v1"

    // Seed URLs (mirror your Retrofit endpoints)
    private const val URL_COUNTRIES = "https://disease.sh/v3/covid-19/countries"
    private const val URL_VN_TRENDS = "https://disease.sh/v3/covid-19/historical/vietnam?lastdays=all"
    private const val URL_VN_VACCINE = "https://disease.sh/v3/covid-19/vaccine/coverage/countries/vietnam?lastdays=30&fullData=false"
    private const val URL_WORLD_VACCINE = "https://disease.sh/v3/covid-19/vaccine/coverage?lastdays=30&fullData=false"

    suspend fun ensureSeeded(context: Context) = withContext(Dispatchers.IO) {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        if (prefs.getBoolean(KEY_SEEDED, false)) return@withContext

        // Download a minimal set for offline use
        FileCache.downloadToFile(context, URL_COUNTRIES, "countries.json")
        FileCache.downloadToFile(context, URL_VN_TRENDS, "vn_trends.json")
        FileCache.downloadToFile(context, URL_VN_VACCINE, "vn_vaccine.json")
        FileCache.downloadToFile(context, URL_WORLD_VACCINE, "world_vaccine.json")

        prefs.edit().putBoolean(KEY_SEEDED, true).apply()
    }
}
