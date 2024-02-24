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
import android.util.Log
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
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.ashim_bari.tildesu.R
import com.ashim_bari.tildesu.viewmodel.LanguageViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AuthenticationScreen(navController: NavHostController, viewModel: AuthenticationViewModel) {
    val context = LocalContext.current
    val languageViewModel: LanguageViewModel = viewModel()

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
    val languages = listOf("English", "Russian", "Kazakh")
    val languageCodes = listOf("en", "ru", "kk")

    val currentLanguageCode by languageViewModel.language.observeAsState(initial = LanguageManager.getLanguagePreference(context))
    var currentLanguage by remember { mutableStateOf(getLanguageName(currentLanguageCode)) }

    LaunchedEffect(currentLanguageCode) {
        currentLanguage = getLanguageName(currentLanguageCode)
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = Modifier.fillMaxSize()
    ) {
        Surface(
            color = MaterialTheme.colorScheme.background
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
                    .padding(padding)
            ) {
                Image(
                    painter = painterResource(R.drawable.satbayev),
                    contentDescription = "Satbayev University Logo",
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = padding)
                        .size(width = 380.dp, height = 100.dp)
                )
                Image(
                    painter = painterResource(R.drawable.logoauthscreen),
                    contentDescription = "App Logo",
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = padding)
                        .size(width = 300.dp, height = 100.dp)
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
                Column(modifier = Modifier.fillMaxWidth().padding(top = 20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Button(onClick = { expanded = true }) {
                        Text(text = currentLanguage)
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                    ) {
                        languages.zip(languageCodes).forEach { (language, code) ->
                            DropdownMenuItem(
                                text = { Text(language) },
                                onClick = {
                                    Log.d("AuthenticationScreen", "Language changed to $language")
                                    currentLanguage = language
                                    expanded = false
                                    LanguageManager.setLocale(context, code)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun getLanguageName(languageCode: String): String {
    return when (languageCode) {
        "en" -> "English"
        "ru" -> "Russian"
        "kk" -> "Kazakh"
        else -> "English"
    }
}
enum class AuthScreens {
    Login,
    Register,
    ResetPassword
}
