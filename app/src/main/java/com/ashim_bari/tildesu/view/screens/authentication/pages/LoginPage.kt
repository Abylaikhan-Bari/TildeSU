package com.ashim_bari.tildesu.view.screens.authentication.pages

import LanguageManager
import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalAutofill
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.ashim_bari.tildesu.R
import com.ashim_bari.tildesu.view.MainActivity
import com.ashim_bari.tildesu.view.navigation.Navigation
import com.ashim_bari.tildesu.view.screens.authentication.AuthScreens
import com.ashim_bari.tildesu.view.ui.theme.BluePrimary
import com.ashim_bari.tildesu.viewmodel.LanguageViewModel
import com.ashim_bari.tildesu.viewmodel.authentication.AuthenticationViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@OptIn(ExperimentalComposeUiApi::class)
@SuppressLint("UnusedCrossfadeTargetStateParameter")
@Composable
fun LoginPage(
    navController: NavHostController,
    onNavigate: (AuthScreens) -> Unit,
    viewModel: AuthenticationViewModel,
    snackbarHostState: SnackbarHostState,
    coroutineScope: CoroutineScope
) {
    val context = LocalContext.current
    val languageViewModel: LanguageViewModel = viewModel()
    var username by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var authMessage by rememberSaveable { mutableStateOf<String?>(null) } // Holds the authentication message
    var passwordVisibility by rememberSaveable { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val passwordFocusRequester = remember { FocusRequester() }
    val autofill = LocalAutofill.current
    val coroutineScope = rememberCoroutineScope()
    var isLoading by rememberSaveable { mutableStateOf(false) }
    var isSuccess by rememberSaveable { mutableStateOf(false) }
    var showLanguageDropdown by rememberSaveable { mutableStateOf(false) }

    var expanded by remember { mutableStateOf(false) }
    val languages = listOf("English", "Russian", "Kazakh")
    val languageCodes = listOf("en", "ru", "kk")
    val currentLanguageCode = languageViewModel.language.collectAsState().value
    var currentLanguage by remember { mutableStateOf(getLanguageName(currentLanguageCode)) }
    var showLanguageDialog by rememberSaveable { mutableStateOf(false) }
    var tempSelectedLanguageCode by rememberSaveable { mutableStateOf<String?>(null) }
    val isUsernameValid by remember(username) {
        mutableStateOf(username.isNotBlank())
    }

    val isPasswordValid by remember(password) {
        mutableStateOf(password.isNotBlank())
    }

    LaunchedEffect(currentLanguageCode) {
        currentLanguage = getLanguageName(currentLanguageCode)
    }

    LaunchedEffect(key1 = currentLanguageCode) {
        Log.d("LanguageChange", "Recomposing due to language change: $currentLanguageCode")
        // Additional actions if needed
    }

    val invalidCredentialsMessage = stringResource(R.string.invalid_credentials)
    val  usernameRequiredMessage= stringResource(R.string.username_required)
    val passwordRequiredMessage = stringResource(R.string.password_required)

    LaunchedEffect(username, password) {
        if (username.isNotBlank() && password.isNotBlank()) {
            authMessage = null
        } else {
            authMessage = if (username.isNotBlank() && password.isBlank()) {
                passwordRequiredMessage
            } else if (username.isBlank() && password.isNotBlank()) {
                usernameRequiredMessage
            } else {
                null
            }
        }
    }

// Update authMessage when the user enters valid credentials
    LaunchedEffect(isSuccess) {
        if (isSuccess) {
            authMessage = null
        }
    }



    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = stringResource(id = R.string.login),
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text(stringResource(id = R.string.email)) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { passwordFocusRequester.requestFocus() }),
            modifier = Modifier
                .fillMaxWidth()

        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(stringResource(id = R.string.password)) },
            singleLine = true,
            visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = {
                    keyboardController?.hide()
                }
            ),
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(passwordFocusRequester), // Apply focusRequester modifier
            trailingIcon = {
                IconButton(onClick = { passwordVisibility = !passwordVisibility }) {
                    Icon(
                        imageVector = if (passwordVisibility) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                        contentDescription = "Toggle Password Visibility"
                    )
                }
            }
        )
        authMessage?.let {
            Text(text = it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(bottom = 8.dp))
        }
        Row(
            horizontalArrangement = Arrangement.End,
            modifier = Modifier.fillMaxWidth()
        ) {
            TextButton(onClick = { onNavigate(AuthScreens.ResetPassword) }) {
                Text(stringResource(id = R.string.forgot_password))
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .animateContentSize(),
            contentAlignment = Alignment.Center
        ) {
            Crossfade(targetState = isLoading || isSuccess, label = "Login") {
                val loginSuccessfulMessage = stringResource(id = R.string.login_successful)
                val loginFailedMessage = stringResource(id = R.string.login_failed)

                when {
                    isLoading -> CircularProgressIndicator(color = BluePrimary)
                    isSuccess -> Icon(Icons.Filled.Check, contentDescription = "Success", tint = BluePrimary)
                    else -> {
                        Button(
                            onClick = {
                                if (isUsernameValid && isPasswordValid) {
                                    authMessage = null
                                    coroutineScope.launch {
                                        isLoading = true
                                        isSuccess = false
                                        val success = viewModel.login(username, password)
                                        isLoading = false
                                        isSuccess = success
                                        if (success) {
                                            snackbarHostState.showSnackbar(loginSuccessfulMessage)
                                            navController.navigate(Navigation.MAIN_ROUTE)
                                        } else {
                                            snackbarHostState.showSnackbar(loginFailedMessage)
                                        }
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp)
                                .padding(top = 8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(stringResource(id = R.string.login_button), color = Color.White, style = MaterialTheme.typography.labelLarge)
                        }
                    }
                }
            }
        }



        Spacer(modifier = Modifier.height(24.dp))
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            TextButton(onClick = { onNavigate(AuthScreens.Register) }) {
                Text(stringResource(id = R.string.register_prompt))
            }
        }
        if (showLanguageDialog) {
            AlertDialog(
                onDismissRequest = {
                    showLanguageDialog = false
                    tempSelectedLanguageCode = null // Reset temporary selection when dialog is dismissed
                },
                title = { Text(text = stringResource(id = R.string.select_language)) },
                text = {
                    Column {
                        val languages = listOf(
                            stringResource(id = R.string.language_english),
                            stringResource(id = R.string.language_russian),
                            stringResource(id = R.string.language_kazakh)
                        )
                        val languageCodes = listOf("en", "ru", "kk")
                        languages.zip(languageCodes).forEach { (language, code) ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { tempSelectedLanguageCode = code }
                                    .padding(vertical = 8.dp)
                            ) {
                                // Use text color or other visual indicators for selection
                                Text(
                                    text = language,
                                    modifier = Modifier.padding(start = 8.dp),
                                    color = if (tempSelectedLanguageCode == code) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (tempSelectedLanguageCode != null) {
                                // Apply the language change only if a selection has been made
                                LanguageManager.setLocale(context, tempSelectedLanguageCode!!)
                                languageViewModel.setLanguage(context, tempSelectedLanguageCode!!)
                                (context as? MainActivity)?.restartActivity()
                            }
                            showLanguageDialog = false
                            tempSelectedLanguageCode = null // Reset temporary selection after applying
                        }
                    ) {
                        Text(text = "OK")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showLanguageDialog = false
                            tempSelectedLanguageCode = null // Reset temporary selection when dialog is dismissed
                        }
                    ) {
                        Text(text = stringResource(id = android.R.string.cancel))
                    }
                }
            )
        }




        Column(modifier = Modifier.fillMaxWidth().padding(top = 16.dp), horizontalAlignment = Alignment.CenterHorizontally) {

            TextButton(onClick = { showLanguageDialog = true }) {
                Text(text = stringResource(id = R.string.change_language_button))
            }

        }
    }
}

fun getLanguageName(languageCode: String): String {
    return when (languageCode) {
        "en" -> "English"
        "ru" -> "Russian"
        "kk" -> "Kazakh"
        else -> "English"
    }
}
