package com.ashim_bari.tildesu.view.screens.main.pages

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.outlined.ModeEdit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.ashim_bari.tildesu.R
import com.ashim_bari.tildesu.model.language.LanguageManager
import com.ashim_bari.tildesu.model.user.UserProfile
import com.ashim_bari.tildesu.view.MainActivity
import com.ashim_bari.tildesu.viewmodel.language.LanguageViewModel
import com.ashim_bari.tildesu.viewmodel.main.MainViewModel

@Composable
fun ProfilePage(navController: NavHostController) {
    val viewModel: MainViewModel = hiltViewModel()
    val languageViewModel: LanguageViewModel = hiltViewModel()
    var showUpdatePasswordDialog by rememberSaveable { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val imageUri by rememberSaveable { mutableStateOf<Uri?>(null) }
    val profileImageUrl = viewModel.profileImageUrl.observeAsState().value
    var showLogoutDialog by rememberSaveable { mutableStateOf(false) }
    var passwordUpdatedSuccessfully by rememberSaveable { mutableStateOf(false) }
    var showLanguageDialog by rememberSaveable { mutableStateOf(false) }
    var tempSelectedLanguageCode by rememberSaveable { mutableStateOf<String?>(null) }
    var showEditProfileDialog by rememberSaveable { mutableStateOf(false) }
    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
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
    if (showEditProfileDialog) {
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
                Spacer(modifier = Modifier.height(16.dp))
                userProfile?.let { profile ->
                    ProfilePicture(profileImageUrl) {
                        launcher.launch("image/*")
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(modifier = Modifier.padding(top = 8.dp)) {
                        Text(
                            text = "${profile.name ?: "Not set"} ${profile.surname ?: ""}",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.align(Alignment.CenterVertically)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    UserInfoCard(profile = profile) {
                        showEditProfileDialog = true // Open dialog on click
                    }
                }
                // Continuing inside the Column from above
                Spacer(modifier = Modifier.height(16.dp))

                ActionCard(
                    text = stringResource(id = R.string.update_password_button),
                    icon = {
                        Icon(
                            Icons.Outlined.ModeEdit,
                            contentDescription = "Update Password"
                        )
                    },
                    onClick = { showUpdatePasswordDialog = true },
                    modifier = Modifier
                        .height(56.dp)
                        .fillMaxWidth(),
                    backgroundColor = MaterialTheme.colorScheme.background
                )
                Spacer(modifier = Modifier.height(16.dp))
                ActionCard(
                    text = stringResource(id = R.string.change_language), // Make sure this resource exists
                    icon = { Icon(Icons.Default.Language, contentDescription = "Change Language") },
                    onClick = { showLanguageDialog = true },
                    modifier = Modifier
                        .height(56.dp)
                        .fillMaxWidth(),
                    backgroundColor = MaterialTheme.colorScheme.background
                )

                if (showLanguageDialog) {
                    AlertDialog(
                        onDismissRequest = { showLanguageDialog = false },
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
                                            .clickable {
                                                tempSelectedLanguageCode = code
                                                showLanguageDialog = false
                                                // Apply the language change
                                                LanguageManager.setLocale(context, code)
                                                languageViewModel.setLanguage(context, code)
                                                // This line requires your MainActivity to have a restartActivity method
                                                (context as? MainActivity)?.restartActivity()
                                            }
                                            .padding(vertical = 8.dp)
                                    ) {
                                        Text(
                                            text = language,
                                            modifier = Modifier.padding(start = 8.dp),
                                            color = if (tempSelectedLanguageCode == code) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        },
                        confirmButton = { },
                        dismissButton = {
                            TextButton(
                                onClick = { showLanguageDialog = false }
                            ) {
                                Text(text = stringResource(id = android.R.string.cancel))
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                ActionCard(
                    text = stringResource(id = R.string.log_out_language_button),
                    icon = { Icon(Icons.Filled.ExitToApp, contentDescription = "Log Out") },
                    onClick = { showLogoutDialog = true },
                    modifier = Modifier
                        .height(56.dp)
                        .fillMaxWidth(),
                    backgroundColor = MaterialTheme.colorScheme.background
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
                val updatePasswordSuccessfulMessage =
                    stringResource(id = R.string.update_password_successful)
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
                            Button(
                                onClick = {
                                    viewModel.logout(navController)
                                    showLogoutDialog = false
                                }
                            ) {
                                Text(stringResource(id = R.string.log_out_language_button))
                            }
                        },
                        dismissButton = {
                            Button(
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
fun UserInfoCard(profile: UserProfile, onEditClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(onClick = onEditClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            UserInfoItem(label = stringResource(id = R.string.email), value = profile.email)
            UserInfoItem(
                label = stringResource(id = R.string.city),
                value = profile.city ?: stringResource(R.string.not_set)
            )
            UserInfoItem(label = stringResource(id = R.string.age), value = profile.age.toString())
            UserInfoItem(
                label = stringResource(id = R.string.gender),
                value = getGenderString(profile.gender)
            )
            UserInfoItem(
                label = stringResource(id = R.string.specialty),
                value = profile.specialty ?: stringResource(R.string.not_set)
            )
        }
    }
}

@Composable
fun getGenderString(gender: Int?): String {
    // Assuming 1 is Male, 2 is Female, and else is Not Set or Other
    return when (gender) {
        1 -> stringResource(id = R.string.gender_male)
        2 -> stringResource(id = R.string.gender_female)
        else -> stringResource(id = R.string.not_set)
    }
}

@Composable
fun UserInfoItem(label: String, value: String) {
    Row(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(
            "$label: ",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun EditProfileDialog(profile: UserProfile?, onDismiss: () -> Unit, onSave: (UserProfile) -> Unit) {
    var name by remember { mutableStateOf(profile?.name ?: "") }
    var surname by remember { mutableStateOf(profile?.surname ?: "") }
    var city by remember { mutableStateOf(profile?.city ?: "") }
    var age by remember { mutableStateOf(profile?.age ?: "") }
    var selectedGender by remember {
        mutableStateOf(
            profile?.gender ?: 0
        )
    } // Use 0 for not set, 1 for male, 2 for female
    var specialty by remember { mutableStateOf(profile?.specialty ?: "") }
    val nameFocusRequester = remember { FocusRequester() }
    val surnameFocusRequester = remember { FocusRequester() }
    val cityFocusRequester = remember { FocusRequester() }
    val ageFocusRequester = remember { FocusRequester() }
    val specialtyFocusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text(stringResource(id = R.string.edit_profile_button)) },
        text = {
            Column {
                TextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(stringResource(id = R.string.name)) },
                    modifier = Modifier.focusRequester(nameFocusRequester),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(onNext = { surnameFocusRequester.requestFocus() })
                )
                TextField(
                    value = surname,
                    onValueChange = { surname = it },
                    label = { Text(stringResource(id = R.string.surname)) },
                    modifier = Modifier.focusRequester(surnameFocusRequester),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(onNext = { cityFocusRequester.requestFocus() })
                )
                TextField(
                    value = city,
                    onValueChange = { city = it },
                    label = { Text(stringResource(id = R.string.city)) },
                    modifier = Modifier.focusRequester(cityFocusRequester),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(onNext = { ageFocusRequester.requestFocus() })
                )
                TextField(
                    value = age,
                    onValueChange = { age = it },
                    label = { Text(stringResource(id = R.string.age)) },
                    modifier = Modifier.focusRequester(ageFocusRequester),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = androidx.compose.ui.text.input.KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(onNext = { specialtyFocusRequester.requestFocus() })
                )
                GenderSelection(
                    selectedGender = selectedGender,
                    onGenderSelected = { selectedGender = it })
                TextField(
                    value = specialty,
                    onValueChange = { specialty = it },
                    label = { Text(stringResource(id = R.string.specialty)) },
                    modifier = Modifier.focusRequester(specialtyFocusRequester),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() })
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                keyboardController?.hide()
                onSave(
                    UserProfile(
                        email = profile?.email ?: "",
                        name = name,
                        surname = surname,
                        city = city,
                        age = age,
                        gender = selectedGender,
                        specialty = specialty
                    )
                )
                onDismiss()
            }) {
                Text(stringResource(id = R.string.confirm))
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text(stringResource(id = R.string.cancel_button))
            }
        }
    )
}

@Composable
fun GenderSelection(selectedGender: Int, onGenderSelected: (Int) -> Unit) {
    Column {
        Text(stringResource(id = R.string.gender))
        Row {
            RadioButton(
                selected = selectedGender == 1,
                onClick = { onGenderSelected(1) }
            )
            Text(
                stringResource(id = R.string.gender_male), modifier = Modifier
                    .clickable(onClick = { onGenderSelected(1) })
                    .padding(start = 4.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            RadioButton(
                selected = selectedGender == 2,
                onClick = { onGenderSelected(2) }
            )
            Text(
                stringResource(id = R.string.gender_female), modifier = Modifier
                    .clickable(onClick = { onGenderSelected(2) })
                    .padding(start = 4.dp)
            )
        }
    }
}

@Composable
fun ActionCard(
    text: String,
    icon: @Composable (() -> Unit)? = null,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.secondaryContainer
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(key1 = true) {
        visible = true
    }
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + slideInVertically(),
        exit = fadeOut()
    ) {
        Card(
            onClick = onClick,
            modifier = modifier
                .size(350.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(containerColor = backgroundColor)
        ) {
            Row(
                modifier = Modifier.padding(all = 15.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                icon?.invoke()
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.weight(1f)
                )
            }
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
    val errorCurrentPasswordEmptyMessage =
        stringResource(id = R.string.error_current_password_empty)
    val errorNewPasswordEmptyMessage = stringResource(id = R.string.error_new_password_empty)
    val errorConfirmPasswordEmptyMessage =
        stringResource(id = R.string.error_confirm_password_empty)
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
                    label = { Text(stringResource(id = R.string.current_password)) },
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
                    label = { Text(stringResource(id = R.string.new_password)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(onNext = { confirmPasswordFocusRequester.requestFocus() }),
                    modifier = Modifier.focusRequester(newPasswordFocusRequester)
                )
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text(stringResource(id = R.string.confirm_password)) },
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
                    keyboardController?.hide()
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
            keyboardController?.hide()

            Button(onClick = onClose) {
                Text(stringResource(id = R.string.cancel_button))
            }
        }
    )
}

@Composable
fun ProfilePicture(imageUrl: String?, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .size(120.dp)
            .clip(CircleShape)
            .clickable(onClick = onClick),
        shape = CircleShape
    ) {
        Image(
            painter = if (imageUrl != null) rememberAsyncImagePainter(model = imageUrl) else painterResource(
                id = R.drawable.default_profile_image
            ),
            contentDescription = "Profile Picture",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}