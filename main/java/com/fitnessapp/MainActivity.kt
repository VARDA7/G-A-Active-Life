package com.fitnessapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import com.fitnessapp.ui.screens.*
import com.fitnessapp.ui.theme.AppTheme
import com.fitnessapp.ui.theme.AccentColor
import com.google.firebase.auth.FirebaseAuth
import android.os.Build
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import android.util.Log
import android.widget.Toast
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.lifecycleScope
import com.fitnessapp.ui.viewmodels.ReminderViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch(Dispatchers.IO) {
            checkAndRequestNotificationPermissions()
        }

        setContent {
            AppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = "g1") {
                        composable("g1") {
                            G1(navController = navController)
                        }
                        composable("g2") {
                            G2(navController = navController)
                        }
                        composable("Login") {
                            Login(navController = navController)
                        }
                        composable("g3") {
                            G3(navController = navController)
                        }
                        composable("DailyActivityScreen") {
                            DailyActivityScreen(navController = navController)
                        }
                        composable("main_screen") {
                            MainScreen()
                        }
                    }
                }
            }
        }
    }

    private fun checkAndRequestNotificationPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                runOnUiThread {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                        1001
                    )
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1001) {
            lifecycleScope.launch(Dispatchers.IO) {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Log.d("MainActivity", "Bildirim izni verildi")
                } else {

                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@MainActivity,
                            "Bildirimler için izin gerekli",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val currentBackStack by navController.currentBackStackEntryAsState()
    val currentDestination = currentBackStack?.destination
    val currentScreen = remember(currentDestination) {
        bottomNavItems.find { it.route == currentDestination?.route } ?: bottomNavItems[0]
    }

    Scaffold(
        topBar = {
            if (currentScreen.route != Screen.Home.route) {
                TopAppBar(
                    title = { Text(currentScreen.title) },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    navigationIcon = {
                        if (currentDestination?.route !in bottomNavItems.map { it.route }) {
                            IconButton(onClick = { navController.navigateUp() }) {
                                Icon(
                                    imageVector = Icons.Default.ArrowBack,
                                    contentDescription = "Geri",
                                    tint = AccentColor
                                )
                            }
                        }
                    }
                )
            }
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ) {
                bottomNavItems.forEach { screen ->
                    NavigationBarItem(
                        selected = currentScreen.route == screen.route,
                        onClick = {
                            if (currentScreen.route != screen.route) {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = screen.icon,
                                contentDescription = screen.title,
                                tint = AccentColor
                            )
                        },
                        label = { Text(screen.title, color = AccentColor) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = AccentColor,
                            selectedTextColor = AccentColor,
                            unselectedIconColor = AccentColor.copy(alpha = 0.6f),
                            unselectedTextColor = AccentColor.copy(alpha = 0.6f),
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(
                route = Screen.Home.route,
                deepLinks = listOf(
                    navDeepLink { uriPattern = "fitnessapp://home" }
                )
            ) {
                HomeScreen(navController = navController)
            }
            composable(
                route = Screen.Profile.route,
                deepLinks = listOf(
                    navDeepLink { uriPattern = "fitnessapp://profile" }
                )
            ) {
                ProfileScreen(navController)
            }
            composable(
                route = Screen.Group.route,
                deepLinks = listOf(
                    navDeepLink { uriPattern = "fitnessapp://dailyActivityScreen" }
                )
            ) {
                DailyActivityScreen(navController = navController)
            }
            composable(
                route = Screen.Reminders.route,
                deepLinks = listOf(
                    navDeepLink { uriPattern = "fitnessapp://reminders" }
                )
            ) {
                RemindersScreen()
            }
            // Alt sayfalar
            composable("calori") {
                calori(navController = navController)
            }
            composable("exercise_plan") {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Egzersiz Planı")
                }
            }
            composable("nutrition_tips") {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Beslenme Önerileri")
                }
            }
        }
    }
}

sealed class Screen(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Home : Screen("home", "Ana Sayfa", Icons.Default.Home)
    object Profile : Screen("profile", "Profil", Icons.Default.Person)
    object Group : Screen("dailyActivityScreen", "Aktivite", Icons.Default.CheckCircle)
    object Reminders : Screen("reminders", "Hatırlatıcılar", Icons.Default.Notifications)
}

private val bottomNavItems = listOf(
    Screen.Home,
    Screen.Profile,
    Screen.Group,
    Screen.Reminders
)