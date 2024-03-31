package com.ashim_bari.tildesu.view.screens.authentication.pages

import android.annotation.SuppressLint
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.ashim_bari.tildesu.R
import com.ashim_bari.tildesu.view.navigation.Navigation
import com.ashim_bari.tildesu.view.screens.authentication.AuthScreens
import com.ashim_bari.tildesu.view.ui.theme.BluePrimary
import com.ashim_bari.tildesu.viewmodel.authentication.AuthenticationViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@SuppressLint("UnusedCrossfadeTargetStateParameter")
@Composable
fun ResetPasswordPage(
    navController: NavHostController,
    onNavigate: (AuthScreens) -> Unit,
    snackbarHostState: SnackbarHostState,
    coroutineScope: CoroutineScope
) {
    val viewModel: AuthenticationViewModel = hiltViewModel()
    var email by rememberSaveable { mutableStateOf("") }
    var authMessage by rememberSaveable { mutableStateOf<String?>(null) } // Holds the authentication message
    val keyboardController = LocalSoftwareKeyboardController.current
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp), // Adjust padding to control the thickness of the outline
        color = MaterialTheme.colorScheme.surfaceVariant, // Choose a contrasting color for the outline
        shape = RoundedCornerShape(14.dp) // Apply rounded corners if desired
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(2.dp), // This padding will act as the outline thickness
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(8.dp), // Adjust elevation to control the shadow
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.background // Choose a color that contrasts with the outline
            ),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                authMessage?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(id = R.string.reset_password),
                    style = MaterialTheme.typography.headlineSmall
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text(stringResource(id = R.string.email)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .animateContentSize(),
                    contentAlignment = Alignment.Center
                ) {
                    var isLoading by rememberSaveable { mutableStateOf(false) }
                    var isSuccess by rememberSaveable { mutableStateOf(false) }
                    Crossfade(targetState = isLoading || isSuccess, label = "Reset Password") {
                        val resetSuccessfulMessage = stringResource(id = R.string.reset_successful)
                        val resetFailedMessage = stringResource(id = R.string.reset_failed)
                        when {
                            isLoading -> CircularProgressIndicator(color = BluePrimary)
                            isSuccess -> Icon(
                                Icons.Filled.Check,
                                contentDescription = "Success",
                                tint = BluePrimary
                            )

                            else -> Button(
                                onClick = {
                                    keyboardController?.hide()
                                    coroutineScope.launch {
                                        isLoading = true
                                        isSuccess = false // Reset success state
                                        val success = viewModel.resetPassword(email)
                                        isLoading = false
                                        isSuccess = success
                                        if (success) {
                                            snackbarHostState.showSnackbar(resetSuccessfulMessage)
                                            navController.navigate(Navigation.AUTHENTICATION_ROUTE)
                                        } else {
                                            snackbarHostState.showSnackbar(resetFailedMessage)
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxSize()
                                    .size(100.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                                shape = RoundedCornerShape(12.dp)// Use BluePrimary for the Button color
                            ) {
                                Text(
                                    stringResource(id = R.string.confirm_reset_button),
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(onClick = { onNavigate(AuthScreens.Login) }) {
                        Text(stringResource(id = R.string.login))
                    }
                    TextButton(onClick = { onNavigate(AuthScreens.Register) }) {
                        Text(stringResource(id = R.string.register))
                    }
                }
            }
        }
    }
}