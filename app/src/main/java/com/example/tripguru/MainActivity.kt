package com.example.tripguru

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.tripguru.presentation.theme.TripGuruTheme
import com.example.tripguru.presentation.trip.AddTripScreen
import com.example.tripguru.presentation.trip.TripListScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TripGuruTheme {
                TripGuruTheme {
                    Surface(modifier = Modifier.fillMaxSize()) {
                        val navController = rememberNavController()
                        NavHost(
                            navController = navController,
                            startDestination = "trip_list"
                        ) {
                            composable("trip_list") {
                                TripListScreen(
                                    onAddTripClick = { navController.navigate("add_trip") },
                                    onTripClick = { tripId ->
                                        // Możesz dodać szczegóły podróży w przyszłości
                                        println("Kliknięto podróż $tripId")
                                    }
                                )
                            }
                            composable("add_trip") {
                                AddTripScreen(
                                    onTripSaved = { navController.popBackStack() },
                                    onCancel = { navController.popBackStack() }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}