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
fun VaccineCoverageScreen() {
    var vaccineData by remember { mutableStateOf<Map<String, Long>>(emptyMap()) }
    var errorMessage by remember { mutableStateOf<String?>(null)}

    LaunchedEffect(Unit) {
        RetrofitInstance.api.getVaccineCoverage().enqueue(object : Callback<VaccineResponseData>{
            override fun onResponse(
                call: Call<VaccineResponseData?>,
                response: Response<VaccineResponseData?>
            ) {
                if (response.isSuccessful) {
                    vaccineData = response.body()?.timeline ?: emptyMap()
                }
                else {
                    errorMessage = "Error: ${response.code()}"
                }
            }

            override fun onFailure(call: Call<VaccineResponseData?>, t: Throwable) {
                errorMessage = "Failure: ${t.message}"
            }
        })

    }
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Vaccine Coverage(VN)") }
            )
        }
    ) { innerPadding ->
        // 👇 This is the content block of Scaffold
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Text("VN doses over time")
            //TODO: Fetch VN vaccine API and display chart

            Spacer(modifier = Modifier.height(8.dp))

            when {
                errorMessage != null -> {
                    Text(
                        text = "Error: $errorMessage",
                        color = MaterialTheme.colorScheme.error
                    )
                }
                vaccineData.isNotEmpty() -> {
                    Column {
                        vaccineData.forEach { (date, doses) ->
                            Text(text = "$date -> $doses doses")
                        }
                    }
                }
                else -> {
                    Text("Loading...")
                }
            }

        }
    }
}
