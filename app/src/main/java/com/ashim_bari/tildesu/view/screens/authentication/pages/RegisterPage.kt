package com.ashim_bari.tildesu.view.screens.authentication.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.ashim_bari.tildesu.view.navigation.Navigation
import com.ashim_bari.tildesu.view.screens.authentication.AuthScreens
import com.ashim_bari.tildesu.viewmodel.authentication.AuthenticationViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun RegisterPage(
    navController: NavHostController,
    onNavigate: (AuthScreens) -> Unit,
    viewModel: AuthenticationViewModel,
    snackbarHostState: SnackbarHostState,
    coroutineScope: CoroutineScope
) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }
    var authMessage by rememberSaveable { mutableStateOf<String?>(null) } // Holds the authentication message
    var passwordVisibility by rememberSaveable { mutableStateOf(false) }
    var confirmpasswordVisibility by rememberSaveable { mutableStateOf(false) }
    // Remember FocusRequester for the password field to request focus programmatically
    val passwordFocusRequester = remember { FocusRequester() }
    // Remember FocusRequester for the confirmPassword field to request focus programmatically
    val confirmPasswordFocusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        authMessage?.let {
            Text(text = it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(bottom = 8.dp))
        }
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = "Register",
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { passwordFocusRequester.requestFocus() }),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        // Password TextField with focus management and keyboard actions
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            singleLine = true,
            visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { confirmPasswordFocusRequester.requestFocus() }),
            trailingIcon = {
                IconButton(onClick = { passwordVisibility = !passwordVisibility }) {
                    Icon(
                        imageVector = if (passwordVisibility) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                        contentDescription = if (passwordVisibility) "Hide password" else "Show password"
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(passwordFocusRequester) // Ensure you have declared and initialized passwordFocusRequester
        )
        Spacer(modifier = Modifier.height(8.dp))

// Confirm Password TextField with focus management and keyboard actions
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirm Password") },
            singleLine = true,
            visualTransformation = if (confirmpasswordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = {
                keyboardController?.hide() // Hide the keyboard when the user presses done
                // Here, you might want to trigger the form submission logic
            }),
            trailingIcon = {
                IconButton(onClick = { confirmpasswordVisibility = !confirmpasswordVisibility }) {
                    Icon(
                        imageVector = if (confirmpasswordVisibility) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                        contentDescription = if (confirmpasswordVisibility) "Hide confirm password" else "Show confirm password"
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(confirmPasswordFocusRequester) // Ensure you have declared and initialized confirmPasswordFocusRequester
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                coroutineScope.launch {
                    if (password == confirmPassword) {
                        val success = viewModel.register(email, password)
                        if (success) {
                            snackbarHostState.showSnackbar("Registration successful")
                            navController.navigate(Navigation.AUTHENTICATION_ROUTE)
                        } else {
                            snackbarHostState.showSnackbar("Registration failed. Please try again.")
                        }
                    } else {
                        snackbarHostState.showSnackbar("Passwords do not match.")
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text("Register", color = Color.White)
        }


        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            TextButton(onClick = { onNavigate(AuthScreens.Login) }) {
                Text("Already have an account? Login")
            }
        }
    }
}