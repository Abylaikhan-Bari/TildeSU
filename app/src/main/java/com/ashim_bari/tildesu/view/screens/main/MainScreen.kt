package com.ashim_bari.tildesu.view.screens.main
import DashboardPage
import UsefulPage
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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavHostController) {
    var currentMainScreen by rememberSaveable { mutableStateOf(MainScreens.Home) }
    val mainViewModel: MainViewModel = viewModel()
    val isLoggedIn by mainViewModel.isLoggedIn.observeAsState()
    var showExitConfirmation by rememberSaveable { mutableStateOf(false) }
    val activity = LocalContext.current as? ComponentActivity
    // This will trigger when isLoggedIn changes its value.
    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn == false) {
            // Navigate to AuthenticationScreen if the user is not logged in
            navController.navigate(Navigation.AUTHENTICATION_ROUTE) {
                // Remove all entries from the back stack up to the authentication route
                // and launch the authentication route as a single top instance
                popUpTo("main") { inclusive = true }
                launchSingleTop = true
            }
        }
    }
    // Define Bottom Navigation Items
    val bottomItems = listOf(
        BottomNavItem(stringResource(id = R.string.bottom_nav_home), Icons.Filled.Home, MainScreens.Home),
        // Using Icons.Filled.Assessment as an example of a more appropriate dashboard icon
        BottomNavItem(stringResource(id = R.string.bottom_nav_dashboard), Icons.Filled.Assessment, MainScreens.Dashboard),
        BottomNavItem(stringResource(id = R.string.bottom_nav_useful), Icons.Filled.Star, MainScreens.Useful),
        BottomNavItem(stringResource(id = R.string.bottom_nav_profile), Icons.Filled.Person, MainScreens.Profile)
    )


    Scaffold(
        topBar = {
            // Dynamic TopAppBar using Material3
            TopAppBar(
                title = { Text(text = bottomItems.first { it.screen == currentMainScreen }.title, color = Color.White) }, // Directly setting the Text color
                // If containerColor is available and you need to set it
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                    // If contentColor isn't recognized or needed, ensure color is set directly in the Text composable
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
                        onClick = { currentMainScreen = item.screen }
                    )
                }
            }
        }


    ) { innerPadding ->


        BackHandler {
            showExitConfirmation = true
        }

        if (showExitConfirmation) {
            AlertDialog(
                onDismissRequest = {
                    // If the dialog is dismissed, don't exit the app
                    showExitConfirmation = false
                },
                title = { Text(stringResource(id = R.string.exit_dialog_title)) },
                text = { Text(stringResource(id = R.string.exit_dialog_content)) },
                confirmButton = {
                    Button(
                        onClick = {
                            // Handle the logic to exit the app
                            activity?.finish()
                        }
                    ) {
                        Text(stringResource(id = R.string.exit_dialog_yes))
                    }
                },
                dismissButton = {
                    Button(
                        onClick = {
                            // Dismiss the dialog and don't exit the app
                            showExitConfirmation = false
                        }
                    ) {
                        Text(stringResource(id = R.string.exit_dialog_no))
                    }
                }
            )
        }
        // Scrollable screen content based on currentMainScreen
        LazyColumn(contentPadding = innerPadding) {
            item {
                MainScreenContent(currentMainScreen, navController)
            }
        }
    }
}


// Placeholder for the screen content
@Composable
fun MainScreenContent(currentScreen: MainScreens, navController: NavHostController, modifier: Modifier = Modifier) {
    // Obtain an instance of AuthenticationViewModel
    val mainViewModel: MainViewModel = viewModel()

    when (currentScreen) {
        MainScreens.Home -> HomePage(navController)
        MainScreens.Dashboard -> DashboardPage(mainViewModel)
        MainScreens.Useful -> UsefulPage(navController) {}
        MainScreens.Profile -> ProfilePage(navController)// Pass the ViewModel here
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
