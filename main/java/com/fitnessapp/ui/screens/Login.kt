package com.fitnessapp.ui.screens

import android.widget.Button
import android.widget.Space
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.fitnessapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun Login (navController: NavController){
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()

    var email by remember { mutableStateOf("") }
    var sifre by remember { mutableStateOf("") }
    var hataMesaji by remember { mutableStateOf<String?>(null) }

    Column (
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {


        Image(painter = painterResource(R.drawable.user), contentDescription = "",
            modifier = Modifier.padding(12.dp).height(200.dp))

        Spacer(modifier = Modifier.height(36.dp))

        Text("Giriş Yap", style = MaterialTheme.typography.headlineLarge)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("E-posta") },
            modifier = Modifier.fillMaxWidth()

        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = sifre,
            onValueChange = { sifre = it },
            label = { Text("Şifre") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                LoginControl(auth, email, sifre, context, navController) { message ->
                    hataMesaji = message
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Giriş Yap", color = Color.White , fontSize = 20.sp)
        }

        hataMesaji?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(it, color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(
            onClick = { navController.navigate("G3") }
        ) {
            Text("Hesabınız yok mu? Kayıt olun")
        }
    }
}

// firebase kayıttan sonra kontrllü giriş için gerekn fonksiyon
fun LoginControl(
    auth: FirebaseAuth,
    email: String,
    password: String,
    context: android.content.Context,
    navController: NavController,
    onHata: (String?) -> Unit
) {
    if (email.isBlank() || password.isBlank()) {
        onHata("E-posta ve şifre boş olamaz")
        return
    }

    auth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = auth.currentUser
                val userId = user?.uid

                if (userId != null) {
                    val db = FirebaseFirestore.getInstance()
                    val userDocRef = db.collection("users").document(userId)

                    userDocRef.get().addOnSuccessListener { document ->
                        if (document != null && document.exists()) {
                            val isFirstTime = document.getBoolean("isFirstTime") ?: true
                            if (isFirstTime) {
                                navController.navigate("G2") {
                                    popUpTo("login") { inclusive = true }
                                }
                            } else {
                                navController.navigate("main_screen") {
                                    popUpTo("login") { inclusive = true }
                                }
                            }
                        } else {

                            navController.navigate("G2") {
                                popUpTo("login") { inclusive = true }
                            }
                        }
                    }.addOnFailureListener {

                        navController.navigate("G2") {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                } else {
                    onHata("Giriş başarısız. Kullanıcı ID bulunamadı.")
                }
            } else {
                onHata(task.exception?.message ?: "Giriş başarısız.")
            }
        }
}

