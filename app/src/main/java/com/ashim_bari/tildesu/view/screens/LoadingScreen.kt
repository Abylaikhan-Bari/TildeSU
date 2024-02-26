package com.ashim_bari.tildesu.view.screens

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.ashim_bari.tildesu.view.navigation.Navigation
import com.ashim_bari.tildesu.viewmodel.main.MainViewModel

@Composable
fun LoadingScreen(navController: NavHostController) {
    val mainViewModel: MainViewModel = viewModel()
    val isLoggedIn by mainViewModel.isLoggedIn.observeAsState()

    // Define a TAG for logging
    val TAG = "LoadingScreen"

    LaunchedEffect(isLoggedIn) {
        Log.d(TAG, "LaunchedEffect: isLoggedIn changed to $isLoggedIn")
        when (isLoggedIn) {
            true -> {
                Log.d(TAG, "User logged in, navigating to MainScreen")
                navController.navigate(Navigation.MAIN_ROUTE) {
                    // Assuming Navigation.MAIN_ROUTE is the correct destination to pop up to
                    popUpTo(Navigation.MAIN_ROUTE) { inclusive = true }
                    launchSingleTop = true
                }
            }
            false -> {
                Log.d(TAG, "User not logged in, navigating to AuthenticationScreen")
                navController.navigate(Navigation.AUTHENTICATION_ROUTE) {
                    popUpTo(Navigation.MAIN_ROUTE) { inclusive = true }
                    launchSingleTop = true
                }
            }
            null -> {
                // Handle null case here
                Log.d(TAG, "Determining login status...")
                // Optionally show a CircularProgressIndicator or similar indicator
            }
        }
    }

    if (isLoggedIn == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(modifier = Modifier.size(50.dp))
        }
    }
}
