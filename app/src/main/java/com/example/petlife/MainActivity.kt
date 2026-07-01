package com.example.petlife

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.petlife.presentation.home.HomeScreen
import com.example.petlife.presentation.scheduling.SchedulingScreen
import com.example.petlife.presentation.splash.SplashScreen
import com.example.petlife.presentation.theme.PetLIfeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PetLIfeTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = "Splash"
                ) {
                    composable("Splash") { SplashScreen(navController) }
                    composable("Home") { HomeScreen(navController) }
                    composable("Scheduling") { SchedulingScreen(navController) }
                }
            }
        }
    }
}
