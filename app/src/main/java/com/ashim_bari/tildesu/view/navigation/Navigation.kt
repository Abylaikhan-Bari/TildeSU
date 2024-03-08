package com.ashim_bari.tildesu.view.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.ashim_bari.tildesu.model.exercise.ExerciseRepository
import com.ashim_bari.tildesu.view.screens.FailureScreen
import com.ashim_bari.tildesu.view.screens.LoadingScreen
import com.ashim_bari.tildesu.view.screens.SuccessScreen
import com.ashim_bari.tildesu.view.screens.TrueFalseFailureScreen
import com.ashim_bari.tildesu.view.screens.TrueFalseSuccessScreen
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
        const val SUCCESS = "success"
        const val FAILURE = "failure"
        const val TRUE_FALSE_SUCCESS = "trueFalseSuccess"
        const val TRUE_FALSE_FAILURE = "trueFalseFailure"

    }
}

@Composable
fun NavigationGraph(navController: NavHostController, initialScreen: String) {
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
        composable(Navigation.LOADING_ROUTE) {
            LoadingScreen(navController = navController)
        }
        composable(
            route = "${Navigation.SUCCESS}/{score}",
            arguments = listOf(navArgument("score") { type = NavType.IntType })
        ) { backStackEntry ->
            val score = backStackEntry.arguments?.getInt("score") ?: 0
            SuccessScreen(navController = navController, score = score)
        }


        composable(Navigation.FAILURE) {
            FailureScreen(navController = navController) {
                // Define what happens when the restart button is clicked. For example:
                navController.navigate("specificExercise/{level}/{type}") {
                    // Clear back stack to prevent going back to the failure screen
                    popUpTo(Navigation.MAIN_ROUTE) { inclusive = true }
                }
            }
        }
        composable(
            route = "${Navigation.TRUE_FALSE_SUCCESS}/{score}",
            arguments = listOf(navArgument("score") { type = NavType.IntType })
        ) { backStackEntry ->
            val score = backStackEntry.arguments?.getInt("score") ?: 0
            TrueFalseSuccessScreen(navController = navController, score = score)
        }


        composable(Navigation.TRUE_FALSE_FAILURE) {
            TrueFalseFailureScreen(navController = navController) {
                // Define what happens when the restart button is clicked. For example:
                navController.navigate("specificExercise/{level}/{type}") {
                    // Clear back stack to prevent going back to the failure screen
                    popUpTo(Navigation.MAIN_ROUTE) { inclusive = true }
                }
            }
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
