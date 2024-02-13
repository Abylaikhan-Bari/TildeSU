import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun DashboardPage(function: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {


        // Define language levels and their progress
        val languageLevels = listOf(
            "A1 деңгейі" to 0.1f, // Sample progress values
            "A2 деңгейі" to 0.3f,
            "B1 деңгейі" to 0.5f,
            "B2 деңгейі" to 0.7f,
            "C1 деңгейі" to 0.9f,
            "C2 деңгейі" to 1.0f
        )

        languageLevels.forEach { (level, progress) ->
            LanguageLevelProgressBar(level, progress)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun LanguageLevelProgressBar(level: String, progress: Float) {
    Column {
        Text(
            text = level,
            color = Color.White,
            style = MaterialTheme.typography.bodyLarge
        )
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .padding(vertical = 8.dp),
            color = MaterialTheme.colorScheme.primary,
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardPagePreview() {
    // For preview purposes, providing a dummy NavController and function.
    DashboardPage {}
}
