package com.ashim_bari.tildesu.view.navigation

import MainScreen
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.ashim_bari.tildesu.view.screens.authentication.AuthenticationScreen
import com.ashim_bari.tildesu.view.screens.exercise.ExerciseScreen
import com.ashim_bari.tildesu.view.screens.main.pages.EditProfilePage
import com.ashim_bari.tildesu.viewmodel.AuthenticationViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

class Navigation {
    companion object {
        const val AUTHENTICATION_ROUTE = "authentication"
        const val MAIN_ROUTE = "main"
        const val EXERCISE_ROUTE = "exercise"
        const val EDITPROFILE_ROUTE = "editProfile"
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
        composable(Navigation.EDITPROFILE_ROUTE) {
            // If EditProfilePage requires AuthenticationViewModel, obtain it here
            val authViewModel: AuthenticationViewModel = viewModel()
            EditProfilePage(navController = navController, viewModel = authViewModel)
        }
    }
}
