package com.ashim_bari.tildesu.view.screens.main

import DashboardPage
import android.content.Context
import android.os.Build
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
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
import com.ashim_bari.tildesu.view.screens.main.pages.UsefulPage
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
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
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
        // Optional: Adjust the scrim color for a better UX when the drawer is open
        scrimColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.32f),
        modifier = Modifier.fillMaxSize()
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
                    actions = {
                        if (currentMainScreen == MainScreens.Dashboard) {
                            IconButton(onClick = {
                                mainViewModel.refreshUserProgress()
                            }) {
                                Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                            }
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
            LazyColumn(
                contentPadding = innerPadding,
                modifier = Modifier.fillMaxSize() // Fill the available space
            ) {
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
    ModalDrawerSheet {
        Text(
            stringResource(id = R.string.app_name),
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.headlineMedium // Adjust the text style to match the design
        )
        Divider()
        NavigationDrawerItem(
            label = { Text(text = stringResource(id = R.string.bottom_nav_home)) },
            selected = false,
            onClick = {
                navController.navigate("main")
                onDestinationClicked()
            },
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") }
        )
        NavigationDrawerItem(
            label = { Text(text = stringResource(id = R.string.gemini)) },
            selected = false,
            onClick = {
                navController.navigate("gemini_route")
                onDestinationClicked()
            },
            icon = { Icon(Icons.Default.Chat, contentDescription = "Gemini") }
        )
//        NavigationDrawerItem(
//            label = { Text(text = stringResource(id = R.string.gpt)) },
//            selected = false,
//            onClick = {
//                navController.navigate("gpt_route")
//                onDestinationClicked()
//            },
//            icon = { Icon(Icons.Default.Camera, contentDescription = "GPT") }
//        )
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
        MainScreens.Useful -> UsefulPage(navController)
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