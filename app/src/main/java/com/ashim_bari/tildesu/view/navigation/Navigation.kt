package com.ashim_bari.tildesu.view.navigation

import com.ashim_bari.tildesu.view.screens.exercise.A1_Level
import com.ashim_bari.tildesu.view.screens.exercise.A2_Level
import com.ashim_bari.tildesu.view.screens.exercise.B1_Level
import com.ashim_bari.tildesu.view.screens.exercise.B2_Level
import com.ashim_bari.tildesu.view.screens.exercise.C1_Level
import com.ashim_bari.tildesu.view.screens.exercise.C2_Level
import MainScreen
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.ashim_bari.tildesu.view.screens.authentication.AuthenticationScreen
import com.ashim_bari.tildesu.viewmodel.authentication.AuthenticationViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ashim_bari.tildesu.model.exercise.ExerciseRepository
import com.ashim_bari.tildesu.viewmodel.exercise.ExerciseViewModelFactory

class Navigation {
    companion object {
        const val AUTHENTICATION_ROUTE = "authentication"
        const val MAIN_ROUTE = "main"
        const val EXERCISE_ROUTE = "exercise"
        const val A1_LEVEL = "a1_level"
        const val A2_LEVEL = "a2_level"
        const val B1_LEVEL = "b1_level"
        const val B2_LEVEL = "b2_level"
        const val C1_LEVEL = "c1_level"
        const val C2_LEVEL = "c2_level"

    }
}

@Composable
fun NavigationGraph(navController: NavHostController) {
    val exerciseRepository = ExerciseRepository() // Replace with actual repository initialization if needed
    val exerciseViewModelFactory = ExerciseViewModelFactory(exerciseRepository)

    NavHost(navController = navController, startDestination = Navigation.MAIN_ROUTE) {
        composable(Navigation.AUTHENTICATION_ROUTE) {
            // Obtain ViewModel scoped to the NavHostController
            val authViewModel: AuthenticationViewModel = viewModel()
            AuthenticationScreen(navController = navController, viewModel = authViewModel)
        }
        composable(Navigation.MAIN_ROUTE) {
            MainScreen(navController = navController)
        }

        composable(Navigation.A1_LEVEL) {
            A1_Level(navController = navController, exerciseViewModelFactory = exerciseViewModelFactory)
        }
        composable(Navigation.A2_LEVEL) {
            A2_Level(navController)
        }
        composable(Navigation.B1_LEVEL) {
            B1_Level(navController)
        }
        composable(Navigation.B2_LEVEL) {
            B2_Level(navController)
        }
        composable(Navigation.C1_LEVEL) {
            C1_Level(navController)
        }
        composable(Navigation.C2_LEVEL) {
            C2_Level(navController)
        }
    }
}
