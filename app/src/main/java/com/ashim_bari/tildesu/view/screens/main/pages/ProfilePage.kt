import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun ProfilePage(navController: NavHostController, content: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(16.dp)
        ) {
            // Profile picture
            ProfilePicture()

            Spacer(modifier = Modifier.height(16.dp))

            // Profile attributes

            ProfileAttribute("john.doe@example.com")

            Spacer(modifier = Modifier.height(24.dp))

            // Edit profile button
            OutlinedButton(
                onClick = { /* Handle edit profile */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Filled.Edit, contentDescription = "Edit Profile")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Edit Profile")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Log out button
            Button(
                onClick = { /* Handle log out */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Log Out")
            }
        }
    }
}

@Composable
fun ProfilePicture() {
    Card(
        modifier = Modifier
            .size(120.dp)
            .clip(CircleShape),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        // Placeholder for profile picture
    }
}

@Composable
fun ProfileAttribute(value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
