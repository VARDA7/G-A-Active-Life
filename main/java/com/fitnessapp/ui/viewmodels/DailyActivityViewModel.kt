package com.fitnessapp.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.*
import kotlinx.coroutines.runBlocking
import com.fitnessapp.ui.screens.UserProfile

data class DailyGoal(
    val id: String = "",
    val title: String = "",
    val target: String = "",
    val currentProgress: String = "",
    val isCompleted: Boolean = false,
    val isApproved: Boolean = false,
    val date: Date = Date(),
    val userId: String = "",
    val lastCompletedDate: Date? = null
)

data class MotivationMessage(
    val message: String,
    val type: MessageType
)

enum class MessageType {
    SUCCESS, PROGRESS, ENCOURAGEMENT
}

class DailyActivityViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val goalsCollection = db.collection("daily_goals")
    private val userStatsCollection = db.collection("user_stats")

    private val _dailyGoals = MutableStateFlow<List<DailyGoal>>(emptyList())
    val dailyGoals: StateFlow<List<DailyGoal>> = _dailyGoals

    private val _weeklyProgress = MutableStateFlow(0)
    val weeklyProgress: StateFlow<Int> = _weeklyProgress

    private val _motivationMessage = MutableStateFlow<MotivationMessage?>(null)
    val motivationMessage: StateFlow<MotivationMessage?> = _motivationMessage

    private val _showCelebration = MutableStateFlow(false)
    val showCelebration: StateFlow<Boolean> = _showCelebration

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _isFirestoreConnected = MutableStateFlow(false)
    val isFirestoreConnected: StateFlow<Boolean> = _isFirestoreConnected

    init {
        viewModelScope.launch {
            try {
                // Firestore bağlantısını kontrol et
                checkFirestoreConnection()
                
                if (_isFirestoreConnected.value) {
                    // Önce hedefleri yükle
                    loadUserDailyGoals()
                    
                    // Her 5 dakikada bir hedefleri kontrol et
                    while (true) {
                        try {
                            checkAndResetDailyGoals()
                            delay(1000 * 60 * 5) // Her 5 dakika kontrol et
                        } catch (e: Exception) {
                            _motivationMessage.value = MotivationMessage(
                                message = "Hedef kontrolü sırasında hata: ${e.localizedMessage}",
                                type = MessageType.ENCOURAGEMENT
                            )
                        }
                    }
                } else {
                    _motivationMessage.value = MotivationMessage(
                        message = "Firestore bağlantısı kurulamadı. Lütfen internet bağlantınızı kontrol edin.",
                        type = MessageType.ENCOURAGEMENT
                    )
                }
            } catch (e: Exception) {
                _motivationMessage.value = MotivationMessage(
                    message = "Başlatma hatası: ${e.localizedMessage}",
                    type = MessageType.ENCOURAGEMENT
                )
            }
        }
    }

    private suspend fun checkFirestoreConnection() {
        try {
            // Test dokümanı oluştur
            val testDoc = db.collection("connection_test").document("test")
            testDoc.set(mapOf("timestamp" to FieldValue.serverTimestamp())).await()
            // Test dokümanını sil
            testDoc.delete().await()
            _isFirestoreConnected.value = true
        } catch (e: Exception) {
            _isFirestoreConnected.value = false
            _motivationMessage.value = MotivationMessage(
                message = "Firestore bağlantı hatası: ${e.localizedMessage}",
                type = MessageType.ENCOURAGEMENT
            )
        }
    }

    private fun checkAndResetDailyGoals() {
        viewModelScope.launch {
            try {
                val userId = auth.currentUser?.uid ?: return@launch
                val now = System.currentTimeMillis()
                val today = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }.time

                // Mevcut hedefleri kontrol et
                val goals = goalsCollection
                    .whereEqualTo("userId", userId)
                    .get()
                    .await()
                    .toObjects(DailyGoal::class.java)

                // Hedefleri kontrol et ve gerekirse sıfırla
                goals.forEach { goal ->
                    val goalDate = goal.date
                    val shouldReset = if (goalDate != null) {
                        val goalTime = goalDate.time
                        val timeDiff = now - goalTime
                        timeDiff >= 24 * 60 * 60 * 1000 // 24 saat
                    } else {
                        true
                    }

                    if (shouldReset && goal.title != "Protein Alımı") {
                        // Hedefi sıfırla
                        val targetValue = getTargetValue(goal.id)
                        goalsCollection.document(goal.id).update(mapOf(
                            "isCompleted" to false,
                            "isApproved" to false,
                            "currentProgress" to "0/$targetValue",
                            "lastCompletedDate" to null,
                            "date" to today
                        )).await()

                        // Motivasyon mesajı göster
                        _motivationMessage.value = MotivationMessage(
                            message = "${goal.title} hedefi sıfırlandı! Yeni gün için hazır mısınız? 💪",
                            type = MessageType.SUCCESS
                        )
                    }
                }

                // Hedefleri yeniden yükle
                loadUserDailyGoals()
            } catch (e: Exception) {
                _motivationMessage.value = MotivationMessage(
                    message = "Hedef kontrolü sırasında hata: ${e.localizedMessage}",
                    type = MessageType.ENCOURAGEMENT
                )
            }
        }
    }

    private suspend fun updateUserStats(goal: DailyGoal) {
        try {
            val userId = auth.currentUser?.uid ?: return
            val today = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.time

            val statsRef = userStatsCollection.document(userId)
            val stats = statsRef.get().await()

            if (!stats.exists()) {
                // Yeni istatistik dokümanı oluştur
                statsRef.set(mapOf(
                    "userId" to userId,
                    "lastUpdated" to today,
                    "totalCompletedGoals" to 1,
                    "goalsByType" to mapOf(goal.title to 1)
                )).await()
            } else {
                // Mevcut istatistikleri güncelle
                val totalCompleted = stats.getLong("totalCompletedGoals") ?: 0
                val goalsByType = stats.get("goalsByType") as? Map<String, Long> ?: mapOf()
                val updatedGoalsByType = goalsByType.toMutableMap()
                updatedGoalsByType[goal.title] = (updatedGoalsByType[goal.title] ?: 0) + 1

                statsRef.update(mapOf(
                    "lastUpdated" to today,
                    "totalCompletedGoals" to (totalCompleted + 1),
                    "goalsByType" to updatedGoalsByType
                )).await()
            }
        } catch (e: Exception) {
            // Hata yönetimi
        }
    }

    fun approveGoal(goalId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val userId = auth.currentUser?.uid ?: return@launch
                val goalRef = goalsCollection
                    .whereEqualTo("userId", userId)
                    .whereEqualTo("id", goalId)
                    .get()
                    .await()
                    .documents.firstOrNull()
                    ?.reference

                if (goalRef != null) {
                    val currentGoal = goalRef.get().await().toObject(DailyGoal::class.java)
                    if (currentGoal != null && currentGoal.isCompleted && !currentGoal.isApproved) {
                        // Hedefi onayla
                        goalRef.update("isApproved", true).await()
                        
                        // Kullanıcı istatistiklerini güncelle
                        updateUserStats(currentGoal)
                        
                        // Hedefleri yeniden yükle
                        loadUserDailyGoals()
                        
                        _motivationMessage.value = MotivationMessage(
                            message = "Hedef onaylandı! 🌟",
                            type = MessageType.SUCCESS
                        )
                    }
                }
            } catch (e: Exception) {
                _motivationMessage.value = MotivationMessage(
                    message = "Onaylama sırasında bir hata oluştu. Lütfen tekrar deneyin.",
                    type = MessageType.ENCOURAGEMENT
                )
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun rejectGoal(goalId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val userId = auth.currentUser?.uid ?: return@launch
                val goalRef = goalsCollection
                    .whereEqualTo("userId", userId)
                    .whereEqualTo("id", goalId)
                    .get()
                    .await()
                    .documents.firstOrNull()
                    ?.reference

                if (goalRef != null) {
                    val currentGoal = goalRef.get().await().toObject(DailyGoal::class.java)
                    if (currentGoal != null && currentGoal.isCompleted && !currentGoal.isApproved) {
                        // Hedefi sıfırla
                        val targetValue = getTargetValue(goalId)
                        goalRef.update(mapOf(
                            "isApproved" to false,
                            "isCompleted" to false,
                            "currentProgress" to "0/$targetValue"
                        )).await()
                        
                        // Hedefleri yeniden yükle
                        loadUserDailyGoals()
                        
                        _motivationMessage.value = MotivationMessage(
                            message = "Hedef reddedildi. Tekrar deneyin! 💪",
                            type = MessageType.ENCOURAGEMENT
                        )
                    }
                }
            } catch (e: Exception) {
                _motivationMessage.value = MotivationMessage(
                    message = "Reddetme sırasında bir hata oluştu. Lütfen tekrar deneyin.",
                    type = MessageType.ENCOURAGEMENT
                )
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun getTargetValue(goalId: String): String {
        val userId = auth.currentUser?.uid ?: return "0"
        
        // Kullanıcı profilini al
        val userRef = db.collection("users").document(userId)
        val userDoc = runBlocking { userRef.get().await() }
        val userProfile = userDoc.toObject(UserProfile::class.java) ?: return "0"

        // UserViewModel'den hedefleri al
        val userViewModel = UserViewModel()
        val goals = userViewModel.getDailyGoals(userProfile)

        return when (goalId) {
            "1" -> goals["exercise"].toString()
            "2" -> goals["water"].toString()
            "3" -> goals["steps"].toString()
            "4" -> goals["calories"].toString()
            "5" -> goals["protein"].toString()
            else -> "0"
        }
    }

    fun loadUserDailyGoals() {
        viewModelScope.launch {
            if (!_isFirestoreConnected.value) {
                _motivationMessage.value = MotivationMessage(
                    message = "Firestore bağlantısı yok. Lütfen internet bağlantınızı kontrol edin.",
                    type = MessageType.ENCOURAGEMENT
                )
                return@launch
            }

            _isLoading.value = true
            try {
                val userId = auth.currentUser?.uid
                if (userId == null) {
                    _motivationMessage.value = MotivationMessage(
                        message = "Kullanıcı oturumu bulunamadı. Lütfen tekrar giriş yapın.",
                        type = MessageType.ENCOURAGEMENT
                    )
                    return@launch
                }

                val today = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }.time

                // Bugünün hedeflerini kontrol et
                val todayGoals = goalsCollection
                    .whereEqualTo("userId", userId)
                    .whereGreaterThanOrEqualTo("date", today)
                    .get()
                    .await()

                if (todayGoals.isEmpty) {
                    // Bugün için hedef yoksa, yeni hedefler oluştur
                    createDefaultGoals(userId)
                } else {
                    // Bugünün hedeflerini yükle
                    val goals = todayGoals.toObjects(DailyGoal::class.java)
                    _dailyGoals.value = goals
                }
                
                calculateWeeklyProgress()
            } catch (e: Exception) {
                _motivationMessage.value = MotivationMessage(
                    message = "Hedefler yüklenirken bir hata oluştu. Lütfen tekrar deneyin.",
                    type = MessageType.ENCOURAGEMENT
                )
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun createDefaultGoals(userId: String) {
        viewModelScope.launch {
            try {
                val today = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }.time

                // Önce tüm eski hedefleri temizle
                val oldGoals = goalsCollection
                    .whereEqualTo("userId", userId)
                    .get()
                    .await()

                oldGoals.documents.forEach { doc ->
                    try {
                        doc.reference.delete().await()
                    } catch (e: Exception) {
                        _motivationMessage.value = MotivationMessage(
                            message = "Eski hedef silinemedi: ${e.localizedMessage}",
                            type = MessageType.ENCOURAGEMENT
                        )
                    }
                }

                // Yeni hedefleri oluştur
                val defaultGoals = getDefaultGoals()
                val updatedGoals = defaultGoals.map { goal ->
                    goal.copy(
                        userId = userId,
                        date = today,
                        isCompleted = false,
                        isApproved = false,
                        lastCompletedDate = null
                    )
                }

                // Yeni hedefleri ekle
                updatedGoals.forEach { goal ->
                    try {
                        goalsCollection.add(goal).await()
                    } catch (e: Exception) {
                        _motivationMessage.value = MotivationMessage(
                            message = "Hedef eklenemedi: ${e.localizedMessage}",
                            type = MessageType.ENCOURAGEMENT
                        )
                    }
                }
                
                _dailyGoals.value = updatedGoals
                
                _motivationMessage.value = MotivationMessage(
                    message = "Yeni günlük hedefleriniz oluşturuldu! 🎯",
                    type = MessageType.SUCCESS
                )
            } catch (e: Exception) {
                _motivationMessage.value = MotivationMessage(
                    message = "Hedefler oluşturulurken hata: ${e.localizedMessage}",
                    type = MessageType.ENCOURAGEMENT
                )
            }
        }
    }

    private fun getDefaultGoals(): List<DailyGoal> {
        val userId = auth.currentUser?.uid ?: return emptyList()
        
        // Kullanıcı profilini al
        val userRef = db.collection("users").document(userId)
        val userDoc = runBlocking { userRef.get().await() }
        val userProfile = userDoc.toObject(UserProfile::class.java) ?: return emptyList()

        // UserViewModel'den hedefleri al
        val userViewModel = UserViewModel()
        val goals = userViewModel.getDailyGoals(userProfile)

        return listOf(
            DailyGoal(
                id = "1",
                title = "Egzersiz Süresi",
                target = "${goals["exercise"]} dakika",
                currentProgress = "0/${goals["exercise"]}",
                isCompleted = false,
                isApproved = false
            ),
            DailyGoal(
                id = "2",
                title = "Su Tüketimi",
                target = "${goals["water"]}ml",
                currentProgress = "0/${goals["water"]}",
                isCompleted = false,
                isApproved = false
            ),
            DailyGoal(
                id = "3",
                title = "Adım Sayısı",
                target = "${goals["steps"]} adım",
                currentProgress = "0/${goals["steps"]}",
                isCompleted = false,
                isApproved = false
            ),
            DailyGoal(
                id = "4",
                title = "Kalori Yakımı",
                target = "${goals["calories"]} kcal",
                currentProgress = "0/${goals["calories"]}",
                isCompleted = false,
                isApproved = false
            ),
            DailyGoal(
                id = "5",
                title = "Protein Alımı",
                target = "${goals["protein"]}g",
                currentProgress = "${userProfile.dailyProteinIntake}/${goals["protein"]}",
                isCompleted = userProfile.dailyProteinIntake >= (goals["protein"] ?: 0),
                isApproved = false
            )
        )
    }

    private fun calculateDailyCalories(profile: UserProfile): Int {
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

    fun updateGoalProgress(goalId: String, newProgress: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val userId = auth.currentUser?.uid ?: return@launch
                val goalRef = goalsCollection
                    .whereEqualTo("userId", userId)
                    .whereEqualTo("id", goalId)
                    .get()
                    .await()
                    .documents.firstOrNull()
                    ?.reference

                if (goalRef != null) {
                    val currentGoal = goalRef.get().await().toObject(DailyGoal::class.java)
                    if (currentGoal != null && !currentGoal.isApproved) {
                        // İlerleme formatını kontrol et
                        val formattedProgress = formatProgress(newProgress, currentGoal.target)
                        // İlerlemeyi güncelle
                        goalRef.update(mapOf(
                            "currentProgress" to formattedProgress,
                            "isCompleted" to (calculateProgress(formattedProgress) >= 1.0f)
                        )).await()

                        // Hedef tipiyle eşleşen kullanıcı istatistiğini güncelle
                        val db = FirebaseFirestore.getInstance()
                        val userRef = db.collection("users").document(userId)
                        when (currentGoal.title) {
                            "Adım Sayısı" -> {
                                val steps = formattedProgress.split("/").firstOrNull()?.toIntOrNull() ?: 0
                                userRef.update("dailySteps", steps).await()
                            }
                            "Su Tüketimi" -> {
                                val water = formattedProgress.split("/").firstOrNull()?.toIntOrNull() ?: 0
                                userRef.update("dailyWaterIntake", water).await()
                            }
                            "Kalori Yakımı" -> {
                                val calories = formattedProgress.split("/").firstOrNull()?.toIntOrNull() ?: 0
                                userRef.update("dailyCaloriesBurned", calories).await()
                            }
                            "Protein" -> {
                                val protein = formattedProgress.split("/").firstOrNull()?.toIntOrNull() ?: 0
                                userRef.update("dailyProteinIntake", protein).await()
                            }
                        }

                        // Hedefleri yeniden yükle
                        loadUserDailyGoals()

                        // İlerleme mesajı göster
                        val progress = calculateProgress(formattedProgress)
                        if (progress > 0.5f) {
                            _motivationMessage.value = MotivationMessage(
                                message = "Harika ilerliyorsun! ${currentGoal.title} hedefine yaklaşıyorsun! 💪",
                                type = MessageType.PROGRESS
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                _motivationMessage.value = MotivationMessage(
                    message = "Güncelleme sırasında bir hata oluştu. Lütfen tekrar deneyin.",
                    type = MessageType.ENCOURAGEMENT
                )
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun verifyGoalCompletion(goalId: String, isVerified: Boolean) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val userId = auth.currentUser?.uid ?: return@launch
                val goalRef = goalsCollection
                    .whereEqualTo("userId", userId)
                    .whereEqualTo("id", goalId)
                    .get()
                    .await()
                    .documents.firstOrNull()
                    ?.reference

                if (goalRef != null) {
                    val currentGoal = goalRef.get().await().toObject(DailyGoal::class.java)
                    if (currentGoal != null && currentGoal.isCompleted && !currentGoal.isApproved) {
                        if (isVerified) {
                            // Hedefi onayla ve tamamlanma tarihini kaydet
                            goalRef.update(mapOf(
                                "isApproved" to true,
                                "lastCompletedDate" to Date()
                            )).await()
                            
                            updateUserStats(currentGoal)
                            
                            _motivationMessage.value = MotivationMessage(
                                message = "Hedef onaylandı! 🌟",
                                type = MessageType.SUCCESS
                            )
                        } else {
                            // Hedefi reddet ve sıfırla
                            val targetValue = getTargetValue(goalId)
                            goalRef.update(mapOf(
                                "isApproved" to false,
                                "isCompleted" to false,
                                "currentProgress" to "0/$targetValue",
                                "lastCompletedDate" to null
                            )).await()
                            
                            _motivationMessage.value = MotivationMessage(
                                message = "Hedef reddedildi. Tekrar deneyin! 💪",
                                type = MessageType.ENCOURAGEMENT
                            )
                        }
                        
                        // Hedefleri yeniden yükle
                        loadUserDailyGoals()
                    }
                }
            } catch (e: Exception) {
                _motivationMessage.value = MotivationMessage(
                    message = "Doğrulama sırasında bir hata oluştu. Lütfen tekrar deneyin.",
                    type = MessageType.ENCOURAGEMENT
                )
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun formatProgress(newProgress: String, target: String): String {
        return try {
            val targetValue = target.filter { it.isDigit() }.toIntOrNull() ?: 0
            val progressValue = newProgress.toIntOrNull() ?: 0
            "$progressValue/$targetValue"
        } catch (e: Exception) {
            "0/0"
        }
    }

    fun toggleGoalCompletion(goalId: String, isCompleted: Boolean) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val userId = auth.currentUser?.uid ?: return@launch
                val goalRef = goalsCollection
                    .whereEqualTo("userId", userId)
                    .whereEqualTo("id", goalId)
                    .get()
                    .await()
                    .documents.firstOrNull()
                    ?.reference

                if (goalRef != null) {
                    val currentGoal = goalRef.get().await().toObject(DailyGoal::class.java)
                    if (currentGoal != null && !currentGoal.isApproved) {
                        // Hedef durumunu güncelle
                        goalRef.update("isCompleted", isCompleted).await()
                        
                        if (isCompleted) {
                            _motivationMessage.value = MotivationMessage(
                                message = "Tebrikler! ${currentGoal.title} hedefini tamamladın! 🎉",
                                type = MessageType.SUCCESS
                            )
                            _showCelebration.value = true
                            
                            kotlinx.coroutines.delay(3000)
                            _showCelebration.value = false
                        }
                        
                        checkAllGoalsCompleted()
                    }
                }
            } catch (e: Exception) {
                _motivationMessage.value = MotivationMessage(
                    message = "Güncelleme sırasında bir hata oluştu. Lütfen tekrar deneyin.",
                    type = MessageType.ENCOURAGEMENT
                )
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun checkAllGoalsCompleted() {
        val allCompleted = _dailyGoals.value.all { it.isCompleted }
        if (allCompleted) {
            _motivationMessage.value = MotivationMessage(
                message = "İnanılmaz! Tüm günlük hedeflerini tamamladın! 🌟",
                type = MessageType.SUCCESS
            )
            _showCelebration.value = true
            
            viewModelScope.launch {
                kotlinx.coroutines.delay(3000)
                _showCelebration.value = false
            }
        }
    }

    private fun calculateWeeklyProgress() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val userId = auth.currentUser?.uid ?: return@launch
                val calendar = Calendar.getInstance()
                calendar.add(Calendar.DAY_OF_YEAR, -7)
                val weekAgo = calendar.time

                val snapshot = goalsCollection
                    .whereEqualTo("userId", userId)
                    .whereGreaterThanOrEqualTo("date", weekAgo)
                    .get()
                    .await()

                val completedGoals = snapshot.documents.count { doc ->
                    doc.getBoolean("isCompleted") ?: false
                }
                val totalGoals = snapshot.documents.size

                _weeklyProgress.value = if (totalGoals > 0) {
                    (completedGoals * 100) / totalGoals
                } else {
                    0
                }

                // Haftalık ilerleme mesajı
                when {
                    _weeklyProgress.value >= 80 -> {
                        _motivationMessage.value = MotivationMessage(
                            message = "Bu hafta muhteşem gidiyorsun! 🌟",
                            type = MessageType.ENCOURAGEMENT
                        )
                    }
                    _weeklyProgress.value >= 50 -> {
                        _motivationMessage.value = MotivationMessage(
                            message = "İyi gidiyorsun, devam et! 💪",
                            type = MessageType.ENCOURAGEMENT
                        )
                    }
                    else -> {
                        _motivationMessage.value = MotivationMessage(
                            message = "Her gün yeni bir başlangıç! 🌅",
                            type = MessageType.ENCOURAGEMENT
                        )
                    }
                }
            } catch (e: Exception) {
                _weeklyProgress.value = 0
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun calculateProgress(progress: String): Float {
        return try {
            val parts = progress.split("/")
            if (parts.size == 2) {
                val current = parts[0].toFloat()
                val target = parts[1].toFloat()
                if (target > 0) current / target else 0f
            } else 0f
        } catch (e: Exception) {
            0f
        }
    }
} 