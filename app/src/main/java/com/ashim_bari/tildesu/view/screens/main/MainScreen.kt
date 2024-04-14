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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.ashim_bari.tildesu.R
import com.ashim_bari.tildesu.view.navigation.Navigation
import com.ashim_bari.tildesu.view.screens.main.pages.HomePage
import com.ashim_bari.tildesu.view.screens.main.pages.ProfilePage
import com.ashim_bari.tildesu.view.screens.main.pages.TranslatePage
import com.ashim_bari.tildesu.viewmodel.main.MainViewModel
import kotlinx.coroutines.launch

private const val TAG = "MainScreen"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavHostController) {
    Log.d(TAG, "MainScreen: Started")
    var currentMainScreen by rememberSaveable { mutableStateOf(MainScreens.Home) }
    val mainViewModel: MainViewModel = hiltViewModel()
    val isLoggedIn by mainViewModel.isLoggedIn.observeAsState()
    var showExitConfirmation by rememberSaveable { mutableStateOf(false) }
    val activity = LocalContext.current as? ComponentActivity
    val context = LocalContext.current
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager =
            context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
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
        BottomNavItem(
            stringResource(id = R.string.bottom_nav_home),
            Icons.Filled.Home,
            MainScreens.Home
        ),
        BottomNavItem(
            stringResource(id = R.string.bottom_nav_dashboard),
            Icons.Filled.Assessment,
            MainScreens.Dashboard
        ),
        BottomNavItem(
            stringResource(id = R.string.bottom_nav_useful),
            Icons.Filled.Star,
            MainScreens.Useful
        ),
        BottomNavItem(
            stringResource(id = R.string.bottom_nav_translate),
            Icons.Filled.Translate,
            MainScreens.Translate
        ),
        BottomNavItem(
            stringResource(id = R.string.bottom_nav_profile),
            Icons.Filled.Person,
            MainScreens.Profile
        )
    )
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            Drawer(navController = navController, onDestinationClicked = {
                scope.launch { drawerState.close() }
            })
        },
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = bottomItems.first { it.screen == currentMainScreen }.title,
                            color = Color.White
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    },
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
    }
    Log.d(TAG, "MainScreen: Ended")
}

@Composable
fun Drawer(navController: NavHostController, onDestinationClicked: () -> Unit) {
    Column(modifier = Modifier.padding(top = 24.dp)) {
        // Your other drawer content goes here...
        DrawerItem(
            title = "Home",
            icon = Icons.Default.Home,
            onClick = {
                navController.navigate("main")
                onDestinationClicked()
            }
        )
        DrawerItem(
            title = "Chat",
            icon = Icons.Default.Chat,
            onClick = {
                navController.navigate("chat_route")
                onDestinationClicked()
            }
        )
    }
}

@Composable
fun DrawerItem(title: String, icon: ImageVector, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = null)
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = title)
    }
}

@Composable
fun MainScreenContent(
    currentScreen: MainScreens,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    when (currentScreen) {
        MainScreens.Home -> HomePage(navController)
        MainScreens.Dashboard -> DashboardPage()
        MainScreens.Useful -> UsefulPage(navController) {}
        MainScreens.Profile -> ProfilePage(navController)
        MainScreens.Translate -> TranslatePage(navController)
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
    Profile,
    Translate
}