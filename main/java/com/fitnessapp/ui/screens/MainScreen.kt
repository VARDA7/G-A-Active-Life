package com.fitnessapp.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.fitnessapp.ui.components.BottomNavBar
import com.fitnessapp.ui.components.BottomNavItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            BottomNavBar(navController = navController)
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.Home.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(BottomNavItem.Home.route) {
                HomeScreen(navController = navController)
            }
            composable(BottomNavItem.DailyActivity.route) {
                DailyActivityScreen(navController= navController)
            }
            composable(BottomNavItem.Profile.route) {
                ProfileScreen(navController = navController)
            }
            composable("login") {
                Login(navController = navController)
            }
            composable("G3") {
               G3(navController = navController)
            }
        }
    }
} 