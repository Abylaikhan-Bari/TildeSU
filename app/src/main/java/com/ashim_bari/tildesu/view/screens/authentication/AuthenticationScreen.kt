package com.ashim_bari.tildesu.view.screens.authentication

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.with
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.ashim_bari.tildesu.R
import com.ashim_bari.tildesu.view.screens.authentication.pages.LoginPage
import com.ashim_bari.tildesu.view.screens.authentication.pages.RegisterPage
import com.ashim_bari.tildesu.view.screens.authentication.pages.ResetPasswordPage
import com.ashim_bari.tildesu.viewmodel.authentication.AuthenticationViewModel

@OptIn(ExperimentalAnimationApi::class)
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
            color = MaterialTheme.colorScheme.background,
            modifier = Modifier.padding(padding)
        ) {
            BackHandler(enabled = true) {
                Log.d("AuthenticationScreen", "Back button pressed")
                showExitConfirmation = true
            }
            if (showExitConfirmation) {
                ExitConfirmationDialog(
                    showExitConfirmation = showExitConfirmation,
                    onDismiss = {
                        showExitConfirmation = false
                    }, // Pass a lambda to update the state
                    onConfirm = {
                        Log.d("AuthenticationScreen", "App exited")
                        activity?.finish()
                    }
                )
            }
            Column(
                modifier = Modifier
                    .verticalScroll(scrollState)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(padding))
                Image(
                    painter = painterResource(R.drawable.logoauthscreen),
                    contentDescription = "App Logo",
                    modifier = Modifier
                        .size(200.dp) // Adjust the logo size for a modern look
                        .align(Alignment.CenterHorizontally)
                )
                Spacer(modifier = Modifier.height(padding))
                AnimatedContent(
                    targetState = currentScreen,
                    transitionSpec = {
                        fadeIn(animationSpec = tween(700)) with fadeOut(animationSpec = tween(700))
                    }, label = ""
                ) { targetScreen ->
                    when (targetScreen) {
                        AuthScreens.Login -> LoginPage(
                            navController,
                            { currentScreen = it },
                            snackbarHostState,
                            coroutineScope
                        )

                        AuthScreens.Register -> RegisterPage(
                            navController,
                            { currentScreen = it },
                            snackbarHostState,
                            coroutineScope
                        )

                        AuthScreens.ResetPassword -> ResetPasswordPage(
                            navController,
                            { currentScreen = it },
                            snackbarHostState,
                            coroutineScope
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ExitConfirmationDialog(
    showExitConfirmation: Boolean,
    onDismiss: () -> Unit, // Added an onDismiss lambda parameter
    onConfirm: () -> Unit
) {
    if (showExitConfirmation) {
        AlertDialog(
            onDismissRequest = { /* Do nothing to prevent dismiss on outside click */ },
            title = { Text(stringResource(id = R.string.exit_dialog_title)) },
            text = { Text(stringResource(id = R.string.exit_dialog_content)) },
            confirmButton = {
                Button(onClick = onConfirm) {
                    Text(stringResource(id = R.string.exit_dialog_yes))
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) { // Use the onDismiss lambda here
                    Text(stringResource(id = R.string.exit_dialog_no))
                }
            }
        )
    }
}

enum class AuthScreens {
    Login,
    Register,
    ResetPassword
}