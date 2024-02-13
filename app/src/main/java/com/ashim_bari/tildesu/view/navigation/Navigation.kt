package com.ashim_bari.tildesu.view.navigation

import MainScreen
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.ashim_bari.tildesu.view.screens.authentication.AuthenticationScreen
import com.ashim_bari.tildesu.view.screens.exercise.ExerciseScreen
import com.ashim_bari.tildesu.viewmodel.AuthenticationViewModel


class Navigation {
    companion object {
        const val AUTHENTICATION_ROUTE = "authentication"
        const val MAIN_ROUTE = "main"
        const val EXERCISE_ROUTE = "exercise"
    }
}

@Composable
fun NavigationGraph(navController: NavHostController) {
    NavHost(navController, startDestination = Navigation.AUTHENTICATION_ROUTE) {
        composable(Navigation.AUTHENTICATION_ROUTE) {
            AuthenticationScreen(navController, viewModel = AuthenticationViewModel())
        }
        composable(Navigation.MAIN_ROUTE) {
            MainScreen(navController)
        }
        composable(Navigation.EXERCISE_ROUTE) {
            ExerciseScreen(navController)
        }
    }
}

