package com.ashim_bari.tildesu.view.screens.main.pages

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.ashim_bari.tildesu.viewmodel.AuthenticationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfilePage(navController: NavHostController, viewModel: AuthenticationViewModel) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }
    var updateResult by rememberSaveable { mutableStateOf<Boolean?>(null) }
    var errorMessage by rememberSaveable { mutableStateOf<String?>(null) }
    var successMessage by rememberSaveable { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(text = "Update Password")},
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary))
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding) // Apply the padding provided by Scaffold here
                .padding(horizontal = 16.dp), // Apply additional padding as needed
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirm Password") },
                modifier = Modifier.fillMaxWidth()
            )
            Row {
                Button(
                    onClick = {
                        // Navigate back without updating
                        navController.navigateUp()
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Back")
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = {
                        if (password == confirmPassword) {
                            viewModel.updatePassword(password) { pwdSuccess ->
                                updateResult = pwdSuccess
                                if (pwdSuccess) {
                                    errorMessage = null
                                    successMessage = "Password updated successfully."
                                    // Navigate back to the profile page on success
                                    navController.navigate("profile") {
                                        // Remove all the non-top-level destinations from the back stack to prevent back navigation to this page
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        // Optionally, you can restore state when coming back to the start destination
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                } else {
                                    errorMessage = "Failed to update password."
                                }
                            }
                        } else {
                            errorMessage = "Passwords do not match."
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Update")
                }
            }

            if (!errorMessage.isNullOrEmpty()) {
                Text(
                    text = errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}


