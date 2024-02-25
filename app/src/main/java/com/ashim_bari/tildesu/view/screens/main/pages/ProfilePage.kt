package com.ashim_bari.tildesu.view.screens.main.pages

import android.annotation.SuppressLint
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.outlined.ModeEdit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.ashim_bari.tildesu.R
import com.ashim_bari.tildesu.model.user.UserProfile
import com.ashim_bari.tildesu.viewmodel.main.MainViewModel

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
    var showLanguageDialog by rememberSaveable { mutableStateOf(false) }
    var showEditProfileDialog by rememberSaveable { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { viewModel.uploadProfileImage(it) }
    }
    val bitmap = rememberSaveable { mutableStateOf<Bitmap?>(null) }
    val userProfile by viewModel.userProfile.observeAsState()
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
    LaunchedEffect(Unit) {
        viewModel.fetchUserProfile()
    }

//    LaunchedEffect(Unit) {
//        viewModel.getUserEmail()
//        // viewModel.getUserProfileImageUrl() // Uncomment if fetching profile image URL
//    }
    if (showEditProfileDialog) {
        // The userProfile from viewModel might be null initially, handle nullability
        EditProfileDialog(
            profile = userProfile,
            onDismiss = { showEditProfileDialog = false },
            onSave = { updatedProfile ->
                viewModel.updateUserProfile(updatedProfile)
                showEditProfileDialog = false // Dismiss the dialog after saving
            }
        )
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
                userProfile?.let { profile ->
                    Row(modifier = Modifier.padding(top = 8.dp)) {
                        Text(
                            text = "${profile.name ?: "Not set"} ${profile.surname ?: ""}",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.align(Alignment.CenterVertically)
                        )
                    }

                    // Continue with the rest of the profile information...
                }
//                userProfile?.let { profile ->
//                    Column(modifier = Modifier.padding(16.dp)) {
//                        Text("Email: ${profile.email ?: "Not set"}", style = MaterialTheme.typography.bodyMedium)
//                        Text("Name: ${profile.name ?: "Not set"}", style = MaterialTheme.typography.bodyMedium)
//                        Text("Surname: ${profile.surname ?: "Not set"}", style = MaterialTheme.typography.bodyMedium)
//                        Text("City: ${profile.city ?: "Not set"}", style = MaterialTheme.typography.bodyMedium)
//                        Text("Age: ${profile.age ?: "Not set"}", style = MaterialTheme.typography.bodyMedium)
//                        Text("Gender: ${if(profile.gender == null) "Not set" else if(profile.gender == 1) "Male" else "Female"}", style = MaterialTheme.typography.bodyMedium) // Assuming gender is an Int that represents Male=1, Female=2
//                        Text("Specialty: ${profile.specialty ?: "Not set"}", style = MaterialTheme.typography.bodyMedium)
//                    }
//                }

                // Continuing inside the Column from above
                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    shape = RoundedCornerShape(8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        userProfile?.let { profile ->
                            Text("Email: ${profile.email ?: "Not set"}", style = MaterialTheme.typography.bodyMedium)
                            Text("City: ${profile.city ?: "Not set"}", style = MaterialTheme.typography.bodyMedium)
                            Text("Age: ${profile.age ?: "Not set"}", style = MaterialTheme.typography.bodyMedium)
                            Text("Gender: ${if(profile.gender == null) "Not set" else if(profile.gender == 1) "Male" else "Female"}", style = MaterialTheme.typography.bodyMedium)
                            Text("Specialty: ${profile.specialty ?: "Not set"}", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }

// Buttons for actions (Edit Profile, Update Password, Logout, etc.)


                Spacer(modifier = Modifier.height(8.dp))

                ActionCard(
                    text = stringResource(id = R.string.edit_profile_button),
                    icon = { Icon(Icons.Filled.Edit, contentDescription = "Edit Profile") },
                    onClick = { showEditProfileDialog = true },
                    modifier = Modifier
                        .height(56.dp)
                        .fillMaxWidth(),
                    backgroundColor = MaterialTheme.colorScheme.background
                )
//                Button(onClick = { showEditProfileDialog = true }) {
//                    Text("Edit Profile")
//                }


                Spacer(modifier = Modifier.height(24.dp))

                ActionCard(
                    text = stringResource(id = R.string.update_password_button),
                    icon = { Icon(Icons.Outlined.ModeEdit, contentDescription = "Update Password") },
                    onClick = { showUpdatePasswordDialog = true },
                    modifier = Modifier
                        .height(56.dp)
                        .fillMaxWidth(),
                    backgroundColor = MaterialTheme.colorScheme.primary
                )


                LanguageChangeDialog(
                    showDialog = showLanguageDialog,
                    onDismiss = { showLanguageDialog = false },
                    onLanguageSelected = { language ->
                        // Handle language selection here
                        // For example, update the app's locale or UI elements as necessary
                        showLanguageDialog = false
                        // You might want to trigger some state change or call a function to apply the language change.
                    }
                )


                Spacer(modifier = Modifier.height(16.dp))

                ActionCard(
                    text = stringResource(id = R.string.log_out_language_button),
                    icon = { Icon(Icons.Filled.ExitToApp, contentDescription = "Log Out")},
                    onClick = { showLogoutDialog = true },
                    modifier = Modifier
                        .height(56.dp)
                        .fillMaxWidth(),
                    backgroundColor = MaterialTheme.colorScheme.errorContainer
                )

                if (showUpdatePasswordDialog) {
                    UpdatePasswordDialog(
                        viewModel = viewModel,
                        snackbarHostState = snackbarHostState, // Pass the snackbarHostState
                        onClose = { showUpdatePasswordDialog = false },
                        onPasswordUpdated = {
                            passwordUpdatedSuccessfully = true
                        }
                    )
                }
                val updatePasswordSuccessfulMessage = stringResource(id = R.string.update_password_successful)

                LaunchedEffect(passwordUpdatedSuccessfully) {
                    if (passwordUpdatedSuccessfully) {
                        snackbarHostState.showSnackbar(
                            message = updatePasswordSuccessfulMessage,
                            duration = SnackbarDuration.Short
                        )
                        // Reset the flag to avoid showing the snackbar again unless another update occurs
                        passwordUpdatedSuccessfully = false
                    }
                }
                if (showLogoutDialog) {
                    AlertDialog(
                        onDismissRequest = { showLogoutDialog = false },
                        title = { Text(stringResource(id = R.string.logout_dialog_title)) },
                        text = { Text(stringResource(id = R.string.logout_dialog_content)) },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    viewModel.logout(navController)
                                    showLogoutDialog = false
                                }
                            ) {
                                Text(stringResource(id = R.string.log_out_language_button))
                            }
                        },
                        dismissButton = {
                            TextButton(
                                onClick = { showLogoutDialog = false }
                            ) {
                                Text(stringResource(id = R.string.cancel_button))
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
fun EditProfileDialog(profile: UserProfile?, onDismiss: () -> Unit, onSave: (UserProfile) -> Unit) {
    var name by remember { mutableStateOf(profile?.name ?: "") }
    var surname by remember { mutableStateOf(profile?.surname ?: "") }
    var city by remember { mutableStateOf(profile?.city ?: "") }
    var age by remember { mutableStateOf(profile?.age ?: "") }
    var gender by remember { mutableStateOf(profile?.gender?.toString() ?: "") }
    var specialty by remember { mutableStateOf(profile?.specialty ?: "") }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Edit Profile") },
        text = {
            Column {
                TextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") }
                )
                TextField(
                    value = surname,
                    onValueChange = { surname = it },
                    label = { Text("Surname") }
                )
                TextField(
                    value = city,
                    onValueChange = { city = it },
                    label = { Text("City") }
                )
                TextField(
                    value = age,
                    onValueChange = { age = it },
                    label = { Text("Age") },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number)
                )
                TextField(
                    value = gender,
                    onValueChange = { gender = it },
                    label = { Text("Gender (1 for Male, 2 for Female)") },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number)
                )
                TextField(
                    value = specialty,
                    onValueChange = { specialty = it },
                    label = { Text("Specialty") }
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                onSave(
                    profile?.copy(
                        name = name,
                        surname = surname,
                        city = city,
                        age = age,
                        gender = gender.toIntOrNull(),
                        specialty = specialty
                    ) ?: UserProfile(
                        name = name,
                        surname = surname,
                        city = city,
                        age = age,
                        gender = gender.toIntOrNull(),
                        specialty = specialty
                    )
                )
            }) {
                Text("Save")
            }
        },
        dismissButton = {
            Button(onClick = { onDismiss() }) {
                Text("Cancel")
            }
        }
    )
}


@Composable
fun ActionCard(
    text: String,
    icon: @Composable (() -> Unit)? = null,
    onClick: () -> Unit,
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.secondaryContainer
) {
    Card(
        onClick = onClick,
        modifier = modifier
            .size(350.dp)
            .fillMaxWidth(), // Adjusted padding for better space utilization.
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Row(
            modifier = Modifier.padding(all = 15.dp), // Adjusted for potentially better text visibility.
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            icon?.invoke()
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f) // Ensures text tries to fill available space, pushing it to be fully visible.
            )
        }
    }
}



@Composable
fun UpdatePasswordDialog(
    viewModel: MainViewModel,
    snackbarHostState: SnackbarHostState, // Accept SnackbarHostState as a parameter
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

    var showSnackbar by remember { mutableStateOf(false) }
    var snackbarMessage by remember { mutableStateOf("") }
    val updatePasswordFailedMessage = stringResource(id = R.string.update_password_failed)
    val passwordNotMatchMessage = stringResource(id = R.string.password_not_match)
    val incorrectPasswordMessage = stringResource(id = R.string.incorrect_password)
    val errorCurrentPasswordEmptyMessage = stringResource(id = R.string.error_current_password_empty)
    val errorNewPasswordEmptyMessage = stringResource(id = R.string.error_new_password_empty)
    val errorConfirmPasswordEmptyMessage = stringResource(id = R.string.error_confirm_password_empty)


    LaunchedEffect(showSnackbar) {
        if (showSnackbar) {
            snackbarHostState.showSnackbar(
                message = snackbarMessage,
                duration = SnackbarDuration.Short
            )
            showSnackbar = false // Reset for next use
        }
    }
    // Inside your composable, after defining the state
    if (showSnackbar) {
        LaunchedEffect(snackbarHostState, snackbarMessage) {
            snackbarHostState.showSnackbar(
                message = snackbarMessage,
                duration = SnackbarDuration.Short
            )
            showSnackbar = false // Reset the flag after showing the snackbar
        }
    }

    AlertDialog(
        onDismissRequest = onClose,
        title = { Text(stringResource(id = R.string.update_password_dialog_title)) },
        text = {
            Column {
                OutlinedTextField(
                    value = currentPassword,
                    onValueChange = { currentPassword = it },
                    label = { Text(stringResource(id = R.string.current_password))  },
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
                    label = { Text(stringResource(id = R.string.new_password))  },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(onNext = { confirmPasswordFocusRequester.requestFocus() }),
                    modifier = Modifier.focusRequester(newPasswordFocusRequester)
                )
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text(stringResource(id = R.string.confirm_password))  },
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
                    if (currentPassword.isBlank()) {
                        errorMessage = errorCurrentPasswordEmptyMessage
                    } else if (newPassword.isBlank()) {
                        errorMessage = errorNewPasswordEmptyMessage
                    } else if (confirmPassword.isBlank()) {
                        errorMessage = errorConfirmPasswordEmptyMessage
                    } else if (newPassword != confirmPassword) {
                        errorMessage = passwordNotMatchMessage
                    } else {
                        viewModel.reAuthenticate(currentPassword) { reAuthSuccess ->
                            if (reAuthSuccess) {
                                viewModel.updatePassword(newPassword) { success ->
                                    if (success) {
                                        onPasswordUpdated()
                                        onClose()
                                    } else {
                                        snackbarMessage = updatePasswordFailedMessage
                                        showSnackbar = true
                                    }
                                }
                            } else {
                                errorMessage = incorrectPasswordMessage
                            }
                        }
                    }
                }
            ) {
                Text(stringResource(id = R.string.update_button))
            }

        },
        dismissButton = {
            Button(onClick = onClose) {
                Text(stringResource(id = R.string.cancel_button))
            }
        }
    )
}

@Composable
fun LanguageChangeDialog(showDialog: Boolean, onDismiss: () -> Unit, onLanguageSelected: (String) -> Unit) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(stringResource(id = R.string.choose_language_title)) },
            text = {
                Column {
                    listOf("English", "Russian", "Kazakh").forEach { language ->
                        TextButton(onClick = { onLanguageSelected(language) }) {
                            Text(language)
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                Button(onClick = onDismiss) {
                    Text(stringResource(id = R.string.cancel_button))
                }
            }
        )
    }
}


@Composable
fun ProfilePicture(imageUrl: String?, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .size(150.dp) // Increased size
            .clip(CircleShape)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp) // Increased elevation for depth
    ) {
        Box(modifier = Modifier.clip(CircleShape)) {
            if (imageUrl != null) {
                Image(
                    painter = rememberAsyncImagePainter(model = imageUrl),
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.default_profile_image),
                    contentDescription = "Default Profile Picture",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}



//@Composable
//fun ProfileAttribute(value: String) {
//    Row(
//        modifier = Modifier.fillMaxWidth(),
//        horizontalArrangement = Arrangement.Center
//    ) {
//        Text(
//            text = value ?: "Loading...", // Show "Loading..." while email is being fetched
//            style = MaterialTheme.typography.bodyLarge
//        )
//    }
//}
