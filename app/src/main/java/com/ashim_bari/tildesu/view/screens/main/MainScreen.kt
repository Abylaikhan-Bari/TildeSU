import androidx.activity.compose.BackHandler
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.TopAppBar
import androidx.compose.ui.Modifier
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.foundation.lazy.LazyColumn
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ashim_bari.tildesu.viewmodel.MainViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavHostController) {
    var currentMainScreen by rememberSaveable { mutableStateOf(MainScreens.Home) }

    // Define Bottom Navigation Items
    val bottomItems = listOf(
        BottomNavItem("Home", Icons.Filled.Home, MainScreens.Home),
        BottomNavItem("Dashboard", Icons.Filled.List, MainScreens.Dashboard),
        BottomNavItem("Useful", Icons.Filled.Star, MainScreens.Useful),
        BottomNavItem("Profile", Icons.Filled.Person, MainScreens.Profile)
    )

    Scaffold(
        topBar = {
            // Dynamic TopAppBar using Material3
            TopAppBar(
                title = { Text(text = bottomItems.first { it.screen == currentMainScreen }.title) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
            )
        },
        bottomBar = {
            // Bottom navigation using Material3
            NavigationBar {
                bottomItems.forEach { item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.title) },
                        label = { Text(item.title) },
                        selected = currentMainScreen == item.screen,
                        onClick = { currentMainScreen = item.screen }
                    )
                }
            }
        }
    ) { innerPadding ->
        BackHandler {
            // Do nothing to prevent navigation when any screen is opened
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
        MainScreens.Home -> HomePage(){}
        MainScreens.Dashboard -> DashboardPage(){}
        MainScreens.Useful -> UsefulPage(navController){}
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
