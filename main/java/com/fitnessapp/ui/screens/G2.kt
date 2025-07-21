package com.fitnessapp.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.ui.Alignment
import kotlinx.coroutines.delay

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun G2(navController: NavController) {
    val context = LocalContext.current
    val db = FirebaseFirestore.getInstance()
    val userId = FirebaseAuth.getInstance().currentUser?.uid

    var isLoading by remember { mutableStateOf(true) }
    var isFirstTime by remember { mutableStateOf(true) }

    val userDocRef = userId?.let { db.collection("users").document(it) }

    // Kullanıcı verisini oku
    LaunchedEffect(userId) {
        userDocRef?.get()?.addOnSuccessListener { document ->
            if (document != null && document.exists()) {
                val isFirst = document.getBoolean("isFirstTime") ?: false
                if (!isFirst) {
                    // Kayıtlı kullanıcı ise hemen ana ekrana yönlendir
                    navController.navigate("main_screen") {
                        popUpTo("G2") { inclusive = true }
                    }
                    return@addOnSuccessListener
                }
                isFirstTime = isFirst
            }
            isLoading = false
        }?.addOnFailureListener {
            isLoading = false
        }
    }

    // Yükleniyorsa boş ekran
    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    // Buradan sonrası: kullanıcı ilk kez kayıt oluyorsa form göster
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var selectedGoal by remember { mutableStateOf("") }
    var selectedLocations = remember { mutableStateListOf<String>() }
    var weeklyDays by remember { mutableStateOf("") }
    var dailyMinutes by remember { mutableStateOf("") }

    val goals = listOf("Fit Görünüm", "Sağlıklı Yaşam", "Kas Gelişimi", "Zayıflama")
    val locations = listOf("Spor Salonu", "Evde Egzersiz", "Açık Alan", "Koşu")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("Profil Bilgileri", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = firstName,
            onValueChange = { firstName = it },
            label = { Text("İsim") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = lastName,
            onValueChange = { lastName = it },
            label = { Text("Soyisim") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = age,
            onValueChange = { age = it },
            label = { Text("Yaş") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        OutlinedTextField(
            value = height,
            onValueChange = { height = it },
            label = { Text("Boy (cm)") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        OutlinedTextField(
            value = weight,
            onValueChange = { weight = it },
            label = { Text("Kilo (kg)") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("Hedef Seçimi", style = MaterialTheme.typography.headlineSmall)
        FlowRow {
            goals.forEach { goal ->
                Button(
                    onClick = { selectedGoal = goal },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedGoal == goal) Color.Green else Color.LightGray
                    ),
                    modifier = Modifier.padding(4.dp)
                ) {
                    Text(goal)
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text("Mekan Seçimi", style = MaterialTheme.typography.headlineSmall)
        FlowRow {
            locations.forEach { location ->
                val isSelected = location in selectedLocations
                Button(
                    onClick = {
                        if (isSelected) selectedLocations.remove(location)
                        else selectedLocations.add(location)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSelected) Color.Blue else Color.LightGray
                    ),
                    modifier = Modifier.padding(4.dp)
                ) {
                    Text(location)
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = weeklyDays,
            onValueChange = { weeklyDays = it },
            label = { Text("Haftada Kaç Gün Spor Yapıyorsunuz?") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        OutlinedTextField(
            value = dailyMinutes,
            onValueChange = { dailyMinutes = it },
            label = { Text("Günlük Spor Süresi (dk)") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (firstName.isNotBlank() && lastName.isNotBlank() && age.isNotBlank()
                    && height.isNotBlank() && weight.isNotBlank() && selectedGoal.isNotBlank()
                ) {
                    val bmi = calculateBMI(weight.toDouble(), height.toDouble())

                    val userProfile = UserProfile(
                        firstName = firstName,
                        lastName = lastName,
                        age = age.toInt(),
                        height = height.toInt(),
                        weight = weight.toInt(),
                        goal = selectedGoal,
                        locationPreferences = selectedLocations,
                        weeklyDays = weeklyDays.toIntOrNull() ?: 0,
                        dailyMinutes = dailyMinutes.toIntOrNull() ?: 0,
                        bmi = bmi
                    )

                    userDocRef?.set(userProfile)?.addOnSuccessListener {
                        userDocRef.update("isFirstTime", false)
                        Toast.makeText(context, "Profil kaydedildi!", Toast.LENGTH_SHORT).show()
                        navController.navigate("main_screen") {
                            popUpTo("G2") { inclusive = true }
                        }
                    }?.addOnFailureListener { e ->
                        Toast.makeText(context, "Hata: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "Tüm bilgileri doldurun.", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Kaydet", color = Color.White)
        }
    }
}

fun calculateBMI(weight: Double, heightCm: Double): Double {
    val heightMeter = heightCm / 100
    return weight / (heightMeter * heightMeter)
}
