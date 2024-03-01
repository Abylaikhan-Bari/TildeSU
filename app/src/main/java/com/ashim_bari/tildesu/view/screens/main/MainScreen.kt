package com.ashim_bari.tildesu.view.screens.main

import DashboardPage
import UsefulPage
import android.content.Context
import android.os.Build
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.ashim_bari.tildesu.R
import com.ashim_bari.tildesu.view.navigation.Navigation
import com.ashim_bari.tildesu.view.screens.main.pages.HomePage
import com.ashim_bari.tildesu.view.screens.main.pages.ProfilePage
import com.ashim_bari.tildesu.viewmodel.main.MainViewModel

private const val TAG = "MainScreen"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavHostController) {
    Log.d(TAG, "MainScreen: Started")
    var currentMainScreen by rememberSaveable { mutableStateOf(MainScreens.Home) }
    val mainViewModel: MainViewModel = viewModel()
    val isLoggedIn by mainViewModel.isLoggedIn.observeAsState()
    var showExitConfirmation by rememberSaveable { mutableStateOf(false) }
    val activity = LocalContext.current as? ComponentActivity
    val context = LocalContext.current
    val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        vibratorManager.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    LaunchedEffect(isLoggedIn) {
        Log.d(TAG, "LaunchedEffect: isLoggedIn changed to $isLoggedIn")
        if (isLoggedIn == false) {
            Log.d(TAG, "LaunchedEffect: User not logged in, navigating to AuthenticationScreen")
            navController.navigate(Navigation.AUTHENTICATION_ROUTE) {
                popUpTo("main") { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    val bottomItems = listOf(
        BottomNavItem(stringResource(id = R.string.bottom_nav_home), Icons.Filled.Home, MainScreens.Home),
        BottomNavItem(stringResource(id = R.string.bottom_nav_dashboard), Icons.Filled.Assessment, MainScreens.Dashboard),
        BottomNavItem(stringResource(id = R.string.bottom_nav_useful), Icons.Filled.Star, MainScreens.Useful),
        BottomNavItem(stringResource(id = R.string.bottom_nav_profile), Icons.Filled.Person, MainScreens.Profile)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = bottomItems.first { it.screen == currentMainScreen }.title, color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        },


        bottomBar = {
            NavigationBar {
                bottomItems.forEach { item ->
                    val isSelected = currentMainScreen == item.screen
                    NavigationBarItem(
                        icon = {
                            Icon(
                                item.icon,
                                contentDescription = item.title,
                                tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        label = {
                            Text(
                                item.title,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        selected = isSelected,
                        onClick = {
                            Log.d(TAG, "NavigationBarItem: ${item.title} clicked")
                            currentMainScreen = item.screen
//                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                                vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
//                            } else {
//                                @Suppress("DEPRECATION")
//                                vibrator.vibrate(50)
//                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        BackHandler {
            Log.d(TAG, "BackHandler: Back button pressed")
            showExitConfirmation = true
        }

        if (showExitConfirmation) {
            AlertDialog(
                onDismissRequest = {
                    showExitConfirmation = false
                },
                title = { Text(stringResource(id = R.string.exit_dialog_title)) },
                text = { Text(stringResource(id = R.string.exit_dialog_content)) },
                confirmButton = {
                    Button(
                        onClick = {
                            activity?.finish()
                        }
                    ) {
                        Text(stringResource(id = R.string.exit_dialog_yes))
                    }
                },
                dismissButton = {
                    Button(
                        onClick = {
                            showExitConfirmation = false
                        }
                    ) {
                        Text(stringResource(id = R.string.exit_dialog_no))
                    }
                }
            )
        }

        LazyColumn(contentPadding = innerPadding) {
            item {
                MainScreenContent(currentMainScreen, navController)
            }
        }
    }
    Log.d(TAG, "MainScreen: Ended")
}

@Composable
fun MainScreenContent(currentScreen: MainScreens, navController: NavHostController, modifier: Modifier = Modifier) {
    val mainViewModel: MainViewModel = viewModel()

    when (currentScreen) {
        MainScreens.Home -> HomePage(navController)
        MainScreens.Dashboard -> DashboardPage(mainViewModel)
        MainScreens.Useful -> UsefulPage(navController) {}
        MainScreens.Profile -> ProfilePage(navController)
    }
}

data class BottomNavItem(
    val title: String,
    val icon: ImageVector,
    val screen: MainScreens
)

enum class MainScreens {
    Home,
    Dashboard,
    Useful,
    Profile
}
