package com.fitnessapp.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.fitnessapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore


@Composable
fun G3(navController: NavController) {
    val context = LocalContext.current
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(painter = painterResource(id = R.drawable.register), contentDescription = "",
            modifier = Modifier.padding(12.dp).size(200.dp)
        )

        Spacer(modifier = Modifier.height(36.dp))

        Text("Kayıt Ol", fontSize = 24.sp , fontWeight = FontWeight.Bold )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Kullanıcı Adı") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("E-posta") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Şifre") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                // Firebase ile kullanıcı kaydı alma kısmı bu sayede kullanıc ıkaydı firebase düşer
                val auth = FirebaseAuth.getInstance()
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val user = auth.currentUser
                            val profileUpdates = userProfileChangeRequest {
                                displayName = name
                            }
                            user?.updateProfile(profileUpdates)?.addOnCompleteListener {
                                // Firestore'a UserProfile kaydet
                                val db = FirebaseFirestore.getInstance()
                                val userProfile = UserProfile(
                                    id = user?.uid ?: "",
                                    firstName = name,
                                    lastName = "",
                                    email = email,
                                    age = 0,
                                    height = 0,
                                    weight = 0,
                                    goal = "",
                                    locationPreferences = emptyList(),
                                    activities = emptyList(),
                                    weeklyDays = 0,
                                    dailyMinutes = 0,
                                    bmi = 0.0,
                                    gender = "",
                                    activityLevel = "",
                                    dailySteps = 0,
                                    dailyCaloriesBurned = 0,
                                    dailyProteinIntake = 0,
                                    dailyWaterIntake = 0,
                                    goalWeight = 0,
                                    goalDate = ""
                                )
                                db.collection("users")
                                    .document(user?.uid ?: "")
                                    .set(userProfile)
                                    .addOnSuccessListener {
                                        // isFirstTime değerini true olarak ayarla
                                        db.collection("users")
                                            .document(user?.uid ?: "")
                                            .update("isFirstTime", true)
                                            .addOnSuccessListener {
                                                Toast.makeText(context, "Kayıt Başarılı!", Toast.LENGTH_SHORT)
                                                    .show()
                                                navController.navigate("login") //Login sayfasına geçş
                                            }
                                    }
                                    .addOnFailureListener { e ->
                                        Toast.makeText(
                                            context,
                                            "Profil kaydedilirken hata oluştu: ${e.message}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                            }
                        } else {
                            Toast.makeText(
                                context,
                                "Hata: ${task.exception?.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Kayıt Ol", color = Color.White , fontSize = 20.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = {
            navController.navigate("Login") // Giriş sayfası varsa ona geç
        }) {
            Text("Zaten hesabınız var mı? Giriş yap")
        }
    }
}
// değişecek fire base denem sorununu çözüldügü zaman yeni kayıt olan kullanıcın onune bir kere çıkacak