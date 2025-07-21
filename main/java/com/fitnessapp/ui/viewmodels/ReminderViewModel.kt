package com.fitnessapp.ui.viewmodels

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import com.fitnessapp.receiver.ReminderReceiver
import com.fitnessapp.data.models.Reminder
import java.util.*

class ReminderViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    
    // Kullanıcının kendi bildirimleri için koleksiyon
    private val userRemindersCollection
        get() = db.collection("users").document(auth.currentUser?.uid ?: "").collection("reminders")
    
    // Günlük rutin bildirimi için ayrı koleksiyon
    private val dailyRoutineCollection
        get() = db.collection("daily_routines")

    private val _reminders = MutableStateFlow<List<Reminder>>(emptyList())
    val reminders: StateFlow<List<Reminder>> = _reminders

    init {
        loadReminders()
        setupDailyRoutine()
    }

    private fun loadReminders() {
        val userId = auth.currentUser?.uid ?: return
        userRemindersCollection.addSnapshotListener { snapshot, _ ->
            if (snapshot != null) {
                val list = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(Reminder::class.java)?.copy(id = doc.id)
                }
                _reminders.value = list
            }
        }
    }

    private fun setupDailyRoutine() {
        viewModelScope.launch {
            try {
                // Günlük rutin bildirimi zaten var mı kontrol et
                val existingRoutine = dailyRoutineCollection
                    .whereEqualTo("userId", auth.currentUser?.uid)
                    .get()
                    .await()
                    .documents
                    .firstOrNull()

                if (existingRoutine == null) {
                    // Günlük rutin bildirimi oluştur
                    val calendar = Calendar.getInstance().apply {
                        set(Calendar.HOUR_OF_DAY, 10)
                        set(Calendar.MINUTE, 0)
                        set(Calendar.SECOND, 0)
                        
                        if (timeInMillis <= System.currentTimeMillis()) {
                            add(Calendar.DAY_OF_YEAR, 1)
                        }
                    }

                    val routine = hashMapOf(
                        "userId" to auth.currentUser?.uid,
                        "time" to calendar.timeInMillis,
                        "title" to "Günlük Fitness Rutini"
                    )

                    dailyRoutineCollection.add(routine).await()
                }
            } catch (e: Exception) {
                Log.e("ReminderViewModel", "Günlük rutin kurulurken hata: ${e.message}")
            }
        }
    }

    // Kullanıcının kendi bildirimlerini eklemesi için
    fun addReminder(context: Context, title: String, year: Int, month: Int, day: Int, hour: Int, minute: Int) {
        viewModelScope.launch {
            try {
                val calendar = Calendar.getInstance().apply {
                    set(Calendar.YEAR, year)
                    set(Calendar.MONTH, month)
                    set(Calendar.DAY_OF_MONTH, day)
                    set(Calendar.HOUR_OF_DAY, hour)
                    set(Calendar.MINUTE, minute)
                    set(Calendar.SECOND, 0)
                }

                val reminder = Reminder(
                    title = title,
                    time = calendar.timeInMillis
                )

                val docRef = userRemindersCollection.add(reminder).await()
                val newReminder = reminder.copy(id = docRef.id)
                setAlarm(context, newReminder)
            } catch (e: Exception) {
                Log.e("ReminderViewModel", "Hatırlatıcı eklenirken hata: ${e.message}")
            }
        }
    }

    // Kullanıcının kendi bildirimlerini silmesi için
    fun deleteReminder(context: Context, reminder: Reminder) {
        viewModelScope.launch {
            try {
                userRemindersCollection.document(reminder.id).delete().await()
                cancelAlarm(context, reminder)
            } catch (e: Exception) {
                Log.e("ReminderViewModel", "Hatırlatıcı silinirken hata: ${e.message}")
            }
        }
    }

    private fun setAlarm(context: Context, reminder: Reminder) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra("title", reminder.title)
            putExtra("notificationId", reminder.id.hashCode())
            putExtra("isDailyRoutine", false)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            reminder.id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            val alarmClockInfo = AlarmManager.AlarmClockInfo(reminder.time, pendingIntent)
            alarmManager.setAlarmClock(alarmClockInfo, pendingIntent)
            Log.d("ReminderViewModel", "Alarm kuruldu: ${reminder.title}, Zaman: ${reminder.time}")
        } catch (e: Exception) {
            Log.e("ReminderViewModel", "Alarm kurulurken hata: ${e.message}")
        }
    }

    private fun cancelAlarm(context: Context, reminder: Reminder) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            reminder.id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            alarmManager.cancel(pendingIntent)
            Log.d("ReminderViewModel", "Alarm iptal edildi: ${reminder.title}")
        } catch (e: Exception) {
            Log.e("ReminderViewModel", "Alarm iptal edilirken hata: ${e.message}")
        }
    }
}