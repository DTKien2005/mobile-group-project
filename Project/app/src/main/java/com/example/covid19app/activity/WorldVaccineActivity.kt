package com.example.covid19app.activity

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.covid19app.api.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorldScreen() {
    var worldData by remember { mutableStateOf<Map<String, Long>>(emptyMap()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        // Correct endpoint + parameters
        RetrofitInstance.api.getWorldVaccineCoverage(lastDays = "30", fullData = false)
            .enqueue(object : Callback<Map<String, Long>> {
                override fun onResponse(
                    call: Call<Map<String, Long>>,
                    response: Response<Map<String, Long>>
                ) {
                    if (response.isSuccessful) {
                        worldData = response.body().orEmpty()
                    } else {
                        errorMessage = "Error: ${response.code()}"
                    }
                }

                override fun onFailure(call: Call<Map<String, Long>>, t: Throwable) {
                    errorMessage = "Failure: ${t.message}"
                }
            })
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text("Vaccine Coverage (Worldwide)") })
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text("Worldwide doses over time")
            Spacer(Modifier.height(8.dp))

            when {
                errorMessage != null -> Text("Error: $errorMessage", color = MaterialTheme.colorScheme.error)
                worldData.isNotEmpty() -> {
                    Column {
                        worldData.forEach { (date, doses) ->
                            Text("$date → $doses doses")
                        }
                    }
                }
                else -> Text("Loading…")
            }
        }
    }
}
