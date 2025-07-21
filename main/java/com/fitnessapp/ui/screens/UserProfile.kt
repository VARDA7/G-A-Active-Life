package com.fitnessapp.ui.screens

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class UserProfile(
    @DocumentId
    val id: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val age: Int = 0,
    val height: Int = 0,
    val weight: Int = 0,
    val goal: String = "",
    val locationPreferences: List<String> = emptyList(),
    val activities: List<String> = emptyList(),
    val weeklyDays: Int = 0,
    val dailyMinutes: Int = 0,
    val bmi: Double = 0.0,
    val gender: String = "",
    val activityLevel: String = "",
    val dailySteps: Int = 0,
    val dailyCaloriesBurned: Int = 0,
    val dailyProteinIntake: Int = 0,
    val dailyWaterIntake: Int = 0,
    val goalWeight: Int = 0,
    val goalDate: String = ""
) {
    // Boş constructor Firebase için gerekli
    constructor() : this(
        id = "",
        firstName = "",
        lastName = "",
        email = "",
        age = 0,
        height = 0,
        weight = 0,
        goal = "",
        locationPreferences = emptyList(),
        activities = emptyList(),
        weeklyDays = 0,
        dailyMinutes = 0,
        bmi = 0.0,
        gender = "",
        activityLevel = "",
        dailySteps = 0,
        dailyCaloriesBurned = 0,
        dailyProteinIntake = 0,
        dailyWaterIntake = 0,
        goalWeight = 0,
        goalDate = ""
    )
}
