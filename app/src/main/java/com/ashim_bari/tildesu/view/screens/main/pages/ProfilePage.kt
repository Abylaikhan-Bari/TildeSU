import androidx.compose.foundation.background
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun ProfilePage(navController: NavHostController, function: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Placeholder for profile picture
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(Color.LightGray), // Placeholder for an image
            contentAlignment = Alignment.Center
        ) {
            // In a real app, replace this Text with an Image composable
            Text("P", color = Color.Black, style = MaterialTheme.typography.headlineMedium)
        }

        Spacer(Modifier.height(16.dp))

        // User name
        Text(
            text = "User Name",
            color = Color.Black,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(8.dp))

        // User email or another identifier
        Text(
            text = "user@example.com",
            color = Color.Black,
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(Modifier.height(24.dp))

        // Settings or options
        Button(
            onClick = { /* Handle edit profile */ },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Filled.Edit, contentDescription = "Edit Profile")
            Spacer(Modifier.width(8.dp))
            Text("Edit Profile")
        }

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = { /* Handle log out */ },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Log Out")
        }

        // Add more profile related options as needed
    }
}
