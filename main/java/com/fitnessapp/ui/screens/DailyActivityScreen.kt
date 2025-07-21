package com.fitnessapp.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.fitnessapp.ui.viewmodels.DailyActivityViewModel
import com.fitnessapp.ui.viewmodels.DailyGoal
import com.fitnessapp.ui.viewmodels.MessageType
import com.fitnessapp.ui.viewmodels.MotivationMessage
import com.fitnessapp.ui.viewmodels.UserViewModel
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DailyActivityScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: DailyActivityViewModel = viewModel(),
    userViewModel: UserViewModel = viewModel()
) {
    val dailyGoals by viewModel.dailyGoals.collectAsState()
    val weeklyProgress by viewModel.weeklyProgress.collectAsState()
    val motivationMessage by viewModel.motivationMessage.collectAsState()
    val showCelebration by viewModel.showCelebration.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isFirestoreConnected by viewModel.isFirestoreConnected.collectAsState()
    val userProfile by userViewModel.userProfile.collectAsState()

    var showWaterDialog by remember { mutableStateOf(false) }
    var showStepsDialog by remember { mutableStateOf(false) }
    var showCaloriesDialog by remember { mutableStateOf(false) }
    var showProteinDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        userViewModel.listenUserProfile()
    }

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize()
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {

                    if (!isFirestoreConnected) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Firestore bağlantısı yok. Lütfen internet bağlantınızı kontrol edin.",
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }

                    // Motivasyon Mesajı
                    AnimatedVisibility(
                        visible = motivationMessage != null,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        motivationMessage?.let { message ->
                            MotivationMessageCard(message)
                        }
                    }

                    // Günlük Hedefler Bölümü
                    userProfile?.let { profile ->
                        val goals = userViewModel.getDailyGoals(profile)
                        
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                                .shadow(8.dp, RoundedCornerShape(24.dp)),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            shape = RoundedCornerShape(24.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Text(
                                    text = "Günlük Hedefler",
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )

                                // Su Hedefi
                                GoalCheckCard(
                                    title = "Su",
                                    icon = Icons.Default.WaterDrop,
                                    currentValue = profile.dailyWaterIntake,
                                    targetValue = goals["water"] ?: 0,
                                    unit = "ml",
                                    onUpdateClick = { showWaterDialog = true },
                                    onComplete = { /* Görev tamamlandığında yapılacak işlemler */ },
                                    completed = (profile.dailyWaterIntake >= (goals["water"] ?: 0))
                                )

                                // Adım Hedefi
                                GoalCheckCard(
                                    title = "Adım",
                                    icon = Icons.Default.DirectionsWalk,
                                    currentValue = profile.dailySteps,
                                    targetValue = goals["steps"] ?: 0,
                                    unit = "adım",
                                    onUpdateClick = { showStepsDialog = true },
                                    onComplete = { /* Görev tamamlandığında yapılacak işlemler */ },
                                    completed = (profile.dailySteps >= (goals["steps"] ?: 0))
                                )

                                // Kalori Hedefi
                                GoalCheckCard(
                                    title = "Kalori",
                                    icon = Icons.Default.LocalFireDepartment,
                                    currentValue = profile.dailyCaloriesBurned,
                                    targetValue = goals["calories"] ?: 0,
                                    unit = "kcal",
                                    onUpdateClick = { showCaloriesDialog = true },
                                    onComplete = { /* Görev tamamlandığında yapılacak işlemler */ },
                                    completed = (profile.dailyCaloriesBurned >= (goals["calories"] ?: 0))
                                )

                                // Protein Hedefi
                                GoalCheckCard(
                                    title = "Protein",
                                    icon = Icons.Default.FitnessCenter,
                                    currentValue = profile.dailyProteinIntake,
                                    targetValue = goals["protein"] ?: 0,
                                    unit = "g",
                                    onUpdateClick = { 
                                        println("Protein hedefi güncelleme tıklandı")
                                        println("Mevcut değer: ${profile.dailyProteinIntake}")
                                        println("Hedef değer: ${goals["protein"]}")
                                        showProteinDialog = true 
                                    },
                                    onComplete = { /* Görev tamamlandığında yapılacak işlemler */ },
                                    completed = (profile.dailyProteinIntake >= (goals["protein"] ?: 0))
                                )
                            }
                        }
                    }

                    // Haftalık İlerleme Bölümü
                    WeeklyProgressSection(
                        title = "Haftalık İlerleme",
                        progress = weeklyProgress
                    )
                }

                // Kutlama Animasyonu
                if (showCelebration) {
                    CelebrationAnimation()
                }

                // Yükleme Göstergesi
                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }

    // Su Güncelleme Dialogu
    if (showWaterDialog) {
        var waterInput by remember { mutableStateOf(userProfile?.dailyWaterIntake.toString()) }
        AlertDialog(
            onDismissRequest = { showWaterDialog = false },
            title = { Text("Su Tüketimi Güncelle") },
            text = {
                OutlinedTextField(
                    value = waterInput,
                    onValueChange = { waterInput = it },
                    label = { Text("Su Miktarı (ml)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        waterInput.toIntOrNull()?.let { value ->
                            userViewModel.updateDailyWaterIntake(value)
                        }
                        showWaterDialog = false
                    }
                ) {
                    Text("Kaydet")
                }
            },
            dismissButton = {
                TextButton(onClick = { showWaterDialog = false }) {
                    Text("İptal")
                }
            }
        )
    }

    // Adım Güncelleme Dialogu
    if (showStepsDialog) {
        var stepsInput by remember { mutableStateOf(userProfile?.dailySteps.toString()) }
        AlertDialog(
            onDismissRequest = { showStepsDialog = false },
            title = { Text("Adım Sayısı Güncelle") },
            text = {
                OutlinedTextField(
                    value = stepsInput,
                    onValueChange = { stepsInput = it },
                    label = { Text("Adım Sayısı") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        stepsInput.toIntOrNull()?.let { value ->
                            userViewModel.updateDailySteps(value)
                        }
                        showStepsDialog = false
                    }
                ) {
                    Text("Kaydet")
                }
            },
            dismissButton = {
                TextButton(onClick = { showStepsDialog = false }) {
                    Text("İptal")
                }
            }
        )
    }

    // Kalori Güncelleme Dialogu
    if (showCaloriesDialog) {
        var caloriesInput by remember { mutableStateOf(userProfile?.dailyCaloriesBurned.toString()) }
        AlertDialog(
            onDismissRequest = { showCaloriesDialog = false },
            title = { Text("Kalori Yakımı Güncelle") },
            text = {
                OutlinedTextField(
                    value = caloriesInput,
                    onValueChange = { caloriesInput = it },
                    label = { Text("Kalori (kcal)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        caloriesInput.toIntOrNull()?.let { value ->
                            userViewModel.updateDailyCaloriesBurned(value)
                        }
                        showCaloriesDialog = false
                    }
                ) {
                    Text("Kaydet")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCaloriesDialog = false }) {
                    Text("İptal")
                }
            }
        )
    }

    // Protein Güncelleme Dialogu
    if (showProteinDialog) {
        var proteinInput by remember { mutableStateOf(userProfile?.dailyProteinIntake.toString()) }
        AlertDialog(
            onDismissRequest = { showProteinDialog = false },
            title = { Text("Protein Alımı Güncelle") },
            text = {
                OutlinedTextField(
                    value = proteinInput,
                    onValueChange = { proteinInput = it },
                    label = { Text("Protein (g)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        proteinInput.toIntOrNull()?.let { value ->
                            println("Protein değeri güncelleniyor: $value")
                            userViewModel.updateDailyProteinIntake(value)
                        }
                        showProteinDialog = false
                    }
                ) {
                    Text("Kaydet")
                }
            },
            dismissButton = {
                TextButton(onClick = { showProteinDialog = false }) {
                    Text("İptal")
                }
            }
        )
    }
}

@Composable
private fun MotivationMessageCard(message: MotivationMessage) {
    val backgroundColor = when (message.type) {
        MessageType.SUCCESS -> Color(0xFF4CAF50)
        MessageType.PROGRESS -> Color(0xFF2196F3)
        MessageType.ENCOURAGEMENT -> Color(0xFFFFC107)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        )
    ) {
        Text(
            text = message.message,
            modifier = Modifier.padding(16.dp),
            color = Color.White,
            fontSize = 16.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun CelebrationAnimation() {
    val infiniteTransition = rememberInfiniteTransition(label = "celebration")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(500),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "🎉",
            modifier = Modifier
                .scale(scale)
                .size(100.dp),
            fontSize = 80.sp
        )
    }
}

@Composable
private fun GoalCheckCard(
    title: String,
    icon: ImageVector,
    currentValue: Int,
    targetValue: Int,
    unit: String,
    onUpdateClick: () -> Unit,
    onComplete: () -> Unit,
    completed: Boolean
) {
    val progress = if (targetValue > 0) {
        (currentValue.toFloat() / targetValue.toFloat()).coerceIn(0f, 1f)
    } else 0f

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .shadow(4.dp, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(
            containerColor = if (completed) 
                MaterialTheme.colorScheme.primaryContainer 
            else 
                MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (completed) 
                            MaterialTheme.colorScheme.primary 
                        else 
                            MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        color = if (completed) 
                            MaterialTheme.colorScheme.primary 
                        else 
                            MaterialTheme.colorScheme.onSurface
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "$currentValue/$targetValue $unit",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (completed) 
                            MaterialTheme.colorScheme.primary 
                        else 
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    IconButton(
                        onClick = onUpdateClick,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Güncelle",
                            tint = if (completed) 
                                MaterialTheme.colorScheme.primary 
                            else 
                                MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = if (completed) 
                    MaterialTheme.colorScheme.primary 
                else 
                    MaterialTheme.colorScheme.secondary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            if (completed) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Hedef Tamamlandı!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Button(
                        onClick = onComplete,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            "Kapat",
                            color = Color.White,
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            } else {
                Text(
                    text = "${(progress * 100).toInt()}%",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun WeeklyProgressSection(
    title: String,
    progress: Int
) {
    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                LinearProgressIndicator(
                    progress = progress / 100f,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = "$progress%",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp), color = Color.White
                )
            }
        }
    }
}

private fun calculateProgress(progress: String): Float {
    return try {
        val parts = progress.split("/")
        if (parts.size == 2) {
            val current = parts[0].filter { it.isDigit() }.toFloatOrNull() ?: 0f
            val target = parts[1].filter { it.isDigit() }.toFloatOrNull() ?: 0f
            if (target > 0) current / target else 0f
        } else 0f
    } catch (e: Exception) {
        0f
    }
} 