package com.ashim_bari.tildesu.view.navigation

import MainScreen
import ProfilePage
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.ashim_bari.tildesu.view.screens.authentication.AuthenticationScreen
import com.ashim_bari.tildesu.view.screens.exercise.ExerciseScreen
import com.ashim_bari.tildesu.viewmodel.AuthenticationViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ashim_bari.tildesu.viewmodel.MainViewModel

class Navigation {
    companion object {
        const val AUTHENTICATION_ROUTE = "authentication"
        const val MAIN_ROUTE = "main"
        const val EXERCISE_ROUTE = "exercise"

    }
}

@Composable
fun NavigationGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Navigation.AUTHENTICATION_ROUTE) {
        composable(Navigation.AUTHENTICATION_ROUTE) {
            // Obtain ViewModel scoped to the NavHostController
            val authViewModel: AuthenticationViewModel = viewModel()
            AuthenticationScreen(navController = navController, viewModel = authViewModel)
        }
        composable(Navigation.MAIN_ROUTE) {
            MainScreen(navController = navController)
        }
        composable(Navigation.EXERCISE_ROUTE) {
            ExerciseScreen(navController = navController)
        }
    }
}
