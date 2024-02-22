@file:Suppress("DEPRECATION")

import android.graphics.Bitmap
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.ashim_bari.tildesu.R
import com.ashim_bari.tildesu.viewmodel.main.MainViewModel
import kotlinx.coroutines.delay

@Composable
fun ProfilePage(navController: NavHostController) {
    val viewModel: MainViewModel = viewModel()
    var showUpdatePasswordDialog by rememberSaveable { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val imageUri by rememberSaveable { mutableStateOf<Uri?>(null) }
    val profileImageUrl = viewModel.profileImageUrl.observeAsState().value
    val userEmail by viewModel.userEmail.observeAsState()
    var showLogoutDialog by rememberSaveable { mutableStateOf(false) }
    var passwordUpdatedSuccessfully by rememberSaveable { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { viewModel.uploadProfileImage(it) }
    }
    val bitmap = rememberSaveable { mutableStateOf<Bitmap?>(null) }

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

    Box(modifier = Modifier.fillMaxSize()) {
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
                }

                Spacer(modifier = Modifier.height(16.dp))

                userEmail?.let { email ->
                    ProfileAttribute(email)
                }

                Spacer(modifier = Modifier.height(24.dp))

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
                            // Instead of calling LaunchedEffect here, update the state
                            passwordUpdatedSuccessfully = true
                        }
                    )
                }
                LaunchedEffect(passwordUpdatedSuccessfully) {
                    if (passwordUpdatedSuccessfully) {
                        snackbarHostState.showSnackbar(
                            message = "Password updated successfully.",
                            duration = SnackbarDuration.Short
                        )
                        // Reset the flag to avoid showing the snackbar again unless another update occurs
                        passwordUpdatedSuccessfully = false
                    }
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

        // Correct placement of SnackbarHost within Box layout using 'align'
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
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
    var currentPassword by rememberSaveable { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current
    // FocusRequester instances
    val newPasswordFocusRequester = remember { FocusRequester() }
    val currentPasswordFocusRequester = remember { FocusRequester() }
    val confirmPasswordFocusRequester = remember { FocusRequester() }
    //var isDonePressed by remember { mutableStateOf(false) }
    LaunchedEffect(errorMessage) {
        if (errorMessage != null) {
            delay(3000) // Delay in milliseconds, e.g., 3000ms = 3 seconds
            errorMessage = null // Reset the success message to hide it
        }
    }
//    LaunchedEffect(isDonePressed) {
//        if (isDonePressed) {
//            keyboardController?.hide()
//            isDonePressed = false // Reset the flag
//        }
//    }

    AlertDialog(
        onDismissRequest = onClose,
        title = { Text("Update Password") },
        text = {
            Column {
                OutlinedTextField(
                    value = currentPassword,
                    onValueChange = { currentPassword = it },
                    label = { Text("Current Password") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(onNext = { newPasswordFocusRequester.requestFocus() }),
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(currentPasswordFocusRequester)
                )

                OutlinedTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = { Text("New Password") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(onNext = { confirmPasswordFocusRequester.requestFocus() }),
                    modifier = Modifier.focusRequester(newPasswordFocusRequester)
                )
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Confirm Password") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = {
                        keyboardController?.hide() // Attempt to hide the keyboard
                        // Explicitly clear focus here if possible
                    }),
                    modifier = Modifier.focusRequester(confirmPasswordFocusRequester)
                )
                errorMessage?.let {
                    Text(text = it, color = MaterialTheme.colorScheme.error)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    // First, validate the current password (re-authenticate)
                    viewModel.reAuthenticate(currentPassword) { reAuthSuccess ->
                        if (reAuthSuccess) {
                            // If re-authentication is successful, proceed with password update
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
                        } else {
                            // Handle re-authentication failure
                            errorMessage = "Current password is incorrect."
                        }
                    }
                }
            )
            {
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
    Card(
        modifier = Modifier
            .size(120.dp)
            .clip(CircleShape) // Ensure the card itself is circular
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(modifier = Modifier.clip(CircleShape)) { // Clip the content of the card to a circular shape
            if (imageUrl != null) {
                // Load image from URL
                Image(
                    painter = rememberAsyncImagePainter(model = imageUrl),
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape), // Clip the image to be circular
                    contentScale = ContentScale.Crop // Crop the image to fill the bounds
                )
            } else {
                // Display default placeholder if no image URL
                Image(
                    painter = painterResource(id = R.drawable.default_profile_image),
                    contentDescription = "Default Profile Picture",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape), // Ensure the placeholder is also circular
                    contentScale = ContentScale.Crop // Crop the placeholder to fill the bounds
                )
            }
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
