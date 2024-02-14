import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.ashim_bari.tildesu.viewmodel.AuthenticationViewModel

import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun ProfilePage(navController: NavHostController, content: () -> Unit) {
    // Get an instance of the ViewModel
    val viewModel: AuthenticationViewModel = viewModel()

    // Call the function to fetch user email
    LaunchedEffect(Unit) {
        viewModel.getUserEmail()
    }

    // Observe the user's email
    val userEmail by viewModel.userEmail.observeAsState()

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
            userEmail?.let { email ->
                ProfileAttribute(email)
            }

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
            text = value ?: "Loading...", // Show "Loading..." while email is being fetched
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
