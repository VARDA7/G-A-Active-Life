package com.fitnessapp.receiver
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import android.media.RingtoneManager
import android.graphics.Color
import android.util.Log
import com.fitnessapp.R
import java.text.SimpleDateFormat
import java.util.*

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra("title") ?: return
        val notificationId = intent.getIntExtra("notificationId", 0)
        val isDailyRoutine = intent.getBooleanExtra("isDailyRoutine", false)
        
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = if (isDailyRoutine) "daily_routine_channel" else "reminder_channel"

        // Bildirim kanalı oluştur
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                if (isDailyRoutine) "Günlük Rutinler" else "Hatırlatıcılar",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = if (isDailyRoutine) "Günlük fitness rutinleri" else "Fitness uygulaması hatırlatıcıları"
                enableLights(true)
                lightColor = Color.RED
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Bildirim içeriğini oluştur
        val contentText = if (isDailyRoutine) {
            "Günaydın! Bugünkü rutinlerinizi kontrol edin."
        } else {
            "Hatırlatıcı zamanı geldi!"
        }

        // Bildirim oluştur
        val notification = NotificationCompat.Builder(context, channelId)
            .setContentTitle(title)
            .setContentText(contentText)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setAutoCancel(true)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        // Bildirimi gönder
        notificationManager.notify(notificationId, notification)
        Log.d("ReminderReceiver", "Bildirim gönderildi: $title")

        // Eğer günlük rutin bildirimi ise, bir sonraki gün için tekrar kur
        if (isDailyRoutine) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as android.app.AlarmManager
            val nextDayIntent = Intent(context, ReminderReceiver::class.java).apply {
                putExtra("title", title)
                putExtra("notificationId", notificationId)
                putExtra("isDailyRoutine", true)
            }

            val pendingIntent = android.app.PendingIntent.getBroadcast(
                context,
                notificationId,
                nextDayIntent,
                android.app.PendingIntent.FLAG_UPDATE_CURRENT or android.app.PendingIntent.FLAG_IMMUTABLE
            )

            val calendar = Calendar.getInstance().apply {
                add(Calendar.DAY_OF_YEAR, 1)
                set(Calendar.HOUR_OF_DAY, 10)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
            }

            try {
                alarmManager.setExactAndAllowWhileIdle(
                    android.app.AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
                Log.d("ReminderReceiver", "Bir sonraki günlük rutin bildirimi kuruldu")
            } catch (e: Exception) {
                Log.e("ReminderReceiver", "Bir sonraki günlük rutin bildirimi kurulurken hata: ${e.message}")
            }
        }
    }
}
