package com.fitnessapp.ui.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fitnessapp.data.models.Reminder
import com.fitnessapp.ui.viewmodels.ReminderViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RemindersScreen() {
    val context = LocalContext.current
    val viewModel: ReminderViewModel = viewModel()
    val reminders by viewModel.reminders.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var reminderTitle by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf<Calendar?>(null) }
    var selectedTime by remember { mutableStateOf<Calendar?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }


    if (showDatePicker) {
        val datePickerDialog = remember {
            DatePickerDialog(
                context,
                { _, year, month, day ->
                    selectedDate = Calendar.getInstance().apply {
                        set(Calendar.YEAR, year)
                        set(Calendar.MONTH, month)
                        set(Calendar.DAY_OF_MONTH, day)
                    }
                    showDatePicker = false
                },
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
            )
        }
        LaunchedEffect(Unit) {
            datePickerDialog.show()
        }
    }


    if (showTimePicker) {
        val timePickerDialog = remember {
            TimePickerDialog(
                context,
                { _, hour, minute ->
                    selectedTime = Calendar.getInstance().apply {
                        set(Calendar.HOUR_OF_DAY, hour)
                        set(Calendar.MINUTE, minute)
                    }
                    showTimePicker = false
                },
                Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
                Calendar.getInstance().get(Calendar.MINUTE),
                true
            )
        }
        LaunchedEffect(Unit) {
            timePickerDialog.show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {
            items(reminders) { reminder ->
                ReminderItem(
                    reminder = reminder,
                    onDelete = { viewModel.deleteReminder(context, reminder) }
                )
            }
        }


        Button(
            onClick = { showAddDialog = true },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text("Yeni Hatırlatıcı Ekle",color = Color.White)
        }
    }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Yeni Hatırlatıcı") },
            text = {
                Column {
                    OutlinedTextField(
                        value = reminderTitle,
                        onValueChange = { reminderTitle = it },
                        label = { Text("Hatırlatıcı Başlığı") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    

                    Button(
                        onClick = { showDatePicker = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(selectedDate?.let { 
                            "${it.get(Calendar.DAY_OF_MONTH)}/${it.get(Calendar.MONTH) + 1}/${it.get(Calendar.YEAR)}"
                        } ?: "Tarih Seç", color = Color.White)
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    

                    Button(
                        onClick = { showTimePicker = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(selectedTime?.let { 
                            String.format("%02d:%02d", it.get(Calendar.HOUR_OF_DAY), it.get(Calendar.MINUTE))
                        } ?: "Saat Seç", color = Color.White)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (reminderTitle.isNotBlank() && selectedDate != null && selectedTime != null) {
                            val calendar = Calendar.getInstance().apply {
                                set(Calendar.YEAR, selectedDate!!.get(Calendar.YEAR))
                                set(Calendar.MONTH, selectedDate!!.get(Calendar.MONTH))
                                set(Calendar.DAY_OF_MONTH, selectedDate!!.get(Calendar.DAY_OF_MONTH))
                                set(Calendar.HOUR_OF_DAY, selectedTime!!.get(Calendar.HOUR_OF_DAY))
                                set(Calendar.MINUTE, selectedTime!!.get(Calendar.MINUTE))
                                set(Calendar.SECOND, 0)
                            }
                            
                            viewModel.addReminder(
                                context,
                                reminderTitle,
                                calendar.get(Calendar.YEAR),
                                calendar.get(Calendar.MONTH),
                                calendar.get(Calendar.DAY_OF_MONTH),
                                calendar.get(Calendar.HOUR_OF_DAY),
                                calendar.get(Calendar.MINUTE)
                            )
                            showAddDialog = false
                            reminderTitle = ""
                            selectedDate = null
                            selectedTime = null
                        } else {
                            Toast.makeText(
                                context,
                                "Lütfen tüm alanları doldurun",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                ) {
                    Text("Ekle", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("İptal")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReminderItem(
    reminder: Reminder,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = { }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = reminder.title,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (reminder.isDailyRoutine) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = "Günlük Rutin",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Text(
                    text = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(reminder.time)),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            if (!reminder.isDailyRoutine) {
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Sil")
                }
            }
        }
    }
}

private fun showTimePicker(context: Context, onTimeSelected: (Long) -> Unit) {
    val calendar = Calendar.getInstance()
    TimePickerDialog(
        context,
        { _, hourOfDay, minute ->
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
            calendar.set(Calendar.MINUTE, minute)
            calendar.set(Calendar.SECOND, 0)
            onTimeSelected(calendar.timeInMillis)
        },
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE),
        true
    ).show()
} 