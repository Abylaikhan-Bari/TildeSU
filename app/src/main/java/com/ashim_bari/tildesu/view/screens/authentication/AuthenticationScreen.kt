package com.ashim_bari.tildesu.view.screens.authentication

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import android.content.res.Configuration
import androidx.compose.runtime.saveable.rememberSaveable
import com.ashim_bari.tildesu.view.screens.authentication.pages.LoginPage
import com.ashim_bari.tildesu.view.screens.authentication.pages.RegisterPage
import com.ashim_bari.tildesu.view.screens.authentication.pages.ResetPasswordPage
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

@Composable
fun AuthenticationScreen(navController: NavHostController) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val padding = if (isLandscape) 32.dp else 16.dp
    val scrollState = rememberScrollState()

    var currentScreen by rememberSaveable { mutableStateOf(AuthScreens.Login) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(scrollState)
                .padding(padding)
        ) {
            Text(
                text = "TildeSU",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = padding, bottom = 40.dp)
            )

            when (currentScreen) {
                AuthScreens.Login -> LoginPage(navController) { currentScreen = it }
                AuthScreens.Register -> RegisterPage(navController) { currentScreen = it }
                AuthScreens.ResetPassword -> ResetPasswordPage(navController) { currentScreen = it }
            }
        }
    }
}



enum class AuthScreens {
    Login,
    Register,
    ResetPassword
}
