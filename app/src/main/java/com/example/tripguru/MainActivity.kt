package com.example.tripguru

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.tripguru.presentation.theme.TripGuruTheme
import com.example.tripguru.presentation.trip.AddTripScreen
import com.example.tripguru.presentation.trip.TripDetailsScreen
import com.example.tripguru.presentation.trip.TripListScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            TripGuruTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val navController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination = AppRoutes.TRIP_LIST_ROUTE
                    ) {
                        // Lista podróży
                        composable(AppRoutes.TRIP_LIST_ROUTE) {
                            val snackBarMessage = navController.currentBackStackEntry
                                ?.savedStateHandle
                                ?.get<String>("snackbar_message")

                            TripListScreen(
                                onAddTripClick = { navController.navigate(AppRoutes.ADD_TRIP_ROUTE) },
                                snackBarMessage = snackBarMessage,
                                onConsumeSnackBarMessage = {
                                    navController.currentBackStackEntry
                                        ?.savedStateHandle
                                        ?.remove<String>("snackbar_message")
                                },
                                onTripClick = { tripId ->
                                    navController.navigate(
                                        AppRoutes.tripDetailsRoute(
                                            tripId
                                        )
                                    )
                                }
                            )
                        }
                        // Dodaj podróż
                        composable(AppRoutes.ADD_TRIP_ROUTE) {
                            AddTripScreen(
                                onNavigateBackWithResult = { message ->
                                    navController.previousBackStackEntry
                                        ?.savedStateHandle
                                        ?.set("snackbar_message", message)
                                    navController.popBackStack()
                                },
                                onCancel = { navController.popBackStack() }
                            )
                        }
                        // Pokaż podróż
                        composable(
                            route = AppRoutes.SHOW_TRIP_ROUTE,
                            arguments = listOf(navArgument(AppRoutes.SHOW_TRIP_ARG_ID) {
                                type = NavType.LongType
                            })
                        ) { backStackEntry ->
                            val tripId =
                                backStackEntry.arguments?.getLong(AppRoutes.SHOW_TRIP_ARG_ID)
                            if (tripId != null) {
                                TripDetailsScreen(
                                    tripId = tripId,
                                    viewModel = hiltViewModel(),
                                    onNavigateBackWithResult = { message ->
                                        navController.previousBackStackEntry
                                            ?.savedStateHandle
                                            ?.set("snackbar_message", message)
                                        navController.popBackStack()
                                    },
                                    onCancel = { navController.popBackStack() }
                                )
                            } else {
                                navController.popBackStack()
                            }
                        }
                    }
                }
            }
        }
    }
}