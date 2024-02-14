package com.ashim_bari.tildesu.view.screens.authentication.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.Visibility
import androidx.navigation.NavHostController
import com.ashim_bari.tildesu.view.navigation.Navigation
import com.ashim_bari.tildesu.view.screens.authentication.AuthScreens
import com.ashim_bari.tildesu.viewmodel.AuthenticationViewModel
import androidx.compose.material.icons.filled.*

@Composable
fun LoginPage(
    navController: NavHostController,
    onNavigate: (AuthScreens) -> Unit,
    viewModel: AuthenticationViewModel // Inject your AuthenticationViewModel
) {
    var username by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var authMessage by rememberSaveable { mutableStateOf<String?>(null) } // Holds the authentication message
    var passwordVisibility by rememberSaveable { mutableStateOf(false) }


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
            text = "Login",
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Email") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            singleLine = true,
            visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { passwordVisibility = !passwordVisibility }) {
                    Icon(
                        imageVector = if (passwordVisibility) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                        contentDescription = if (passwordVisibility) "Hide password" else "Show password"
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            horizontalArrangement = Arrangement.End,
            modifier = Modifier.fillMaxWidth()
        ) {
            TextButton(onClick = { onNavigate(AuthScreens.ResetPassword) }) {
                Text("Forgot password?")
            }
        }
        Button(
            onClick = {
                // Call login function from viewMod
                viewModel.login(username, password) { success ->
                    if (success) {
                        // Login successful, navigate to next screen
                        navController.navigate(Navigation.MAIN_ROUTE) // Replace "next_screen_route" with the actual route to navigate to
                        authMessage = null
                    } else {
                        // Login failed, you can show an error message to the user
                        // For example, you can update a state variable to display an error message

                        authMessage = "Login failed. Please check your credentials."
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("Login", color = Color.White)
        }

        Spacer(modifier = Modifier.height(24.dp))
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            TextButton(onClick = { onNavigate(AuthScreens.Register) }) {
                Text("Don't have an account? Register")
            }
        }
    }
}