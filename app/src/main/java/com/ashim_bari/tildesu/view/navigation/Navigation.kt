package com.ashim_bari.tildesu.view.navigation

import MainScreen
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.ashim_bari.tildesu.view.screens.authentication.AuthenticationScreen


class Navigation {
    companion object {
        const val AUTHENTICATION_ROUTE = "authentication"
        const val MAIN_ROUTE = "main"
    }
}

@Composable
fun NavigationGraph(navController: NavHostController) {
    NavHost(navController, startDestination = Navigation.MAIN_ROUTE) {
        composable(Navigation.AUTHENTICATION_ROUTE) {
            AuthenticationScreen(navController)
        }
        composable(Navigation.MAIN_ROUTE) {
            MainScreen(navController)
        }
    }
}

