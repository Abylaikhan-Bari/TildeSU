import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ashim_bari.tildesu.viewmodel.MainViewModel
import kotlinx.coroutines.delay

@Composable
fun ProfilePage(navController: NavHostController) {
    // Get an instance of the ViewModel
    val viewModel: MainViewModel = viewModel()
    var showUpdatePasswordDialog by remember { mutableStateOf(false) }
    var successMessage by remember { mutableStateOf<String?>(null) }
    // Observe the user's email
    val userEmail by viewModel.userEmail.observeAsState()
    // State for showing logout confirmation dialog
    var showLogoutDialog by remember { mutableStateOf(false) }

    // Call the function to fetch user email and possibly other user info like profile image URL
    LaunchedEffect(Unit) {
        viewModel.getUserEmail()
        // viewModel.getUserProfileImageUrl() // Uncomment if fetching profile image URL
    }
    LaunchedEffect(successMessage) {
        if (successMessage != null) {
            delay(3000) // Delay in milliseconds, e.g., 3000ms = 3 seconds
            successMessage = null // Reset the success message to hide it
        }
    }
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(16.dp)
        ) {
            ProfilePicture() // Placeholder for profile picture, can be expanded to show actual user profile image

            Spacer(modifier = Modifier.height(16.dp))

            userEmail?.let { email ->
                ProfileAttribute(email)
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (successMessage != null) {
                Text(
                    text = successMessage!!,
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            OutlinedButton(
                onClick = { showUpdatePasswordDialog = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Filled.Edit, contentDescription = "Update Password")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Update Password")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { showLogoutDialog = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Log Out")
            }
            if (showUpdatePasswordDialog) {
                UpdatePasswordDialog(
                    viewModel = viewModel,
                    onClose = { showUpdatePasswordDialog = false },
                    onPasswordUpdated = {
                        successMessage = "Password updated successfully."
                    }
                )
            }
            if (showLogoutDialog) {
                AlertDialog(
                    onDismissRequest = { showLogoutDialog = false },
                    title = { Text("Confirm Logout") },
                    text = { Text("Are you sure you want to log out?") },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                viewModel.logout(navController)
                                showLogoutDialog = false
                            }
                        ) {
                            Text("Log Out")
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = { showLogoutDialog = false }
                        ) {
                            Text("Cancel")
                        }
                    }
                )
            }
        }
    }




}

@Composable
fun UpdatePasswordDialog(
    viewModel: MainViewModel,
    onClose: () -> Unit,
    onPasswordUpdated: () -> Unit
) {
    var newPassword by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }
    var errorMessage by rememberSaveable { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onClose,
        title = { Text("Update Password") },
        text = {
            Column {
                OutlinedTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = { Text("New Password") }
                )
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Confirm Password") }
                )
                errorMessage?.let {
                    Text(text = it, color = MaterialTheme.colorScheme.error)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (newPassword == confirmPassword) {
                        viewModel.updatePassword(newPassword) { success ->
                            if (success) {
                                onPasswordUpdated()
                                onClose()
                            } else {
                                errorMessage = "Failed to update password."
                            }
                        }
                    } else {
                        errorMessage = "Passwords do not match."
                    }
                }
            ) {
                Text("Update")
            }
        },
        dismissButton = {
            Button(onClick = onClose) {
                Text("Cancel")
            }
        }
    )
}
@Composable
fun ProfilePicture() {
    Card(
        modifier = Modifier
            .size(120.dp)
            .clip(CircleShape),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        // Placeholder for profile picture
    }
}

@Composable
fun ProfileAttribute(value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = value ?: "Loading...", // Show "Loading..." while email is being fetched
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
