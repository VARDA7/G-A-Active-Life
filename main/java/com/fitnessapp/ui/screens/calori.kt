package com.fitnessapp.ui.screens


import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.fitnessapp.ui.viewmodels.UserViewModel


@Composable
fun calori(navController: NavController) {
    val userViewModel: UserViewModel = viewModel()
    var weight by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var activityLevel by remember { mutableStateOf(1.2f) }
    var calculatedCalories by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Kalori Hesaplayıcı",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        OutlinedTextField(
            value = weight,
            onValueChange = { weight = it },
            label = { Text("Kilo (kg)", modifier = Modifier.align(Alignment.CenterHorizontally)) },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = height,
            onValueChange = { height = it },
            label = { Text("Boy (cm)",modifier = Modifier.align(Alignment.CenterHorizontally)) },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = age,
            onValueChange = { age = it },
            label = { Text("Yaş",modifier = Modifier.align(Alignment.CenterHorizontally)) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(30.dp))


        Text("Aktivite Seviyesi" , modifier = Modifier.align(Alignment.CenterHorizontally), fontSize = 15.sp)

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(
                "Hareketsiz" to 1.2f,
                "Hafif Aktif" to 1.375f,
                "Orta Aktif" to 1.55f,
                "Çok Aktif" to 1.725f
            ).forEach { (label, value) ->
                FilterChip(
                    selected = activityLevel == value,
                    onClick = { activityLevel = value },
                    label = { Text(label, fontSize = 11.sp) },
                    modifier = Modifier.weight(1f).then(
                        if (activityLevel == value) {
                            Modifier.border(
                                width = 2.dp,
                                color = Color.Green,
                                shape = MaterialTheme.shapes.small
                            )
                        } else {
                            Modifier
                        }

                    )
                )
            }
        }

        Button(
            onClick = {
                val w = weight.toFloatOrNull() ?: 0f
                val h = height.toFloatOrNull() ?: 0f
                val a = age.toIntOrNull() ?: 0

                // BMR hesaplama (Harris-Benedict formülü)
                val bmr = 10 * w + 6.25 * h - 5 * a + 5
                calculatedCalories = (bmr * activityLevel).toInt()
            },
            modifier = Modifier.fillMaxWidth()

        ) {
            Spacer(modifier = Modifier.height(20.dp))

            Text("Hesapla",color = Color.White)
        }

        Spacer(modifier = Modifier.height(50.dp))

        if (calculatedCalories > 0) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Günlük Kalori İhtiyacı", modifier = Modifier.align(Alignment.CenterHorizontally),
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        text = "$calculatedCalories kcal", modifier = Modifier.align(Alignment.CenterHorizontally),
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}




