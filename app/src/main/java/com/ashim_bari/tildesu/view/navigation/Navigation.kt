package com.ashim_bari.tildesu.view.navigation

import MainScreen
import com.ashim_bari.tildesu.view.screens.exercise.ExerciseScreen
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.ashim_bari.tildesu.view.screens.authentication.AuthenticationScreen
import com.ashim_bari.tildesu.viewmodel.authentication.AuthenticationViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.ashim_bari.tildesu.model.exercise.ExerciseRepository
import com.ashim_bari.tildesu.viewmodel.exercise.ExerciseViewModelFactory

class Navigation {
    companion object {
        const val AUTHENTICATION_ROUTE = "authentication"
        const val MAIN_ROUTE = "main"
        const val EXERCISE_ROUTE = "exercise/{level}"
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

        composable(
            route = Navigation.EXERCISE_ROUTE,
            arguments = listOf(navArgument("level") { type = NavType.StringType })
        ) { backStackEntry ->
            val level = backStackEntry.arguments?.getString("level") ?: "A1"
            ExerciseScreen(navController, exerciseViewModelFactory, level)
        }

    }
}
