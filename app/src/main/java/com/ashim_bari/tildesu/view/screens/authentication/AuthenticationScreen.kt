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
import androidx.compose.foundation.Image
import androidx.compose.runtime.saveable.rememberSaveable
import com.ashim_bari.tildesu.view.screens.authentication.pages.LoginPage
import com.ashim_bari.tildesu.view.screens.authentication.pages.RegisterPage
import com.ashim_bari.tildesu.view.screens.authentication.pages.ResetPasswordPage
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import com.ashim_bari.tildesu.viewmodel.authentication.AuthenticationViewModel
import androidx.compose.material3.Scaffold
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.ashim_bari.tildesu.R

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
    var showLanguageDropdown by rememberSaveable { mutableStateOf(false) }

    var expanded by remember { mutableStateOf(false) }
    var currentLanguage by remember { mutableStateOf("English") }
    val languages = listOf("English", "Russian", "Kazakh")
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

                Image(
                    painter = painterResource(R.drawable.satbayev), // Ensure you have a drawable named satbayev.png
                    contentDescription = "Satbayev University Logo",
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = padding)
                        .size(width = 380.dp, height = 100.dp) // Adjust the size as needed
                )
                Image(
                    painter = painterResource(R.drawable.logoauthscreen), // Use the resource ID for your logo
                    contentDescription = "App Logo",
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = padding)
                        .size(width = 300.dp, height = 100.dp) // Adjust the size as needed
                )
                when (currentScreen) {
                    AuthScreens.Login -> LoginPage(navController, { currentScreen = it }, viewModel, snackbarHostState, coroutineScope)
                    AuthScreens.Register -> RegisterPage(navController, { currentScreen = it }, viewModel, snackbarHostState, coroutineScope)
                    AuthScreens.ResetPassword -> ResetPasswordPage(navController, { currentScreen = it }, viewModel, snackbarHostState, coroutineScope)
                }
                Column(modifier = Modifier.fillMaxWidth().padding(top = 20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Button(onClick = { expanded = true }) {
                        Text(text = currentLanguage)
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                    ) {
                        languages.forEach { language ->
                            DropdownMenuItem(
                                text = { Text(language) },
                                onClick = {
                                    currentLanguage = language
                                    expanded = false
                                    // Add your language change handling logic here
                                }
                            )
                        }
                    }
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
