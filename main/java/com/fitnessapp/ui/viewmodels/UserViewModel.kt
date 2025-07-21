package com.fitnessapp.ui.viewmodels

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.fitnessapp.ui.screens.UserProfile
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Date
import java.util.Calendar
import java.text.SimpleDateFormat
import java.util.Locale
import androidx.compose.material3.ExperimentalMaterial3Api
import com.fitnessapp.ui.components.FoodSuggestion
import com.google.firebase.firestore.FieldValue


@OptIn(ExperimentalMaterial3Api::class)
data class DailyActivity(
    val id: String = "",
    val date: Date = Date(),
    val waterIntake: Int = 0,
    val exerciseType: String = "",
    val exerciseDuration: Int = 0,
    val exerciseIntensity: String = "",
    val steps: Int = 0,
    val sleepHours: Float = 0f
)

data class DailyRecommendations(
    val waterIntake: Int, // ml
    val calories: Int, // kcal
    val protein: Int, // gram
    val carbs: Int, // gram
    val fat: Int, // gram
    val exerciseMinutes: Int,
    val recommendedActivities: List<String>,
    val mealPlan: MealPlan
)

data class MealPlan(
    val breakfast: Meal,
    val lunch: Meal,
    val dinner: Meal,
    val snacks: Meal
)

data class Meal(
    val name: String,
    val calories: Int,
    val protein: Int,
    val carbs: Int,
    val fat: Int,
    val suggestions: List<FoodSuggestion>
)

data class ActivityRecord(
    val id: String = "",
    val type: String = "",
    val duration: Int = 0,
    val caloriesBurned: Int = 0,
    val date: Date = Date(),
    val notes: String = "",
    val isCompleted: Boolean = false
)

data class MonthlyStats(
    val totalCaloriesBurned: Int = 0,
    val totalExerciseMinutes: Int = 0,
    val totalWaterIntake: Int = 0,
    val averageCaloriesPerDay: Int = 0,
    val averageExerciseMinutesPerDay: Int = 0,
    val averageWaterIntakePerDay: Int = 0,
    val dailyStats: List<DailyStats> = emptyList(),
    val totalTasks: Int = 0,
    val completedTasks: Int = 0
)

data class DailyStats(
    val date: Date,
    val caloriesBurned: Int,
    val exerciseMinutes: Int,
    val waterIntake: Int
)

class UserViewModel : ViewModel() {
    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> = _userProfile

    private val _isFirstTime = MutableStateFlow(false)
    val isFirstTime: StateFlow<Boolean> = _isFirstTime

    private val _isEditing = MutableStateFlow(false)
    val isEditing: StateFlow<Boolean> = _isEditing

    private val _showDeleteDialog = MutableStateFlow(false)
    val showDeleteDialog: StateFlow<Boolean> = _showDeleteDialog

    private val _showLogoutDialog = MutableStateFlow(false)
    val showLogoutDialog: StateFlow<Boolean> = _showLogoutDialog

    private val _showGoalDialog = MutableStateFlow(false)
    val showGoalDialog: StateFlow<Boolean> = _showGoalDialog

    private val _showHeightDialog = MutableStateFlow(false)
    val showHeightDialog: StateFlow<Boolean> = _showHeightDialog

    private val _showWeightDialog = MutableStateFlow(false)
    val showWeightDialog: StateFlow<Boolean> = _showWeightDialog

    private val _showAgeDialog = MutableStateFlow(false)
    val showAgeDialog: StateFlow<Boolean> = _showAgeDialog

    private val _showGenderDialog = MutableStateFlow(false)
    val showGenderDialog: StateFlow<Boolean> = _showGenderDialog

    private val _showActivityLevelDialog = MutableStateFlow(false)
    val showActivityLevelDialog: StateFlow<Boolean> = _showActivityLevelDialog

    private val _showGoalWeightDialog = MutableStateFlow(false)
    val showGoalWeightDialog: StateFlow<Boolean> = _showGoalWeightDialog

    private val _showGoalDateDialog = MutableStateFlow(false)
    val showGoalDateDialog: StateFlow<Boolean> = _showGoalDateDialog

    private val _showGoalTypeDialog = MutableStateFlow(false)
    val showGoalTypeDialog: StateFlow<Boolean> = _showGoalTypeDialog

    private val _showGoalIntensityDialog = MutableStateFlow(false)
    val showGoalIntensityDialog: StateFlow<Boolean> = _showGoalIntensityDialog

    private val _showGoalDurationDialog = MutableStateFlow(false)
    val showGoalDurationDialog: StateFlow<Boolean> = _showGoalDurationDialog

    private val _showGoalFrequencyDialog = MutableStateFlow(false)
    val showGoalFrequencyDialog: StateFlow<Boolean> = _showGoalFrequencyDialog

    private val _showGoalEquipmentDialog = MutableStateFlow(false)
    val showGoalEquipmentDialog: StateFlow<Boolean> = _showGoalEquipmentDialog

    private val _showGoalLocationDialog = MutableStateFlow(false)
    val showGoalLocationDialog: StateFlow<Boolean> = _showGoalLocationDialog

    private val _showGoalTimeDialog = MutableStateFlow(false)
    val showGoalTimeDialog: StateFlow<Boolean> = _showGoalTimeDialog

    private val _showGoalDietDialog = MutableStateFlow(false)
    val showGoalDietDialog: StateFlow<Boolean> = _showGoalDietDialog

    private val _showGoalSleepDialog = MutableStateFlow(false)
    val showGoalSleepDialog: StateFlow<Boolean> = _showGoalSleepDialog

    private val _showGoalStressDialog = MutableStateFlow(false)
    val showGoalStressDialog: StateFlow<Boolean> = _showGoalStressDialog

    private val _showGoalWaterDialog = MutableStateFlow(false)
    val showGoalWaterDialog: StateFlow<Boolean> = _showGoalWaterDialog

    private val _showGoalStepsDialog = MutableStateFlow(false)
    val showGoalStepsDialog: StateFlow<Boolean> = _showGoalStepsDialog

    private val _showGoalCaloriesBurnedDialog = MutableStateFlow(false)
    val showGoalCaloriesBurnedDialog: StateFlow<Boolean> = _showGoalCaloriesBurnedDialog

    private val _showGoalProteinIntakeDialog = MutableStateFlow(false)
    val showGoalProteinIntakeDialog: StateFlow<Boolean> = _showGoalProteinIntakeDialog

    private val _showGoalCarbsIntakeDialog = MutableStateFlow(false)
    val showGoalCarbsIntakeDialog: StateFlow<Boolean> = _showGoalCarbsIntakeDialog

    private val _showGoalFatIntakeDialog = MutableStateFlow(false)
    val showGoalFatIntakeDialog: StateFlow<Boolean> = _showGoalFatIntakeDialog

    private val _showGoalFiberIntakeDialog = MutableStateFlow(false)
    val showGoalFiberIntakeDialog: StateFlow<Boolean> = _showGoalFiberIntakeDialog

    private val _showGoalSugarIntakeDialog = MutableStateFlow(false)
    val showGoalSugarIntakeDialog: StateFlow<Boolean> = _showGoalSugarIntakeDialog

    private val _showGoalSodiumIntakeDialog = MutableStateFlow(false)
    val showGoalSodiumIntakeDialog: StateFlow<Boolean> = _showGoalSodiumIntakeDialog

    private val _showGoalPotassiumIntakeDialog = MutableStateFlow(false)
    val showGoalPotassiumIntakeDialog: StateFlow<Boolean> = _showGoalPotassiumIntakeDialog

    private val _showGoalCalciumIntakeDialog = MutableStateFlow(false)
    val showGoalCalciumIntakeDialog: StateFlow<Boolean> = _showGoalCalciumIntakeDialog

    private val _showGoalIronIntakeDialog = MutableStateFlow(false)
    val showGoalIronIntakeDialog: StateFlow<Boolean> = _showGoalIronIntakeDialog

    private val _showGoalMagnesiumIntakeDialog = MutableStateFlow(false)
    val showGoalMagnesiumIntakeDialog: StateFlow<Boolean> = _showGoalMagnesiumIntakeDialog

    private val _showGoalZincIntakeDialog = MutableStateFlow(false)
    val showGoalZincIntakeDialog: StateFlow<Boolean> = _showGoalZincIntakeDialog

    private val _showGoalVitaminAIntakeDialog = MutableStateFlow(false)
    val showGoalVitaminAIntakeDialog: StateFlow<Boolean> = _showGoalVitaminAIntakeDialog

    private val _showGoalVitaminCIntakeDialog = MutableStateFlow(false)
    val showGoalVitaminCIntakeDialog: StateFlow<Boolean> = _showGoalVitaminCIntakeDialog

    private val _showGoalVitaminDIntakeDialog = MutableStateFlow(false)
    val showGoalVitaminDIntakeDialog: StateFlow<Boolean> = _showGoalVitaminDIntakeDialog

    private val _showGoalVitaminEIntakeDialog = MutableStateFlow(false)
    val showGoalVitaminEIntakeDialog: StateFlow<Boolean> = _showGoalVitaminEIntakeDialog

    private val _showGoalVitaminKIntakeDialog = MutableStateFlow(false)
    val showGoalVitaminKIntakeDialog: StateFlow<Boolean> = _showGoalVitaminKIntakeDialog

    private val _showGoalVitaminB1IntakeDialog = MutableStateFlow(false)
    val showGoalVitaminB1IntakeDialog: StateFlow<Boolean> = _showGoalVitaminB1IntakeDialog

    private val _showGoalVitaminB2IntakeDialog = MutableStateFlow(false)
    val showGoalVitaminB2IntakeDialog: StateFlow<Boolean> = _showGoalVitaminB2IntakeDialog

    private val _showGoalVitaminB3IntakeDialog = MutableStateFlow(false)
    val showGoalVitaminB3IntakeDialog: StateFlow<Boolean> = _showGoalVitaminB3IntakeDialog

    private val _showGoalVitaminB5IntakeDialog = MutableStateFlow(false)
    val showGoalVitaminB5IntakeDialog: StateFlow<Boolean> = _showGoalVitaminB5IntakeDialog

    private val _showGoalVitaminB6IntakeDialog = MutableStateFlow(false)
    val showGoalVitaminB6IntakeDialog: StateFlow<Boolean> = _showGoalVitaminB6IntakeDialog

    private val _showGoalVitaminB7IntakeDialog = MutableStateFlow(false)
    val showGoalVitaminB7IntakeDialog: StateFlow<Boolean> = _showGoalVitaminB7IntakeDialog

    private val _showGoalVitaminB9IntakeDialog = MutableStateFlow(false)
    val showGoalVitaminB9IntakeDialog: StateFlow<Boolean> = _showGoalVitaminB9IntakeDialog

    private val _showGoalVitaminB12IntakeDialog = MutableStateFlow(false)
    val showGoalVitaminB12IntakeDialog: StateFlow<Boolean> = _showGoalVitaminB12IntakeDialog

    private val _showGoalCholineIntakeDialog = MutableStateFlow(false)
    val showGoalCholineIntakeDialog: StateFlow<Boolean> = _showGoalCholineIntakeDialog

    private val _showGoalBetaineIntakeDialog = MutableStateFlow(false)
    val showGoalBetaineIntakeDialog: StateFlow<Boolean> = _showGoalBetaineIntakeDialog

    private val _showGoalAlcoholIntakeDialog = MutableStateFlow(false)
    val showGoalAlcoholIntakeDialog: StateFlow<Boolean> = _showGoalAlcoholIntakeDialog

    private val _showGoalCaffeineIntakeDialog = MutableStateFlow(false)
    val showGoalCaffeineIntakeDialog: StateFlow<Boolean> = _showGoalCaffeineIntakeDialog

    private val _showGoalWaterIntakeDialog = MutableStateFlow(false)
    val showGoalWaterIntakeDialog: StateFlow<Boolean> = _showGoalWaterIntakeDialog

    private val _dailyRecommendations = MutableStateFlow<DailyRecommendations?>(null)
    val dailyRecommendations: StateFlow<DailyRecommendations?> = _dailyRecommendations

    private val _activityHistory = MutableStateFlow<List<ActivityRecord>>(emptyList())
    val activityHistory: StateFlow<List<ActivityRecord>> = _activityHistory.asStateFlow()

    private val _monthlyStats = MutableStateFlow<MonthlyStats?>(null)
    val monthlyStats: StateFlow<MonthlyStats?> = _monthlyStats.asStateFlow()

    private val _dailyActivities = MutableStateFlow<List<DailyActivity>>(emptyList())
    val dailyActivities: StateFlow<List<DailyActivity>> = _dailyActivities.asStateFlow()

    init {
        checkFirstTimeUser()
    }

    fun checkFirstTimeUser() {
        viewModelScope.launch {
            try {
                val userId = FirebaseAuth.getInstance().currentUser?.uid
                if (userId != null) {
                    val db = FirebaseFirestore.getInstance()
                    val document = db.collection("users")
                        .document(userId)
                        .get()
                        .await()

                    if (!document.exists()) {
                        // Sadece kullanıcı dökümanı yoksa ilk giriş olarak işaretle
                        _isFirstTime.value = true
                    } else {
                        // Kayıtlı kullanıcı için profili yükle
                        val profile = document.toObject(UserProfile::class.java)
                        _userProfile.value = profile
                        _isFirstTime.value = false
                        profile?.let { updateRecommendations(it) }
                    }
                }
            } catch (exception: Exception) {
                println("Error checking first time user: ${exception.message}")
            }
        }
    }

    fun saveUserProfile(profile: UserProfile) {
        viewModelScope.launch {
            try {
                val userId = FirebaseAuth.getInstance().currentUser?.uid
                if (userId != null) {
                    val db = FirebaseFirestore.getInstance()
                    db.collection("users")
                        .document(userId)
                        .set(profile)
                        .await()
                    
                    _userProfile.value = profile
                    // Profil kaydedildikten sonra ilk giriş değil
                    _isFirstTime.value = false
                    updateRecommendations(profile)
                }
            } catch (exception: Exception) {
                println("Error saving user profile: ${exception.message}")
            }
        }
    }

    fun updateRecommendations(profile: UserProfile) {
        val recommendations = DailyRecommendations(
            waterIntake = calculateDailyWaterIntake(profile),
            calories = calculateDailyCalories(profile),
            protein = calculateDailyProtein(profile),
            carbs = calculateDailyCarbs(profile),
            fat = calculateDailyFat(profile),
            exerciseMinutes = calculateDailyExerciseMinutes(profile),
            recommendedActivities = getRecommendedActivities(profile),
            mealPlan = generateMealPlan(profile)
        )
        _dailyRecommendations.value = recommendations
    }

    fun calculateDailyWaterIntake(profile: UserProfile): Int {
        // Kilo bazlı su tüketimi hedefi (30ml/kg)
        return (profile.weight * 30).toInt()
    }

    fun calculateDailyCalories(profile: UserProfile): Int {
        // BMR hesaplama (Basal Metabolic Rate)
        val bmr = when (profile.gender) {
            "Erkek" -> 10 * profile.weight + 6.25 * profile.height - 5 * profile.age + 5
            else -> 10 * profile.weight + 6.25 * profile.height - 5 * profile.age - 161
        }

        // Aktivite seviyesine göre çarpan
        val activityMultiplier = when (profile.activityLevel) {
            "Sedanter" -> 1.2f
            "Hafif Aktif" -> 1.375f
            "Orta Aktif" -> 1.55f
            "Çok Aktif" -> 1.725f
            else -> 1.9f
        }

        // Hedef bazlı kalori ayarlaması
        val goalAdjustment = when (profile.goal) {
            "Zayıflama" -> 0.85f // %15 kalori azaltma
            "Kas Gelişimi" -> 1.15f // %15 kalori artırma
            else -> 1.0f // Fit Görünüm için değişiklik yok
        }

        return (bmr * activityMultiplier * goalAdjustment).toInt()
    }

    fun calculateDailySteps(profile: UserProfile): Int {
        return when {
            profile.age < 30 -> 10000
            profile.age < 50 -> 8000
            else -> 6000
        }
    }

    fun calculateDailyExerciseMinutes(profile: UserProfile): Int {
        return when (profile.goal) {
            "Kas Gelişimi" -> 60
            "Zayıflama" -> 45
            "Fit Görünüm" -> 40
            else -> 30
        }
    }

    fun calculateDailyProtein(profile: UserProfile): Int {
        // Protein hesaplama: Kilo başına 1.6-2.2g
        val proteinPerKg = when (profile.goal) {
            "Kas Gelişimi" -> 2.2
            "Zayıflama" -> 2.0
            "Fit Görünüm" -> 1.8
            else -> 1.6
        }
        return (profile.weight * proteinPerKg).toInt()
    }

    fun calculateDailyCarbs(profile: UserProfile): Int {
        val calories = calculateDailyCalories(profile)
        // Karbonhidrat: Toplam kalorinin %45-65'i
        return (calories * 0.55 / 4).toInt() // 4 kalori/gram
    }

    fun calculateDailyFat(profile: UserProfile): Int {
        val calories = calculateDailyCalories(profile)
        // Yağ: Toplam kalorinin %20-35'i
        return (calories * 0.25 / 9).toInt() // 9 kalori/gram
    }

    fun getDailyGoals(profile: UserProfile): Map<String, Int> {
        return mapOf(
            "water" to calculateDailyWaterIntake(profile),
            "calories" to calculateDailyCalories(profile),
            "steps" to calculateDailySteps(profile),
            "exercise" to calculateDailyExerciseMinutes(profile),
            "protein" to calculateDailyProtein(profile),
            "carbs" to calculateDailyCarbs(profile),
            "fat" to calculateDailyFat(profile)
        )
    }

    fun calculateWeeklyExerciseGoal(profile: UserProfile): Int {
        return calculateDailyExerciseMinutes(profile) * profile.weeklyDays
    }

    fun getRecommendedActivities(profile: UserProfile): List<String> {
        val activities = mutableListOf<String>()
        
        when (profile.goal) {
            "Kas Gelişimi" -> {
                activities.addAll(listOf(
                    "Bench Press",
                    "Squat",
                    "Deadlift",
                    "Pull-ups",
                    "Dumbbell Rows"
                ))
            }
            "Zayıflama" -> {
                activities.addAll(listOf(
                    "HIIT",
                    "Koşu",
                    "Yürüyüş",
                    "Bisiklet",
                    "Yüzme"
                ))
            }
            "Fit Görünüm" -> {
                activities.addAll(listOf(
                    "Pilates",
                    "Yoga",
                    "HIIT",
                    "Kardiyo",
                    "Güç Antrenmanı"
                ))
            }
            else -> {
                activities.addAll(listOf(
                    "Yürüyüş",
                    "Yoga",
                    "Hafif Kardiyo",
                    "Esneme",
                    "Meditasyon"
                ))
            }
        }

        // Mekan tercihlerine göre filtrele
        return activities.filter { activity ->
            when (activity) {
                in listOf("Bench Press", "Squat", "Deadlift", "Pull-ups", "Dumbbell Rows") ->
                    "Spor Salonu" in profile.locationPreferences
                in listOf("HIIT", "Yoga", "Pilates") ->
                    "Evde Egzersiz" in profile.locationPreferences
                in listOf("Koşu", "Yürüyüş", "Bisiklet") ->
                    "Açık Alan" in profile.locationPreferences
                else -> true
            }
        }
    }

    fun generateMealPlan(profile: UserProfile): MealPlan {
        val calories = calculateDailyCalories(profile)
        val protein = calculateDailyProtein(profile)
        val carbs = calculateDailyCarbs(profile)
        val fat = calculateDailyFat(profile)

        return MealPlan(
            breakfast = Meal(
                name = "Kahvaltı",
                calories = (calories * 0.25).toInt(),
                protein = (protein * 0.25).toInt(),
                carbs = (carbs * 0.25).toInt(),
                fat = (fat * 0.25).toInt(),
                suggestions = listOf(
                    FoodSuggestion("Yulaf ezmesi", "50g"),
                    FoodSuggestion("Yumurta", "2 adet"),
                    FoodSuggestion("Peynir", "30g"),
                    FoodSuggestion("Tam buğday ekmeği", "2 dilim"),
                    FoodSuggestion("Süt", "200ml")
                )
            ),
            lunch = Meal(
                name = "Öğle Yemeği",
                calories = (calories * 0.35).toInt(),
                protein = (protein * 0.35).toInt(),
                carbs = (carbs * 0.35).toInt(),
                fat = (fat * 0.35).toInt(),
                suggestions = listOf(
                    FoodSuggestion("Izgara et/tavuk", "150g"),
                    FoodSuggestion("Pilav veya makarna", "100g"),
                    FoodSuggestion("Salata", "1 porsiyon"),
                    FoodSuggestion("Sebze yemeği", "1 porsiyon"),
                    FoodSuggestion("Ayran", "200ml")
                )
            ),
            dinner = Meal(
                name = "Akşam Yemeği",
                calories = (calories * 0.3).toInt(),
                protein = (protein * 0.3).toInt(),
                carbs = (carbs * 0.3).toInt(),
                fat = (fat * 0.3).toInt(),
                suggestions = listOf(
                    FoodSuggestion("Balık", "200g"),
                    FoodSuggestion("Sebze yemeği", "1 porsiyon"),
                    FoodSuggestion("Bulgur pilavı", "100g"),
                    FoodSuggestion("Salata", "1 porsiyon"),
                    FoodSuggestion("Yoğurt", "200g")
                )
            ),
            snacks = Meal(
                name = "Ara Öğünler",
                calories = (calories * 0.1).toInt(),
                protein = (protein * 0.1).toInt(),
                carbs = (carbs * 0.1).toInt(),
                fat = (fat * 0.1).toInt(),
                suggestions = listOf(
                    FoodSuggestion("Meyve", "1 porsiyon"),
                    FoodSuggestion("Kuruyemiş", "30g"),
                    FoodSuggestion("Protein shake", "1 ölçek"),
                    FoodSuggestion("Yoğurt", "150g"),
                    FoodSuggestion("Tam tahıllı bisküvi", "2 adet")
                )
            )
        )
    }

    fun addActivityRecord(activity: ActivityRecord) {
        viewModelScope.launch {
            try {
                val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return@launch
                val activityRef = FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(userId)
                    .collection("activities")
                    .document()

                val activityWithId = activity.copy(id = activityRef.id)
                activityRef.set(activityWithId)
                    .addOnSuccessListener {
                        _activityHistory.value = _activityHistory.value + activityWithId
                    }
            } catch (e: Exception) {
                // Hata yönetimi
            }
        }
    }

    fun loadActivityHistory() {
        viewModelScope.launch {
            try {
                val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return@launch
                FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(userId)
                    .collection("activities")
                    .orderBy("date", Query.Direction.DESCENDING)
                    .limit(10)
                    .get()
                    .addOnSuccessListener { documents ->
                        val activities = documents.mapNotNull { it.toObject(ActivityRecord::class.java) }
                        _activityHistory.value = activities
                    }
            } catch (e: Exception) {
                // Hata yönetimi
            }
        }
    }

    fun loadMonthlyStats() {
        viewModelScope.launch {
            try {
                val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return@launch
                val thirtyDaysAgo = Calendar.getInstance().apply {
                    add(Calendar.DAY_OF_YEAR, -30)
                }.time

                FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(userId)
                    .collection("activities")
                    .whereGreaterThanOrEqualTo("date", thirtyDaysAgo)
                    .orderBy("date", Query.Direction.ASCENDING)
                    .get()
                    .addOnSuccessListener { documents ->
                        val activities = documents.mapNotNull { it.toObject(ActivityRecord::class.java) }
                        val dailyStatsMap = mutableMapOf<String, DailyStats>()
                        
                        // Her gün için istatistikleri hesapla
                        activities.forEach { activity ->
                            val dateStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                                .format(activity.date)
                            
                            val currentStats = dailyStatsMap.getOrPut(dateStr) {
                                DailyStats(
                                    date = activity.date,
                                    caloriesBurned = 0,
                                    exerciseMinutes = 0,
                                    waterIntake = 0
                                )
                            }
                            
                            dailyStatsMap[dateStr] = currentStats.copy(
                                caloriesBurned = currentStats.caloriesBurned + activity.caloriesBurned,
                                exerciseMinutes = currentStats.exerciseMinutes + activity.duration
                            )
                        }

                        // Eksik günleri 0 değerleriyle doldur
                        val calendar = Calendar.getInstance()
                        for (i in 0..29) {
                            calendar.add(Calendar.DAY_OF_YEAR, -1)
                            val dateStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                                .format(calendar.time)
                            if (!dailyStatsMap.containsKey(dateStr)) {
                                dailyStatsMap[dateStr] = DailyStats(
                                    date = calendar.time,
                                    caloriesBurned = 0,
                                    exerciseMinutes = 0,
                                    waterIntake = 0
                                )
                            }
                        }

                        val dailyStats = dailyStatsMap.values.sortedBy { it.date }
                        
                        // Toplam ve ortalama değerleri hesapla
                        val totalCaloriesBurned = dailyStats.sumOf { it.caloriesBurned }
                        val totalExerciseMinutes = dailyStats.sumOf { it.exerciseMinutes }
                        val totalWaterIntake = dailyStats.sumOf { it.waterIntake }
                        
                        // Görev istatistiklerini hesapla
                        val totalTasks = activities.size
                        val completedTasks = activities.count { it.isCompleted }
                        
                        _monthlyStats.value = MonthlyStats(
                            totalCaloriesBurned = totalCaloriesBurned,
                            totalExerciseMinutes = totalExerciseMinutes,
                            totalWaterIntake = totalWaterIntake,
                            averageCaloriesPerDay = totalCaloriesBurned / 30,
                            averageExerciseMinutesPerDay = totalExerciseMinutes / 30,
                            averageWaterIntakePerDay = totalWaterIntake / 30,
                            dailyStats = dailyStats,
                            totalTasks = totalTasks,
                            completedTasks = completedTasks
                        )
                    }
            } catch (e: Exception) {
                // Hata yönetimi
            }
        }
    }


    fun loadDailyActivities() {
        viewModelScope.launch {
            try {
                val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return@launch
                FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(userId)
                    .collection("daily_activities")
                    .orderBy("date", Query.Direction.DESCENDING)
                    .addSnapshotListener { snapshot, e ->
                        if (e != null) {
                            return@addSnapshotListener
                        }

                        val activities = snapshot?.documents?.mapNotNull { doc ->
                            doc.toObject(DailyActivity::class.java)
                        } ?: emptyList()

                        _dailyActivities.value = activities
                    }
            } catch (e: Exception) {
                // Hata durumunda kullanıcıya bilgi ver
            }
        }
    }

    fun updateDailyStats(
        steps: Int? = null,
        caloriesBurned: Int? = null,
        proteinIntake: Int? = null,
        waterIntake: Int? = null
    ) {
        viewModelScope.launch {
            try {
                val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return@launch
                val db = FirebaseFirestore.getInstance()
                val userRef = db.collection("users").document(userId)
                
                val updates = mutableMapOf<String, Any>()
                steps?.let { updates["dailySteps"] = it }
                caloriesBurned?.let { updates["dailyCaloriesBurned"] = it }
                proteinIntake?.let { updates["dailyProteinIntake"] = it }
                waterIntake?.let { updates["dailyWaterIntake"] = it }

                if (updates.isNotEmpty()) {
                    userRef.update(updates).await()
                    // Profili yeniden yükle
                    val updatedProfile = userRef.get().await().toObject(UserProfile::class.java)
                    _userProfile.value = updatedProfile
                }
            } catch (e: Exception) {
                // Hata yönetimi
            }
        }
    }

    fun resetDailyStats() {
        viewModelScope.launch {
            try {
                val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return@launch
                val db = FirebaseFirestore.getInstance()
                val userRef = db.collection("users").document(userId)
                
                val updates = mapOf(
                    "dailySteps" to 0,
                    "dailyCaloriesBurned" to 0,
                    "dailyProteinIntake" to 0,
                    "dailyWaterIntake" to 0
                )

                userRef.update(updates).await()
                // Profili yeniden yükle
                val updatedProfile = userRef.get().await().toObject(UserProfile::class.java)
                _userProfile.value = updatedProfile
            } catch (e: Exception) {
                // Hata yönetimi
            }
        }
    }

    fun listenUserProfile() {
        viewModelScope.launch {
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return@launch
            val db = FirebaseFirestore.getInstance()
            db.collection("users")
                .document(userId)
                .addSnapshotListener { snapshot, _ ->
                    if (snapshot != null && snapshot.exists()) {
                        val updatedProfile = snapshot.toObject(UserProfile::class.java)
                        _userProfile.value = updatedProfile
                    }
                }
        }
    }

    fun showLogoutDialog() {
        _showLogoutDialog.value = true
    }

    fun hideLogoutDialog() {
        _showLogoutDialog.value = false
    }

    fun logout() {
        FirebaseAuth.getInstance().signOut()
    }

    fun updateDailyWaterIntake(amount: Int) {
        viewModelScope.launch {
            _userProfile.value?.let { profile ->
                val updatedProfile = profile.copy(dailyWaterIntake = amount)
                _userProfile.value = updatedProfile
                saveUserProfile(updatedProfile)
            }
        }
    }

    fun updateDailySteps(steps: Int) {
        viewModelScope.launch {
            _userProfile.value?.let { profile ->
                val updatedProfile = profile.copy(dailySteps = steps)
                _userProfile.value = updatedProfile
                saveUserProfile(updatedProfile)
            }
        }
    }

    fun updateDailyCaloriesBurned(calories: Int) {
        viewModelScope.launch {
            _userProfile.value?.let { profile ->
                val updatedProfile = profile.copy(dailyCaloriesBurned = calories)
                _userProfile.value = updatedProfile
                saveUserProfile(updatedProfile)
            }
        }
    }

    fun updateDailyProteinIntake(protein: Int) {
        viewModelScope.launch {
            try {
                val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return@launch
                val db = FirebaseFirestore.getInstance()
                val userRef = db.collection("users").document(userId)
                
                userRef.update("dailyProteinIntake", protein).await()
                
                // Profili yeniden yükle
                val updatedProfile = userRef.get().await().toObject(UserProfile::class.java)
                _userProfile.value = updatedProfile
            } catch (e: Exception) {
                println("Protein güncelleme hatası: ${e.message}")
            }
        }
    }

    fun resetDailyGoals() {
        viewModelScope.launch {
            _userProfile.value?.let { currentProfile ->
                val updatedProfile = currentProfile.copy(
                    dailyWaterIntake = 0,
                    dailySteps = 0,
                    dailyCaloriesBurned = 0,
                    dailyProteinIntake = 0
                )
                saveUserProfile(updatedProfile)
            }
        }
    }

    fun setEditing(isEditing: Boolean) {
        _isEditing.value = isEditing
    }

    // Kullanıcının kilosunu geçmişe kaydet
    fun addWeightEntry(weight: Int) {
        viewModelScope.launch {
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return@launch
            val db = FirebaseFirestore.getInstance()
            val weightEntry = mapOf(
                "date" to FieldValue.serverTimestamp(),
                "weight" to weight
            )
            db.collection("users").document(userId)
                .collection("weightHistory")
                .add(weightEntry)
        }
    }

    // Son 15 günün kilo geçmişini çek
    fun getWeightHistory(onResult: (List<Pair<Long, Int>>) -> Unit) {
        viewModelScope.launch {
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return@launch
            val db = FirebaseFirestore.getInstance()
            
            // Son 15 günün verilerini al
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_YEAR, -15)
            val startDate = calendar.time
            
            db.collection("users").document(userId)
                .collection("weightHistory")
                .whereGreaterThanOrEqualTo("date", startDate)
                .orderBy("date", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener { snapshot ->
                    val list = snapshot.documents.mapNotNull { doc ->
                        val date = doc.getTimestamp("date")?.toDate()?.time ?: 0L
                        val weight = (doc.getLong("weight") ?: 0L).toInt()
                        if (date > 0 && weight > 0) Pair(date, weight) else null
                    }
                    
                    onResult(list)
                }
        }
    }
} 