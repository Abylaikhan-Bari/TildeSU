package com.ashim_bari.tildesu.view.screens.authentication

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
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
            BackHandler {
                Log.d("AuthenticationScreen", "Back button pressed")
                showExitConfirmation = true
            }

            if (showExitConfirmation) {
                AlertDialog(
                    onDismissRequest = {
                        Log.d("AuthenticationScreen", "Exit dialog dismissed")
                        showExitConfirmation = false
                    },
                    title = { Text(stringResource(id = R.string.exit_dialog_title)) },
                    text = { Text(stringResource(id = R.string.exit_dialog_content)) },
                    confirmButton = {
                        Button(
                            onClick = {
                                Log.d("AuthenticationScreen", "App exited")
                                activity?.finish()
                            }
                        ) {
                            Text(stringResource(id = R.string.exit_dialog_yes))
                        }
                    },
                    dismissButton = {
                        Button(
                            onClick = {
                                Log.d("AuthenticationScreen", "Exit dialog dismissed")
                                showExitConfirmation = false
                            }
                        ) {
                            Text(stringResource(id = R.string.exit_dialog_no))
                        }
                    }
                )
            }

            Column(
                modifier = Modifier
                    .verticalScroll(scrollState)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(R.drawable.satbayev),
                    contentDescription = "Satbayev University Logo",
                    modifier = Modifier
                        .sizeIn(maxWidth = 400.dp, maxHeight = 70.dp)
                        .padding(bottom = padding)
                )
                Image(
                    painter = painterResource(R.drawable.logoauthscreen),
                    contentDescription = "App Logo",
                    modifier = Modifier
                        .sizeIn(maxWidth = 300.dp, maxHeight = 100.dp)
                        .padding(top = 50.dp, bottom = 20.dp)
                )

                when (currentScreen) {
                    AuthScreens.Login -> {
                        Log.d("AuthenticationScreen", "Showing Login page")
                        LoginPage(navController, { currentScreen = it }, viewModel, snackbarHostState, coroutineScope)
                    }
                    AuthScreens.Register -> {
                        Log.d("AuthenticationScreen", "Showing Register page")
                        RegisterPage(navController, { currentScreen = it }, viewModel, snackbarHostState, coroutineScope)
                    }
                    AuthScreens.ResetPassword -> {
                        Log.d("AuthenticationScreen", "Showing ResetPassword page")
                        ResetPasswordPage(navController, { currentScreen = it }, viewModel, snackbarHostState, coroutineScope)
                    }
                }

                Image(
                    painter = painterResource(R.drawable.logoauthscreenbottom),
                    contentDescription = "App Bottom Logo",
                    modifier = Modifier
                        .size(190.dp)
                        .padding(bottom = padding)
                )
            }
        }
    }
}




enum class AuthScreens {
    Login,
    Register,
    ResetPassword
}
