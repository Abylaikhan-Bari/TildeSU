package com.ashim_bari.tildesu.view.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.ashim_bari.tildesu.view.screens.FailureScreen
import com.ashim_bari.tildesu.view.screens.LoadingScreen
import com.ashim_bari.tildesu.view.screens.SuccessScreen
import com.ashim_bari.tildesu.view.screens.TrueFalseFailureScreen
import com.ashim_bari.tildesu.view.screens.TrueFalseSuccessScreen
import com.ashim_bari.tildesu.view.screens.authentication.AuthenticationScreen
import com.ashim_bari.tildesu.view.screens.exercise.ExerciseTypeSelectionScreen
import com.ashim_bari.tildesu.view.screens.exercise.SpecificExerciseScreen
import com.ashim_bari.tildesu.view.screens.gemini.GeminiScreen
import com.ashim_bari.tildesu.view.screens.gpt.GptScreen
import com.ashim_bari.tildesu.view.screens.lessons.LessonsScreen
import com.ashim_bari.tildesu.view.screens.lessons.LevelLessons
import com.ashim_bari.tildesu.view.screens.main.MainScreen
import com.ashim_bari.tildesu.viewmodel.authentication.AuthenticationViewModel

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
        const val LESSONS_ROUTE = "lessons/{level}"
        const val LEVEL_LESSONS_ROUTE = "levelLessons/{level}/{lessonId}"
    }
}

@Composable
fun NavigationGraph(navController: NavHostController, initialScreen: String) {
    NavHost(navController = navController, startDestination = Navigation.MAIN_ROUTE) {
        composable(Navigation.AUTHENTICATION_ROUTE) {
            // Obtain ViewModel scoped to the NavHostController using hiltViewModel
            val authViewModel: AuthenticationViewModel = hiltViewModel()
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
                navController.navigate("specificExercise/{level}/{type}") {
                    // Clear back stack to prevent going back to the failure screen
                    popUpTo(Navigation.MAIN_ROUTE) { inclusive = true }
                }
            }
        }
        composable(
            route = Navigation.LESSONS_ROUTE,
            arguments = listOf(navArgument("level") { type = NavType.StringType })
        ) { backStackEntry ->
            val level = backStackEntry.arguments?.getString("level") ?: "A1"
            LessonsScreen(navController = navController, level = level)
        }
        composable(
            route = Navigation.LEVEL_LESSONS_ROUTE,
            arguments = listOf(
                navArgument("level") { type = NavType.StringType },
                navArgument("lessonId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val level = backStackEntry.arguments?.getString("level") ?: "A1"
            val lessonId = backStackEntry.arguments?.getString("lessonId") ?: "Lesson 1"
            LevelLessons(navController, level, lessonId)
        }
        composable("gemini_route") {
            GeminiScreen(navController)
        }
        composable("gpt_route") {
            GptScreen(navController)
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
            // Use hiltViewModel to retrieve ExerciseViewModel and pass it to the screen
            SpecificExerciseScreen(navController, level, type)
        }
    }
}