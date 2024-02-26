package com.ashim_bari.tildesu.view.screens

import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.ashim_bari.tildesu.view.navigation.Navigation
import com.ashim_bari.tildesu.viewmodel.main.MainViewModel

@Composable
fun LoadingScreen(navController: NavHostController) {
    val mainViewModel: MainViewModel = viewModel()
    val isLoggedIn by mainViewModel.isLoggedIn.observeAsState()

    LaunchedEffect(isLoggedIn) {
        Log.d(TAG, "LaunchedEffect: isLoggedIn changed to $isLoggedIn")
        if (isLoggedIn == true) {
            Log.d(TAG, "LaunchedEffect: User logged in, navigating to MainScreen")
            navController.navigate(Navigation.MAIN_ROUTE) {
                popUpTo("main") { inclusive = true }
                launchSingleTop = true
            }
        } else if (isLoggedIn == false) {
            Log.d(TAG, "LaunchedEffect: User not logged in, navigating to AuthenticationScreen")
            navController.navigate(Navigation.AUTHENTICATION_ROUTE) {
                popUpTo("main") { inclusive = true }
                launchSingleTop = true
            }
        }
    }
}
