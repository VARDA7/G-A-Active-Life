package com.fitnessapp.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.fitnessapp.ui.viewmodels.ActivityRecord
import com.fitnessapp.ui.viewmodels.MonthlyStats
import com.fitnessapp.ui.viewmodels.UserViewModel
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.pow
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.style.TextAlign

@Composable
fun ProfileScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    userViewModel: UserViewModel = viewModel()
) {
    var showWeightDialog by remember { mutableStateOf(false) }
    var weightInput by remember { mutableStateOf("") }
    var weightHistory by remember { mutableStateOf<List<Pair<Long, Int>>>(emptyList()) }

    LaunchedEffect(Unit) {
        userViewModel.getWeightHistory { list -> weightHistory = list }
    }

    val userProfile by userViewModel.userProfile.collectAsState(initial = null)
    val isEditing by userViewModel.isEditing.collectAsState(initial = false)
    val showDeleteDialog by userViewModel.showDeleteDialog.collectAsState(initial = false)
    val showLogoutDialog by userViewModel.showLogoutDialog.collectAsState(initial = false)
    val showGoalDialog by userViewModel.showGoalDialog.collectAsState(initial = false)
    val showHeightDialog by userViewModel.showHeightDialog.collectAsState(initial = false)
    val showAgeDialog by userViewModel.showAgeDialog.collectAsState(initial = false)
    val showGenderDialog by userViewModel.showGenderDialog.collectAsState(initial = false)
    val showActivityLevelDialog by userViewModel.showActivityLevelDialog.collectAsState(initial = false)
    val showGoalWeightDialog by userViewModel.showGoalWeightDialog.collectAsState(initial = false)
    val showGoalDateDialog by userViewModel.showGoalDateDialog.collectAsState(initial = false)
    val showGoalTypeDialog by userViewModel.showGoalTypeDialog.collectAsState(initial = false)
    val showGoalIntensityDialog by userViewModel.showGoalIntensityDialog.collectAsState(initial = false)
    val showGoalDurationDialog by userViewModel.showGoalDurationDialog.collectAsState(initial = false)
    val showGoalFrequencyDialog by userViewModel.showGoalFrequencyDialog.collectAsState(initial = false)
    val showGoalEquipmentDialog by userViewModel.showGoalEquipmentDialog.collectAsState(initial = false)
    val showGoalLocationDialog by userViewModel.showGoalLocationDialog.collectAsState(initial = false)
    val showGoalTimeDialog by userViewModel.showGoalTimeDialog.collectAsState(initial = false)
    val showGoalDietDialog by userViewModel.showGoalDietDialog.collectAsState(initial = false)
    val showGoalSleepDialog by userViewModel.showGoalSleepDialog.collectAsState(initial = false)
    val showGoalStressDialog by userViewModel.showGoalStressDialog.collectAsState(initial = false)
    val showGoalWaterDialog by userViewModel.showGoalWaterDialog.collectAsState(initial = false)
    val showGoalStepsDialog by userViewModel.showGoalStepsDialog.collectAsState(initial = false)
    val showGoalCaloriesBurnedDialog by userViewModel.showGoalCaloriesBurnedDialog.collectAsState(initial = false)
    val showGoalProteinIntakeDialog by userViewModel.showGoalProteinIntakeDialog.collectAsState(initial = false)
    val showGoalCarbsIntakeDialog by userViewModel.showGoalCarbsIntakeDialog.collectAsState(initial = false)
    val showGoalFatIntakeDialog by userViewModel.showGoalFatIntakeDialog.collectAsState(initial = false)
    val showGoalFiberIntakeDialog by userViewModel.showGoalFiberIntakeDialog.collectAsState(initial = false)
    val showGoalSugarIntakeDialog by userViewModel.showGoalSugarIntakeDialog.collectAsState(initial = false)
    val showGoalSodiumIntakeDialog by userViewModel.showGoalSodiumIntakeDialog.collectAsState(initial = false)
    val showGoalPotassiumIntakeDialog by userViewModel.showGoalPotassiumIntakeDialog.collectAsState(initial = false)
    val showGoalCalciumIntakeDialog by userViewModel.showGoalCalciumIntakeDialog.collectAsState(initial = false)
    val showGoalIronIntakeDialog by userViewModel.showGoalIronIntakeDialog.collectAsState(initial = false)
    val showGoalMagnesiumIntakeDialog by userViewModel.showGoalMagnesiumIntakeDialog.collectAsState(initial = false)
    val showGoalZincIntakeDialog by userViewModel.showGoalZincIntakeDialog.collectAsState(initial = false)
    val showGoalVitaminAIntakeDialog by userViewModel.showGoalVitaminAIntakeDialog.collectAsState(initial = false)
    val showGoalVitaminCIntakeDialog by userViewModel.showGoalVitaminCIntakeDialog.collectAsState(initial = false)
    val showGoalVitaminDIntakeDialog by userViewModel.showGoalVitaminDIntakeDialog.collectAsState(initial = false)
    val showGoalVitaminEIntakeDialog by userViewModel.showGoalVitaminEIntakeDialog.collectAsState(initial = false)
    val showGoalVitaminKIntakeDialog by userViewModel.showGoalVitaminKIntakeDialog.collectAsState(initial = false)
    val showGoalVitaminB1IntakeDialog by userViewModel.showGoalVitaminB1IntakeDialog.collectAsState(initial = false)
    val showGoalVitaminB2IntakeDialog by userViewModel.showGoalVitaminB2IntakeDialog.collectAsState(initial = false)
    val showGoalVitaminB3IntakeDialog by userViewModel.showGoalVitaminB3IntakeDialog.collectAsState(initial = false)
    val showGoalVitaminB5IntakeDialog by userViewModel.showGoalVitaminB5IntakeDialog.collectAsState(initial = false)
    val showGoalVitaminB6IntakeDialog by userViewModel.showGoalVitaminB6IntakeDialog.collectAsState(initial = false)
    val showGoalVitaminB7IntakeDialog by userViewModel.showGoalVitaminB7IntakeDialog.collectAsState(initial = false)
    val showGoalVitaminB9IntakeDialog by userViewModel.showGoalVitaminB9IntakeDialog.collectAsState(initial = false)
    val showGoalVitaminB12IntakeDialog by userViewModel.showGoalVitaminB12IntakeDialog.collectAsState(initial = false)
    val showGoalCholineIntakeDialog by userViewModel.showGoalCholineIntakeDialog.collectAsState(initial = false)
    val showGoalBetaineIntakeDialog by userViewModel.showGoalBetaineIntakeDialog.collectAsState(initial = false)
    val showGoalAlcoholIntakeDialog by userViewModel.showGoalAlcoholIntakeDialog.collectAsState(initial = false)
    val showGoalCaffeineIntakeDialog by userViewModel.showGoalCaffeineIntakeDialog.collectAsState(initial = false)

    val bmi = if (userProfile?.height != null && userProfile?.height != 0) {
        (userProfile?.weight?.toFloat() ?: 0f) / ((userProfile?.height?.toFloat() ?: 0f) / 100f).pow(2)
    } else 0f

    val monthlyStats by userViewModel.monthlyStats.collectAsState(initial = null)

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
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    // Profil Başlığı
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .shadow(12.dp, RoundedCornerShape(28.dp)),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        ),
                        shape = RoundedCornerShape(28.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(120.dp)
                                    .background(MaterialTheme.colorScheme.primaryContainer, shape = CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Profil Fotoğrafı",
                                    modifier = Modifier
                                        .padding(24.dp)
                                        .fillMaxSize(),
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            userProfile?.let { profile ->
                                Text(
                                    text = "${profile.firstName} ${profile.lastName}",
                                    style = MaterialTheme.typography.headlineSmall.copy(
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Button(
                                    onClick = { showWeightDialog = true },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                                ) {
                                    Icon(Icons.Default.Edit, contentDescription = null, tint = Color.White)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Kilo Güncelle", color = Color.White)
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = profile.email,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(text = "Kilo", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                                        Text(text = "${profile.weight} kg", style = MaterialTheme.typography.titleMedium)
                                    }
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(text = "Boy", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                                        Text(text = "${profile.height} cm", style = MaterialTheme.typography.titleMedium)
                                    }
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(text = "BMI", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                                        Text(text = "${"%.1f".format(profile.bmi)}", style = MaterialTheme.typography.titleMedium)
                                    }
                                }
                            }
                        }
                    }

                    // Kilo güncelleme diyalogu
                    if (showWeightDialog) {
                        AlertDialog(
                            onDismissRequest = { showWeightDialog = false },
                            title = { Text("Kilo Güncelle") },
                            text = {
                                OutlinedTextField(
                                    value = weightInput,
                                    onValueChange = { weightInput = it },
                                    label = { Text("Kilo (kg)") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                                )
                            },
                            confirmButton = {
                                TextButton(
                                    onClick = {
                                        weightInput.toIntOrNull()?.let { value ->
                                            userViewModel.addWeightEntry(value)
                                            userProfile?.let { profile ->
                                                userViewModel.saveUserProfile(profile.copy(weight = value))
                                            }
                                        }
                                        showWeightDialog = false
                                        weightInput = ""
                                    }
                                ) { Text("Kaydet") }
                            },
                            dismissButton = {
                                TextButton(onClick = { showWeightDialog = false }) { Text("İptal") }
                            }
                        )
                    }

                    // Kısa özet kartı
                    userProfile?.let { profile ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                                .shadow(8.dp, RoundedCornerShape(24.dp)),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White
                            ),
                            shape = RoundedCornerShape(24.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Text(
                                    text = "Kısa Özet",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Kilo: ${profile.weight} kg",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = "Boy: ${profile.height} cm",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = "BMI: ${"%.1f".format(profile.bmi)}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }

                    // Kilo gelişim grafiği
                    WeightChartCard(weightHistory = weightHistory)

                    // Aktivite ve görev takibi
                    ActivityProgressCard(monthlyStats = monthlyStats)

                    // Profil Bilgileri
                    userProfile?.let { profile ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                                .shadow(8.dp, RoundedCornerShape(24.dp)),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White
                            ),
                            shape = RoundedCornerShape(24.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Text(
                                    text = "Temel Bilgiler",
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.primary
                                )
                                InfoRow(icon = Icons.Default.MonitorWeight, label = "Kilo", value = "${profile.weight} kg")
                                InfoRow(icon = Icons.Default.Height, label = "Boy", value = "${profile.height} cm")
                                InfoRow(icon = Icons.Default.Cake, label = "Yaş", value = "${profile.age} yaş")
                            }
                        }

                        // Hedef Bilgileri
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                                .shadow(8.dp, RoundedCornerShape(24.dp)),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White
                            ),
                            shape = RoundedCornerShape(24.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Hedef Bilgileri",
                                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    IconButton(onClick = { userViewModel.setEditing(true) }) {
                                        Icon(Icons.Default.Edit, contentDescription = "Düzenle", tint = MaterialTheme.colorScheme.primary)
                                    }
                                }

                                InfoRow(icon = Icons.Default.Flag, label = "Hedef Türü", value = if (profile.goal.isNotBlank()) profile.goal else "Belirtilmedi")
                            }
                        }
                    }

                    // Çıkış Yap Butonu
                    Button(
                        onClick = { userViewModel.showLogoutDialog() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Logout,
                                contentDescription = null,
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Çıkış Yap",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White
                            )
                        }
                    }
                }

                // Çıkış Yapma Onay Dialogu
                if (showLogoutDialog) {
                    AlertDialog(
                        onDismissRequest = { userViewModel.hideLogoutDialog() },
                        title = { Text("Çıkış Yap") },
                        text = { Text("Çıkış yapmak istediğinizden emin misiniz?") },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    userViewModel.hideLogoutDialog()
                                    userViewModel.logout()
                                    navController.navigate("login") {
                                        popUpTo("profile") { inclusive = true }
                                    }
                                }
                            ) {
                                Text("Evet")
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { userViewModel.hideLogoutDialog() }) {
                                Text("Hayır")
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileHeader(userName: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .padding(8.dp)
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profil Fotoğrafı",
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxSize(),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = userName,
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun UserStats(
    profile: UserProfile,
    userViewModel: UserViewModel
) {
    var showWaterDialog by remember { mutableStateOf(false) }
    var showStepsDialog by remember { mutableStateOf(false) }
    var showCaloriesDialog by remember { mutableStateOf(false) }
    var showProteinDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatisticItem("Kilo", "${profile.weight} kg")
            StatisticItem("Boy", "${profile.height} cm")
            StatisticItem("BMI", "%.1f".format(profile.bmi))
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Günlük Hedefler",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    val goals = userViewModel.getDailyGoals(profile)
                    
                    // Su Hedefi
                    val waterTarget = goals["water"] ?: 2000
                    val waterProgress = if (waterTarget > 0) {
                        (profile.dailyWaterIntake.toDouble() / waterTarget.toDouble()).toFloat()
                    } else 0f
                    GoalItem(
                        title = "Su Tüketimi",
                        target = "${profile.dailyWaterIntake}ml / ${waterTarget}ml",
                        progress = waterProgress.coerceIn(0f, 1f),
                        onUpdateClick = { showWaterDialog = true }
                    )

                    // Adım Hedefi
                    val stepTarget = goals["steps"] ?: 10000
                    val stepProgress = if (stepTarget > 0) {
                        (profile.dailySteps.toDouble() / stepTarget.toDouble()).toFloat()
                    } else 0f
                    GoalItem(
                        title = "Adım",
                        target = "${profile.dailySteps} / ${stepTarget}",
                        progress = stepProgress.coerceIn(0f, 1f),
                        onUpdateClick = { showStepsDialog = true }
                    )

                    // Kalori Hedefi
                    val calorieTarget = goals["calories"] ?: 2000
                    val calorieProgress = if (calorieTarget > 0) {
                        (profile.dailyCaloriesBurned.toDouble() / calorieTarget.toDouble()).toFloat()
                    } else 0f
                    GoalItem(
                        title = "Kalori",
                        target = "${profile.dailyCaloriesBurned} / ${calorieTarget} kcal",
                        progress = calorieProgress.coerceIn(0f, 1f),
                        onUpdateClick = { showCaloriesDialog = true }
                    )

                    // Protein Hedefi
                    val proteinTarget = goals["protein"] ?: 0
                    val proteinProgress = if (proteinTarget > 0) {
                        (profile.dailyProteinIntake.toDouble() / proteinTarget.toDouble()).toFloat()
                    } else 0f
                    GoalItem(
                        title = "Protein",
                        target = "${profile.dailyProteinIntake}g / ${proteinTarget}g",
                        progress = proteinProgress.coerceIn(0f, 1f),
                        onUpdateClick = { showProteinDialog = true }
                    )
                }
            }
        }
    }

    // Su Güncelleme Dialogu
    if (showWaterDialog) {
        var waterInput by remember { mutableStateOf(profile.dailyWaterIntake.toString()) }
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
        var stepsInput by remember { mutableStateOf(profile.dailySteps.toString()) }
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
        var caloriesInput by remember { mutableStateOf(profile.dailyCaloriesBurned.toString()) }
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
        var proteinInput by remember { mutableStateOf(profile.dailyProteinIntake.toString()) }
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
private fun MonthlyStatsCard(monthlyStats: MonthlyStats?) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "30 Günlük İstatistikler",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            monthlyStats?.let { stats ->
                MonthlyStatsContent(stats = stats)
            } ?: run {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

@Composable
private fun MonthlyStatsContent(stats: MonthlyStats) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatisticItem(
                label = "Toplam Kalori",
                value = "${stats.totalCaloriesBurned} kcal"
            )
            StatisticItem(
                label = "Toplam Süre",
                value = "${stats.totalExerciseMinutes} dk"
            )
            StatisticItem(
                label = "Ort. Kalori",
                value = "${stats.averageCaloriesPerDay} kcal"
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Günlük Kalori Yakımı",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.fillMaxWidth()
        )
        ActivityGraph(
            data = stats.dailyStats.map { it.caloriesBurned },
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Günlük Egzersiz Süresi",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.fillMaxWidth()
        )
        ActivityGraph(
            data = stats.dailyStats.map { it.exerciseMinutes },
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        )
    }
}

@Composable
private fun ActivityHistoryCard(activityHistory: List<ActivityRecord>) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Aktivite Geçmişi",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            if (activityHistory.isEmpty()) {
                Text(
                    text = "Henüz aktivite kaydı bulunmuyor",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                activityHistory.forEach { activity ->
                    ActivityHistoryItem(activity = activity)
                }
            }
        }
    }
}

@Composable
private fun LogoutButton(onLogoutClick: () -> Unit) {
    Button(
        onClick = onLogoutClick,
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.error
        )
    ) {
        Icon(
            imageVector = Icons.Default.Logout,
            contentDescription = "Çıkış Yap",
            tint = Color.White
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text("Hesaptan Çıkış Yap", color = Color.White)
    }
}

@Composable
private fun LogoutDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Çıkış Yap") },
        text = { Text("Hesaptan çıkış yapmak istediğinizden emin misiniz?") },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Evet")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Hayır")
            }
        }
    )
}

@Composable
private fun GoalItem(
    title: String,
    target: String,
    progress: Float,
    onUpdateClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = target,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    IconButton(
                        onClick = onUpdateClick,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Güncelle",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        }
    }
}

@Composable
private fun StatisticItem(
    label: String,
    value: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun ActivityHistoryItem(activity: ActivityRecord) {
    val dateFormat = remember { SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()) }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = activity.type,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = dateFormat.format(activity.date),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Süre: ${activity.duration} dk",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Kalori: ${activity.caloriesBurned} kcal",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            
            if (activity.notes.isNotEmpty()) {
                Text(
                    text = activity.notes,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun ActivityGraph(
    data: List<Int>,
    modifier: Modifier = Modifier
) {
    // Veri noktası yoksa veya tek nokta varsa basit mesaj göster
    if (data.isEmpty()) {
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center
        ) {
            Text("Henüz veri yok", color = Color.Gray)
        }
        return
    }
    
    if (data.size == 1) {
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Sadece tek veri noktası", color = Color.Gray)
                Text("${data[0]}", style = MaterialTheme.typography.headlineMedium)
            }
        }
        return
    }

    val primaryColor = MaterialTheme.colorScheme.primary
    val bgColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
    val maxValue = (data.maxOrNull()?.toFloat() ?: 0f).coerceAtLeast(1f)  // En az 1 olsun ki 0'a bölme hatası olmasın
    
    Canvas(modifier = modifier.padding(vertical = 8.dp, horizontal = 8.dp)) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        val pointCount = data.size
        
        // Yatay ızgara çizgileri
        val gridLines = 5
        val gridStep = canvasHeight / gridLines
        repeat(gridLines + 1) { i ->
            val y = i * gridStep
            drawLine(
                color = bgColor,
                start = Offset(0f, y),
                end = Offset(canvasWidth, y),
                strokeWidth = 1.dp.toPx()
            )
            
            // Değer etiketlerini eklemek için burada bir yol yok,
            // bu nedenle sadece çizgileri çiziyoruz
        }
        
        // Veriler için çizgi oluşturma
        if (pointCount >= 2) {
            val pointDistance = canvasWidth / (pointCount - 1)
            val path = Path()
            
            // Her bir veri için nokta konumu hesapla
            val points = data.mapIndexed { index, value ->
                Offset(
                    x = index * pointDistance,
                    y = canvasHeight - (value / maxValue) * canvasHeight
                )
            }
            
            // İlk noktadan başla
            path.moveTo(points.first().x, points.first().y)
            
            // Diğer noktalara bağla (pürüzsüz eğri için)
            for (i in 1 until points.size) {
                val prev = points[i - 1]
                val curr = points[i]
                
                // Basit doğrusal çizgi
                path.lineTo(curr.x, curr.y)
            }
            
            // Çizgiyi çiz
            drawPath(
                path = path,
                color = primaryColor,
                style = Stroke(
                    width = 3.dp.toPx(),
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )
            
            // Her bir noktayı çiz
            points.forEach { point ->
                drawCircle(
                    color = primaryColor,
                    radius = 4.dp.toPx(),
                    center = point
                )
                
                // Noktanın etrafında beyaz bir halka
                drawCircle(
                    color = Color.White,
                    radius = 2.dp.toPx(),
                    center = point
                )
            }
            
            // Alanı doldur (opsiyonel)
            val fillPath = Path().apply {
                moveTo(points.first().x, canvasHeight) // sol alt köşe
                lineTo(points.first().x, points.first().y) // ilk nokta
                
                // Tüm noktaları takip et
                for (i in 1 until points.size) {
                    lineTo(points[i].x, points[i].y)
                }
                
                lineTo(points.last().x, canvasHeight) // sağ alt köşe
                close() // sol alt köşeye geri dön
            }
            
            drawPath(
                path = fillPath,
                color = primaryColor.copy(alpha = 0.15f),
                style = Fill
            )
        }
    }
}

@Composable
private fun InfoRow(
    icon: ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge
            )
        }
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium)
        )
    }
}

@Composable
fun BarChart(months: List<String>, values: List<Int>, modifier: Modifier = Modifier) {
    val maxValue = (values.maxOrNull() ?: 1).coerceAtLeast(1)
    val barColor = MaterialTheme.colorScheme.primary
    val labelColor = MaterialTheme.colorScheme.onSurface

    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            values.forEachIndexed { idx, value ->
                val barHeightRatio = value.toFloat() / maxValue
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .height((120.dp * barHeightRatio).coerceAtLeast(8.dp))
                            .width(24.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(barColor)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("${value}", style = MaterialTheme.typography.bodySmall, color = labelColor)
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            months.forEach { month ->
                Text(month, style = MaterialTheme.typography.bodySmall, color = labelColor, modifier = Modifier.width(32.dp), maxLines = 1)
            }
        }
    }
}

@Composable
private fun WeightChartCard(
    weightHistory: List<Pair<Long, Int>>,
    modifier: Modifier = Modifier
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val surfaceVariantColor = MaterialTheme.colorScheme.onSurfaceVariant
    
    // Mevcut ay ve geçmiş ay için tarih formatları
    val currentMonthFormat = remember { SimpleDateFormat("MMMM yyyy", Locale("tr")) }
    val previousMonthFormat = remember { SimpleDateFormat("MMMM yyyy", Locale("tr")) }
    val dateFormat = remember { SimpleDateFormat("dd MMM", Locale("tr")) }
    
    // Mevcut ay ve geçmiş ay için verileri hesapla
    val currentDate = Calendar.getInstance()
    val previousMonth = Calendar.getInstance().apply {
        add(Calendar.MONTH, -1)
    }
    
    val currentMonthData = weightHistory.filter { 
        val date = Calendar.getInstance().apply { timeInMillis = it.first }
        date.get(Calendar.MONTH) == currentDate.get(Calendar.MONTH) &&
        date.get(Calendar.YEAR) == currentDate.get(Calendar.YEAR)
    }
    
    val previousMonthData = weightHistory.filter {
        val date = Calendar.getInstance().apply { timeInMillis = it.first }
        date.get(Calendar.MONTH) == previousMonth.get(Calendar.MONTH) &&
        date.get(Calendar.YEAR) == previousMonth.get(Calendar.YEAR)
    }
    
    // Kilo değişimlerini hesapla
    val currentMonthChange = if (currentMonthData.size >= 2) {
        currentMonthData.last().second - currentMonthData.first().second
    } else 0
    
    val previousMonthChange = if (previousMonthData.size >= 2) {
        previousMonthData.last().second - previousMonthData.first().second
    } else 0
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .shadow(8.dp, RoundedCornerShape(24.dp)),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Başlık ve mevcut ay
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Kilo Takibi",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = primaryColor
                )
                Text(
                    text = currentMonthFormat.format(currentDate.time),
                    style = MaterialTheme.typography.titleMedium,
                    color = surfaceVariantColor
                )
            }
            
            if (weightHistory.isNotEmpty()) {
                val weights = remember(weightHistory) { weightHistory.map { it.second } }
                val dates = remember(weightHistory) { weightHistory.map { dateFormat.format(Date(it.first)) } }
                
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val width = size.width
                        val height = size.height
                        val maxWeight = weights.maxOrNull()?.toFloat() ?: 0f
                        val minWeight = weights.minOrNull()?.toFloat() ?: 0f
                        val weightRange = (maxWeight - minWeight).coerceAtLeast(1f)
                        
                        // Yatay çizgiler
                        val gridLines = 5
                        val gridStep = height / gridLines
                        repeat(gridLines + 1) { i ->
                            val y = i * gridStep
                            drawLine(
                                color = Color.Gray.copy(alpha = 0.2f),
                                start = Offset(0f, y),
                                end = Offset(width, y),
                                strokeWidth = 1.dp.toPx()
                            )
                            
                            // Y ekseni değerleri
                            val weightValue = maxWeight - (i * weightRange / gridLines)
                            drawContext.canvas.nativeCanvas.drawText(
                                "%.1f".format(weightValue),
                                8f,
                                y + 12f,
                                android.graphics.Paint().apply {
                                    color = android.graphics.Color.GRAY
                                    textSize = 24f
                                }
                            )
                        }
                        
                        // Veri noktaları ve çizgiler
                        val pointCount = weights.size
                        val xStep = width / (pointCount - 1).coerceAtLeast(1)
                        
                        // Çizgi çizimi
                        val path = Path()
                        weights.forEachIndexed { index, weight ->
                            val x = index * xStep
                            val y = height - ((weight - minWeight) / weightRange * height)
                            
                            if (index == 0) {
                                path.moveTo(x, y)
                            } else {
                                path.lineTo(x, y)
                            }
                            
                            // Nokta çizimi
                            drawCircle(
                                color = primaryColor,
                                radius = 4.dp.toPx(),
                                center = Offset(x, y)
                            )
                        }
                        
                        // Çizgiyi çiz
                        drawPath(
                            path = path,
                            color = primaryColor,
                            style = Stroke(
                                width = 2.dp.toPx(),
                                cap = StrokeCap.Round,
                                join = StrokeJoin.Round
                            )
                        )
                    }
                }
                
                // Tarih etiketleri
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    dates.forEachIndexed { index, date ->
                        if (index % 7 == 0 || index == dates.size - 1) {
                            Text(
                                text = date,
                                style = MaterialTheme.typography.bodySmall,
                                color = surfaceVariantColor,
                                modifier = Modifier.width(64.dp),
                                textAlign = TextAlign.Center
                            )
                        } else {
                            Spacer(modifier = Modifier.width(64.dp))
                        }
                    }
                }
                
                // Geçmiş ay özeti
                if (previousMonthData.isNotEmpty()) {
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${previousMonthFormat.format(previousMonth.time)} Özeti",
                            style = MaterialTheme.typography.titleMedium,
                            color = surfaceVariantColor
                        )
                        Text(
                            text = if (previousMonthChange > 0) 
                                "+${previousMonthChange} kg" 
                            else 
                                "${previousMonthChange} kg",
                            style = MaterialTheme.typography.titleMedium,
                            color = if (previousMonthChange < 0) 
                                Color.Green 
                            else if (previousMonthChange > 0) 
                                Color.Red 
                            else 
                                surfaceVariantColor
                        )
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Henüz kilo verisi bulunmuyor",
                        style = MaterialTheme.typography.bodyLarge,
                        color = surfaceVariantColor
                    )
                }
            }
        }
    }
}

@Composable
private fun ActivityProgressCard(
    monthlyStats: MonthlyStats?,
    modifier: Modifier = Modifier
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val surfaceVariantColor = MaterialTheme.colorScheme.onSurfaceVariant
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .shadow(8.dp, RoundedCornerShape(24.dp)),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Aylık Aktivite Özeti",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = primaryColor
            )
            
            if (monthlyStats != null) {
                // Toplam istatistikler
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatisticItem(
                        label = "Toplam Görev",
                        value = "${monthlyStats.totalTasks}"
                    )
                    StatisticItem(
                        label = "Tamamlanan",
                        value = "${monthlyStats.completedTasks}"
                    )
                    StatisticItem(
                        label = "Başarı",
                        value = "%${(monthlyStats.completedTasks.toFloat() / monthlyStats.totalTasks.toFloat() * 100).toInt()}"
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Aylık görev grafiği
                Text(
                    text = "Aylık Görev Takibi",
                    style = MaterialTheme.typography.titleMedium,
                    color = primaryColor
                )
                
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val width = size.width
                        val height = size.height
                        val maxTasks = monthlyStats.totalTasks.toFloat().coerceAtLeast(1f)
                        
                        // Yatay çizgiler
                        val gridLines = 5
                        val gridStep = height / gridLines
                        repeat(gridLines + 1) { i ->
                            val y = i * gridStep
                            drawLine(
                                color = Color.Gray.copy(alpha = 0.2f),
                                start = Offset(0f, y),
                                end = Offset(width, y),
                                strokeWidth = 1.dp.toPx()
                            )
                            
                            // Y ekseni değerleri
                            val taskValue = (maxTasks - (i * maxTasks / gridLines)).toInt()
                            drawContext.canvas.nativeCanvas.drawText(
                                "$taskValue",
                                8f,
                                y + 12f,
                                android.graphics.Paint().apply {
                                    color = android.graphics.Color.GRAY
                                    textSize = 24f
                                }
                            )
                        }
                        
                        // Görev çubukları
                        val barWidth = width / 2 - 16.dp.toPx()
                        val spacing = 16.dp.toPx()
                        
                        // Toplam görevler
                        drawRect(
                            color = primaryColor.copy(alpha = 0.2f),
                            topLeft = Offset(spacing, height - (monthlyStats.totalTasks.toFloat() / maxTasks * height)),
                            size = androidx.compose.ui.geometry.Size(barWidth, (monthlyStats.totalTasks.toFloat() / maxTasks * height))
                        )
                        
                        // Tamamlanan görevler
                        drawRect(
                            color = primaryColor,
                            topLeft = Offset(width - barWidth - spacing, height - (monthlyStats.completedTasks.toFloat() / maxTasks * height)),
                            size = androidx.compose.ui.geometry.Size(barWidth, (monthlyStats.completedTasks.toFloat() / maxTasks * height))
                        )
                    }
                }
                
                // Görev etiketleri
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Text(
                        text = "Toplam Görev",
                        style = MaterialTheme.typography.bodySmall,
                        color = surfaceVariantColor,
                        modifier = Modifier.width(100.dp),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "Tamamlanan",
                        style = MaterialTheme.typography.bodySmall,
                        color = surfaceVariantColor,
                        modifier = Modifier.width(100.dp),
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Henüz aktivite verisi bulunmuyor",
                        style = MaterialTheme.typography.bodyLarge,
                        color = surfaceVariantColor
                    )
                }
            }
        }
    }
}

