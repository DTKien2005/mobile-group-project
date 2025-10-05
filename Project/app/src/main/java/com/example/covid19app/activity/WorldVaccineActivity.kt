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
    var errorMessage by remember { mutableStateOf<String?>(null)}

    LaunchedEffect(Unit) {
        RetrofitInstance.api.getWorldData().enqueue(object : Callback<Map<String, Long>> {
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
            CenterAlignedTopAppBar(
                title = { Text("Vaccine Coverage(World wide)") }
            )
        }
    ) { innerPadding ->
        // 👇 This is the content block of Scaffold
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Text("World wide doses over time")
            //TODO: Fetch VN vaccine API and display chart

            Spacer(modifier = Modifier.height(8.dp))

            when {
                errorMessage != null -> {
                    Text(
                        text = "Error: $errorMessage",
                        color = MaterialTheme.colorScheme.error
                    )
                }
                worldData.isNotEmpty() -> {
                    Column {
                        worldData.forEach { (date, doses) ->
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