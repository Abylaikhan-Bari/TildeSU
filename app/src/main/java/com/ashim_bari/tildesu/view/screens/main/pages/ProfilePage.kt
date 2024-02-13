import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfilePage(navController: NavHostController, function: () -> Unit) {
    Scaffold(
        topBar = {
            // If you have a TopAppBar, it should be defined here
            TopAppBar(
                title = { Text("Profile") },
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding), // Apply the padding provided by the Scaffold
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Content goes here, same as before
                ProfileContent()
            }
        }
    }
}

@Composable
fun ProfileContent() {
    // Profile picture placeholder
    Card(
        modifier = Modifier
            .size(120.dp)
            .clip(CircleShape),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text("P", color = Color.Black, style = MaterialTheme.typography.headlineMedium)
        }
    }

    Spacer(Modifier.height(16.dp))

    // User name
    Text(
        text = "User Name",
        color = MaterialTheme.colorScheme.onBackground,
        style = MaterialTheme.typography.headlineMedium,
        fontWeight = FontWeight.Bold
    )

    Spacer(Modifier.height(8.dp))

    // User email or identifier
    Text(
        text = "user@example.com",
        color = MaterialTheme.colorScheme.onBackground,
        style = MaterialTheme.typography.bodyLarge
    )

    Spacer(Modifier.height(24.dp))

    // Edit profile button
    OutlinedButton(
        onClick = { /* Handle edit profile */ },
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(Icons.Filled.Edit, contentDescription = "Edit Profile")
        Spacer(Modifier.width(8.dp))
        Text("Edit Profile")
    }

    Spacer(Modifier.height(16.dp))

    // Log out button
    Button(
        onClick = { /* Handle log out */ },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Log Out")
    }
}

@Preview(showBackground = true)
@Composable
fun ProfilePagePreview() {
    MaterialTheme {
        ProfilePage(NavHostController(context = LocalContext.current)) {}
    }
}
