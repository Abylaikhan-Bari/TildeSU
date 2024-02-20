package com.ashim_bari.tildesu.view.screens.authentication

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import android.content.res.Configuration
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.saveable.rememberSaveable
import com.ashim_bari.tildesu.view.screens.authentication.pages.LoginPage
import com.ashim_bari.tildesu.view.screens.authentication.pages.RegisterPage
import com.ashim_bari.tildesu.view.screens.authentication.pages.ResetPasswordPage
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import com.ashim_bari.tildesu.viewmodel.authentication.AuthenticationViewModel
import androidx.compose.material3.Scaffold
import androidx.compose.ui.platform.LocalContext

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AuthenticationScreen(navController: NavHostController, viewModel: AuthenticationViewModel) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val padding = if (isLandscape) 32.dp else 16.dp
    val scrollState = rememberScrollState()

    var currentScreen by rememberSaveable { mutableStateOf(AuthScreens.Login) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    var showExitConfirmation by rememberSaveable { mutableStateOf(false) }
    val activity = LocalContext.current as? ComponentActivity
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = Modifier.fillMaxSize()
    ) {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {


            BackHandler {
                showExitConfirmation = true
            }

            if (showExitConfirmation) {
                AlertDialog(
                    onDismissRequest = {
                        // If the dialog is dismissed, don't exit the app
                        showExitConfirmation = false
                    },
                    title = { Text("Exit App") },
                    text = { Text("Are you sure you want to exit the app?") },
                    confirmButton = {
                        Button(
                            onClick = {
                                // Handle the logic to exit the app
                                activity?.finish()
                            }
                        ) {
                            Text("Yes")
                        }
                    },
                    dismissButton = {
                        Button(
                            onClick = {
                                // Dismiss the dialog and don't exit the app
                                showExitConfirmation = false
                            }
                        ) {
                            Text("No")
                        }
                    }
                )
            }
            Column(
                modifier = Modifier
                    .verticalScroll(scrollState)
                    .padding(padding)
            ) {
                Text(
                    "TildeSU",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = padding, bottom = 40.dp)
                )

                when (currentScreen) {
                    AuthScreens.Login -> LoginPage(navController, { currentScreen = it }, viewModel, snackbarHostState, coroutineScope)
                    AuthScreens.Register -> RegisterPage(navController, { currentScreen = it }, viewModel, snackbarHostState, coroutineScope)
                    AuthScreens.ResetPassword -> ResetPasswordPage(navController, { currentScreen = it }, viewModel, snackbarHostState, coroutineScope)
                }
            }
        }
    }
}

enum class AuthScreens {
    Login,
    Register,
    ResetPassword
}
