package com.fitnessapp.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Home : BottomNavItem(
        route = "home",
        title = "Ana Sayfa",
        icon = Icons.Default.Home
    )
    object DailyActivity : BottomNavItem(
        route = "daily_activity",
        title = "Günlük Aktivite",
        icon = Icons.Default.FitnessCenter
    )
    object Profile : BottomNavItem(
        route = "profile",
        title = "Profil",
        icon = Icons.Default.Person
    )
} 