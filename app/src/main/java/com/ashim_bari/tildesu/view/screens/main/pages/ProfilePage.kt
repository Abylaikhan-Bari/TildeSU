import android.graphics.Bitmap
import android.graphics.ColorFilter
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.BlendMode.Companion.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import com.ashim_bari.tildesu.R
import com.ashim_bari.tildesu.viewmodel.MainViewModel
import kotlinx.coroutines.delay

@Composable
fun ProfilePage(navController: NavHostController) {
    // Get an instance of the ViewModel
    val viewModel: MainViewModel = viewModel()
    var showUpdatePasswordDialog by rememberSaveable { mutableStateOf(false) }
    var successMessage by rememberSaveable { mutableStateOf<String?>(null) }
    // Observe the user's email
    val userEmail by viewModel.userEmail.observeAsState()
    // State for showing logout confirmation dialog
    var showLogoutDialog by remember { mutableStateOf(false) }
    var imageUri by rememberSaveable { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current
    val bitmap = rememberSaveable { mutableStateOf<Bitmap?>(null) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { viewModel.uploadProfileImage(it) }
    }

    // Fetch profile image URL from ViewModel
    val profileImageUrl = viewModel.profileImageUrl.observeAsState().value

    // Other code remains the same

    LaunchedEffect(imageUri) {
        imageUri?.let {
            if (Build.VERSION.SDK_INT < 28) {
                bitmap.value = MediaStore.Images.Media.getBitmap(context.contentResolver, it)
            } else {
                val source = ImageDecoder.createSource(context.contentResolver, it)
                bitmap.value = ImageDecoder.decodeBitmap(source)
            }
        }
    }
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

            ProfilePicture(profileImageUrl) {
                launcher.launch("image/*")
            }// Placeholder for profile picture, can be expanded to show actual user profile image

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
fun ProfilePicture(imageUrl: String?, onClick: () -> Unit) {
    // Display the profile image from URL or a placeholder
    Card(
        modifier = Modifier
            .size(120.dp)
            .clip(CircleShape)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        if (imageUrl != null) {
            Image(
                painter = rememberAsyncImagePainter(imageUrl),
                contentDescription = "Profile Picture",
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Text("Tap to select image", modifier = Modifier.padding(16.dp))
        }
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
