package com.fitnessapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Female
import androidx.compose.material.icons.outlined.Male
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.airbnb.lottie.compose.*
import com.fitnessapp.ui.components.QuickAccessButton
import androidx.compose.ui.graphics.vector.ImageVector
import java.util.Calendar
import kotlin.random.Random
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import com.fitnessapp.R
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fitnessapp.ui.viewmodels.UserViewModel
import com.fitnessapp.ui.components.RecommendationCard

import com.fitnessapp.ui.viewmodels.DailyActivityViewModel

@Composable
fun HomeScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    userViewModel: UserViewModel = viewModel(),
    dailyActivityViewModel: DailyActivityViewModel = viewModel()
) {
    val userProfile by userViewModel.userProfile.collectAsState()
    val isFirstTime by userViewModel.isFirstTime.collectAsState()
    val recommendations by userViewModel.dailyRecommendations.collectAsState()
    val showCelebration by dailyActivityViewModel.showCelebration.collectAsState()

    LaunchedEffect(Unit) {
        userViewModel.listenUserProfile()
        userViewModel.checkFirstTimeUser()
    }

    LaunchedEffect(userProfile) {
        if (userProfile != null) {
            dailyActivityViewModel.loadUserDailyGoals()
        }
    }

    if (isFirstTime) {
        G2(navController = navController)
    } else {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                                MaterialTheme.colorScheme.background
                            )
                        )
                    )
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(12.dp, RoundedCornerShape(28.dp))
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color(0xFFe0eafc), Color(0xFFcfdef3))
                            ),
                            shape = RoundedCornerShape(28.dp)
                        )
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
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
                                    .size(70.dp)
                                    .background(Color(0xFF212121), shape = RoundedCornerShape(50)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.EmojiEmotions,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(48.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Hoş Geldin, ${userProfile?.firstName ?: "Kullanıcı"}!",
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF212121)
                                ),
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Bugün de harika bir gün olacak! Hedeflerine ulaşmak için hazırsın.",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontStyle = FontStyle.Italic,
                                    color = Color(0xFF616161)
                                ),
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "BMI: ${userProfile?.bmi?.let { "%.1f".format(it) } ?: "0.0"}",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontStyle = FontStyle.Italic,
                                    color = Color(0xFF212121)
                                ),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }


                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Günlük Hedefler",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = Color.Black
                        )
                        userProfile?.let { profile ->
                            val goals = userViewModel.getDailyGoals(profile)
                            val waterTarget = goals["water"] ?: 2000
                            val calorieTarget = goals["calories"] ?: 2000
                            val stepTarget = goals["steps"] ?: 10000
                            val proteinTarget = goals["protein"] ?: 60

                            val waterProgress = (profile.dailyWaterIntake.toFloat() / waterTarget).coerceIn(0f, 1f)
                            val calorieProgress = (profile.dailyCaloriesBurned.toFloat() / calorieTarget).coerceIn(0f, 1f)
                            val stepProgress = (profile.dailySteps.toFloat() / stepTarget).coerceIn(0f, 1f)
                            val proteinProgress = (profile.dailyProteinIntake.toFloat() / proteinTarget).coerceIn(0f, 1f)

                            val allCompleted = waterProgress >= 1f && calorieProgress >= 1f && stepProgress >= 1f && proteinProgress >= 1f

                            // Dairesel Progress Barlar
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                CircularStat(
                                    label = "Su",
                                    value = profile.dailyWaterIntake,
                                    target = waterTarget,
                                    unit = "ml",
                                    color = Color.Black,
                                    isCompleted = waterProgress >= 1f
                                )
                                CircularStat(
                                    label = "Kalori",
                                    value = profile.dailyCaloriesBurned,
                                    target = calorieTarget,
                                    unit = "kcal",
                                    color = Color.Black,
                                    isCompleted = calorieProgress >= 1f
                                )
                                CircularStat(
                                    label = "Adım",
                                    value = profile.dailySteps,
                                    target = stepTarget,
                                    unit = "adım",
                                    color = Color.Black,
                                    isCompleted = stepProgress >= 1f
                                )
                                CircularStat(
                                    label = "Protein",
                                    value = profile.dailyProteinIntake,
                                    target = proteinTarget,
                                    unit = "g",
                                    color = Color.Black,
                                    isCompleted = proteinProgress >= 1f
                                )
                            }
                            if (allCompleted) {
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "Günlük hedefler tamamlandı",
                                    color = Color(0xFF388E3C),
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                        }
                    }
                }

                // Günlük Özet Kartı
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Günlük Özet",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                        recommendations?.let { rec ->
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    RecommendationCard(
                                        title = "Su",
                                        value = "${rec.waterIntake}ml",
                                        icon = "💧",
                                        modifier = Modifier.weight(1f)
                                    )
                                    Spacer(modifier = Modifier.width(16.dp))
                                    RecommendationCard(
                                        title = "Kalori",
                                        value = "${rec.calories}kcal",
                                        icon = "🔥",
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    RecommendationCard(
                                        title = "Protein",
                                        value = "${rec.protein}g",
                                        icon = "🥩",
                                        modifier = Modifier.weight(1f)
                                    )
                                    Spacer(modifier = Modifier.width(16.dp))
                                    RecommendationCard(
                                        title = "Karbonhidrat",
                                        value = "${rec.carbs}g",
                                        icon = "🍚",
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }
                }


                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(2.dp, RoundedCornerShape(14.dp)),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Önerilen Aktiviteler",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = Color.Black
                        )
                        recommendations?.recommendedActivities?.let { activities ->
                            Row(
                                modifier = Modifier
                                    .horizontalScroll(rememberScrollState())
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                activities.forEach { activity ->
                                    ModernActivityCardWithDesc(activity = activity)
                                }
                            }
                        }
                    }
                }

                // Beslenme Planı Kartı
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(2.dp, RoundedCornerShape(14.dp)),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Günlük Beslenme Planı",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = Color.Black
                        )
                        recommendations?.mealPlan?.let { mealPlan ->
                            ModernMealCard(
                                icon = "🍳", title = mealPlan.breakfast.name, calories = mealPlan.breakfast.calories,
                                protein = mealPlan.breakfast.protein, carbs = mealPlan.breakfast.carbs, fat = mealPlan.breakfast.fat,
                                suggestions = mealPlan.breakfast.suggestions
                            )
                            ModernMealCard(
                                icon = "🍲", title = mealPlan.lunch.name, calories = mealPlan.lunch.calories,
                                protein = mealPlan.lunch.protein, carbs = mealPlan.lunch.carbs, fat = mealPlan.lunch.fat,
                                suggestions = mealPlan.lunch.suggestions
                            )
                            ModernMealCard(
                                icon = "🍽️", title = mealPlan.dinner.name, calories = mealPlan.dinner.calories,
                                protein = mealPlan.dinner.protein, carbs = mealPlan.dinner.carbs, fat = mealPlan.dinner.fat,
                                suggestions = mealPlan.dinner.suggestions
                            )
                            ModernMealCard(
                                icon = "🥗", title = mealPlan.snacks.name, calories = mealPlan.snacks.calories,
                                protein = mealPlan.snacks.protein, carbs = mealPlan.snacks.carbs, fat = mealPlan.snacks.fat,
                                suggestions = mealPlan.snacks.suggestions
                            )
                        }
                    }
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                        .shadow(10.dp, RoundedCornerShape(24.dp)),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = "BMI Nedir?",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Vücut Kitle İndeksi (BMI), kilonuzun boyunuza oranla değerlendirilmesidir. Aşağıdaki aralıklar genel referans değerleridir:",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Column(
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text("• 18.5 altı → Zayıf", color = Color(0xFF2196F3))
                            Text("• 18.5 - 24.9 → Normal", color = Color(0xFF4CAF50))
                            Text("• 25 - 29.9 → Fazla Kilolu", color = Color(0xFFFFC107))
                            Text("• 30 ve üzeri → Obez", color = Color(0xFFF44336))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun GoalProgressBar(label: String, progress: Float, value: String, color: Color) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                color = color
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        LinearProgressIndicator(
            progress = progress,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp)
                .height(10.dp)
                .clip(RoundedCornerShape(6.dp)),
            color = color,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
        Text(
            text = "%${(progress * 100).toInt()}",
            style = MaterialTheme.typography.bodySmall,
            color = color,
            modifier = Modifier.align(Alignment.End)
        )
    }
}

@Composable
private fun GoalItem(title: String, target: String, progress: Float) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = target,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        LinearProgressIndicator(
            progress = progress,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp)
                .height(10.dp)
                .clip(RoundedCornerShape(6.dp)),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
        Text(
            text = "%${(progress * 100).toInt()}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.align(Alignment.End)
        )
    }
}

@Composable
private fun CircularStat(label: String, value: Int, target: Int, unit: String, color: Color, isCompleted: Boolean) {
    val progress = (value.toFloat() / target).coerceIn(0f, 1f)
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(contentAlignment = Alignment.Center) {
            CircularProgressIndicator(
                progress = progress,
                strokeWidth = 8.dp,
                color = color,
                trackColor = Color.LightGray,
                modifier = Modifier.size(70.dp)
            )
            if (isCompleted) {
                Text(
                    text = "✔️",
                    color = Color(0xFF388E3C),
                    style = MaterialTheme.typography.titleLarge
                )
            } else {
                Text(
                    text = "$value",
                    color = color,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
        Text(text = label, color = color, style = MaterialTheme.typography.bodySmall)
        Text(text = "/ $target $unit", color = Color.Gray, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
private fun WeeklyBarChart(data: List<Int>, barColor: Color) {
    val days = listOf("Pzt", "Sal", "Çar", "Per", "Cum", "Cmt", "Paz")
    val max = (data.maxOrNull() ?: 1).toFloat()
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        data.forEachIndexed { idx, value ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .height((60 * (value / max)).dp)
                        .width(12.dp)
                        .background(barColor, RoundedCornerShape(4.dp))
                )
                Text(
                    text = days[idx],
                    color = Color.White,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}

@Composable
private fun ModernActivityCardWithDesc(activity: String) {
    val (icon, desc) = when (activity.lowercase()) {
        "yürüyüş" -> "🚶" to "10 dakika tempolu yürüyüş yap."
        "koşu" -> "🏃" to "5 dakika hafif koşu ile başla."
        "esneme" -> "🤸" to "Tüm vücut için 5 dakika esneme."
        "bisiklet" -> "🚴" to "15 dakika sabit tempoda bisiklet sür."
        "pilates" -> "🧘" to "Mat üzerinde 10 dakika pilates."
        "yoga" -> "🧘‍♂️" to "Rahatlatıcı yoga hareketleri."
        "hiit" -> "🔥" to "Yüksek tempolu 7 dakika HIIT."
        "kardiyo" -> "❤️" to "20 dakika kardiyo egzersizi."
        "güç antrenmanı" -> "💪" to "Ağırlıklarla güç çalışması."
        "bench press" -> "🏋️" to "Bench press ile göğüs çalış."
        "squat" -> "🏋️‍♂️" to "Squat ile bacak güçlendir."
        "deadlift" -> "🏋️‍♀️" to "Deadlift ile tüm vücut çalış."
        "pull-ups" -> "🤸‍♂️" to "Barfiks ile sırt çalış."
        "dumbbell rows" -> "🏋️‍♂️" to "Dambıl ile sırt çalış."
        "yüzme" -> "🏊" to "Havuzda 20 dakika yüz."
        "meditasyon" -> "🧘‍♀️" to "5 dakika sessiz meditasyon yap."
        "hafif kardiyo" -> "🚶‍♀️" to "10 dakika hafif tempolu yürüyüş."
        else -> "✨" to "Kısa bir egzersiz ile enerjini yükselt!"
    }
    Card(
        modifier = Modifier
            .width(180.dp)
            .height(130.dp)
            .shadow(4.dp, RoundedCornerShape(18.dp)),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF5F5F5)
        ),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Color(0xFF212121)),
                contentAlignment = Alignment.Center
            ) {
                Text(icon, color = Color.White, style = MaterialTheme.typography.titleLarge)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = activity,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.Black,
                maxLines = 1
            )
            Text(
                text = desc,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                maxLines = 2
            )
        }
    }
}

@Composable
private fun ModernMealCard(icon: String, title: String, calories: Int, protein: Int, carbs: Int, fat: Int, suggestions: List<com.fitnessapp.ui.components.FoodSuggestion>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF8F8F8)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(icon, fontSize = 28.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = Color.Black)
            }
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("${calories} kcal", color = Color(0xFF212121), style = MaterialTheme.typography.bodySmall)
                Text("${protein}g protein", color = Color(0xFF388E3C), style = MaterialTheme.typography.bodySmall)
                Text("${carbs}g karbonhidrat", color = Color(0xFF1976D2), style = MaterialTheme.typography.bodySmall)
                Text("${fat}g yağ", color = Color(0xFFF57C00), style = MaterialTheme.typography.bodySmall)
            }
            Divider(modifier = Modifier.padding(vertical = 6.dp))
            suggestions.forEach { suggestion ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = suggestion.name,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.DarkGray
                    )
                    Text(
                        text = suggestion.amount,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Black,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}


