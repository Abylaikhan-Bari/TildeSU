package com.ashim_bari.tildesu.view.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.ashim_bari.tildesu.view.screens.authentication.AuthenticationScreen
import com.ashim_bari.tildesu.view.screens.main.MainScreen

class Navigation {
    companion object {
        const val AUTHENTICATION_ROUTE = "authentication"
        const val MAIN_ROUTE = "main"
    }
}

@Composable
fun NavigationGraph(navController: NavHostController) {
    NavHost(navController, startDestination = Navigation.AUTHENTICATION_ROUTE) {
        composable(Navigation.AUTHENTICATION_ROUTE) {
            AuthenticationScreen(navController)
        }
        composable(Navigation.MAIN_ROUTE) {
            MainScreen(navController)
        }
    }
}
