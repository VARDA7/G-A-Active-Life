package com.fitnessapp.data.models

data class Reminder(
    val id: String = "",
    val title: String = "",
    val time: Long = 0L,
    val isDailyRoutine: Boolean = false
) 