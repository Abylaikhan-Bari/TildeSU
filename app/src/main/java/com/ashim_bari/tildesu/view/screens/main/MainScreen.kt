import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.automirrored.filled.List
import com.ashim_bari.tildesu.view.screens.main.pages.DashboardPage
import com.ashim_bari.tildesu.view.screens.main.pages.HomePage
import com.ashim_bari.tildesu.view.screens.main.pages.ProfilePage
import com.ashim_bari.tildesu.view.screens.main.pages.UsefulPage
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Surface

@Composable
fun MainScreen(navController: NavHostController) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val padding = if (isLandscape) 32.dp else 16.dp
    val scrollState = rememberScrollState()

    var currentMainScreen by rememberSaveable { mutableStateOf(MainScreens.Home) }

    // Define Bottom Navigation Items
    val bottomItems = listOf(
        BottomNavItem("Home", Icons.Filled.Home, MainScreens.Home),
        BottomNavItem("Dashboard", Icons.AutoMirrored.Filled.List, MainScreens.Dashboard),
        BottomNavItem("Useful", Icons.Filled.Star, MainScreens.Useful),
        BottomNavItem("Profile", Icons.Filled.Person, MainScreens.Profile)
    )

    Scaffold(
        topBar = { /* TopAppBar, if needed */ },
        bottomBar = {
            BottomAppBar {
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
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(scrollState)
                    .padding(padding)
            ) {
                when (currentMainScreen) {
                    MainScreens.Home -> HomePage(navController) { /* Define what to do here */ }
                    MainScreens.Dashboard -> DashboardPage(navController) { /* Define what to do here */ }
                    MainScreens.Useful -> UsefulPage(navController) { /* Define what to do here */ }
                    MainScreens.Profile -> ProfilePage(navController) { /* Define what to do here */ }
                }
            }
        }
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
