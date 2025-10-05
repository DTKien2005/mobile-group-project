package com.example.covid19app.activity

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.covid19app.api.RetrofitInstance
import com.example.covid19app.data.VaccineResponseData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompareScreen() {
    var vaccineData by remember { mutableStateOf<Map<String, Long>>(emptyMap()) }
    var worldData by remember { mutableStateOf<Map<String, Long>>(emptyMap()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        RetrofitInstance.api.getVaccineCoverage().enqueue(object : Callback<VaccineResponseData> {
            override fun onResponse(
                call: Call<VaccineResponseData?>,
                response: Response<VaccineResponseData?>
            ) {
                if (response.isSuccessful) {
                    vaccineData = response.body()?.timeline ?: emptyMap()
                }
            }

            override fun onFailure(call: Call<VaccineResponseData?>, t: Throwable) {
                errorMessage = "Vietnam data error: ${t.message}"
            }
        })

        RetrofitInstance.api.getWorldData().enqueue(object : Callback<Map<String, Long>> {
            override fun onResponse(
                call: Call<Map<String, Long>>,
                response: Response<Map<String, Long>>
            ) {
                if (response.isSuccessful) {
                    worldData = response.body().orEmpty()
                }
            }
            override fun onFailure(call: Call<Map<String, Long>>, t: Throwable) {
                errorMessage = "World data error: ${t.message}"
            }
        })
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Comparison") }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            when {
                errorMessage != null -> Text(
                    "Error: $errorMessage",
                    color = MaterialTheme.colorScheme.error
                )

                vaccineData.isNotEmpty() && worldData.isNotEmpty() -> {
                    val vietnamLatest = vaccineData.entries.last()
                    val worldLatest = worldData.entries.last()

                    Text("Vietnam latest: ${vietnamLatest.key} → ${vietnamLatest.value} doses")
                    Text("World latest: ${worldLatest.key} → ${worldLatest.value} doses")

                    Spacer(modifier = Modifier.height(16.dp))
                    Text("VN vs World Timeline:")

                    vaccineData.keys.zip(worldData.keys).forEach { (vnDate, worldDate) ->
                        val vnDoses = vaccineData[vnDate]
                        val worldDoses = worldData[worldDate]
                        Text("$vnDate: VN $vnDoses | World $worldDoses")
                    }
                }

            }

        }
    }
}