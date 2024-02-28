package com.ashim_bari.tildesu.view.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.ashim_bari.tildesu.model.exercise.ExerciseRepository
import com.ashim_bari.tildesu.view.screens.LoadingScreen
import com.ashim_bari.tildesu.view.screens.authentication.AuthenticationScreen
import com.ashim_bari.tildesu.view.screens.exercise.ExerciseTypeSelectionScreen
import com.ashim_bari.tildesu.view.screens.exercise.SpecificExerciseScreen
import com.ashim_bari.tildesu.view.screens.main.MainScreen
import com.ashim_bari.tildesu.viewmodel.authentication.AuthenticationViewModel
import com.ashim_bari.tildesu.viewmodel.exercise.ExerciseViewModelFactory

class Navigation {
    companion object {
        const val AUTHENTICATION_ROUTE = "authentication"
        const val MAIN_ROUTE = "main"
        const val LOADING_ROUTE = "loading"
        const val EXERCISE_TYPE_SELECTION_ROUTE = "exerciseTypeSelection/{level}"
        const val SPECIFIC_EXERCISE_ROUTE = "specificExercise/{level}/{type}"

    }
}

@Composable
fun NavigationGraph(navController: NavHostController, initialScreen: String) {
    val exerciseRepository = ExerciseRepository() // Replace with actual repository initialization if needed
    val exerciseViewModelFactory = ExerciseViewModelFactory(exerciseRepository)

    NavHost(navController = navController, startDestination = Navigation.LOADING_ROUTE) {
        composable(Navigation.AUTHENTICATION_ROUTE) {
            // Obtain ViewModel scoped to the NavHostController
            val authViewModel: AuthenticationViewModel = viewModel()
            AuthenticationScreen(navController = navController, viewModel = authViewModel)
        }
        composable(Navigation.MAIN_ROUTE) {
            MainScreen(navController = navController)
        }
        composable(Navigation.LOADING_ROUTE) {
            LoadingScreen(navController = navController)
        }


        composable(
            route = Navigation.EXERCISE_TYPE_SELECTION_ROUTE,
            arguments = listOf(navArgument("level") { type = NavType.StringType })
        ) { backStackEntry ->
            val level = backStackEntry.arguments?.getString("level") ?: "A1"
            ExerciseTypeSelectionScreen(navController, level)
        }

        composable(
            route = Navigation.SPECIFIC_EXERCISE_ROUTE,
            arguments = listOf(
                navArgument("level") { type = NavType.StringType },
                navArgument("type") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val level = backStackEntry.arguments?.getString("level") ?: "A1"
            val type = backStackEntry.arguments?.getString("type") ?: "quiz"
            SpecificExerciseScreen(navController, exerciseViewModelFactory, level, type)
        }
    }
}
