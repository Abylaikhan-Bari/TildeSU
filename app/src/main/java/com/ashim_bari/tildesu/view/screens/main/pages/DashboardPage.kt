import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun DashboardPage(function: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background // Use Material Theme background color
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Define language levels and their progress
            val languageLevels = listOf(
                "A1 деңгейі" to 0.1f,
                "A2 деңгейі" to 0.3f,
                "B1 деңгейі" to 0.5f,
                "B2 деңгейі" to 0.7f,
                "C1 деңгейі" to 0.9f,
                "C2 деңгейі" to 1.0f
            )

            languageLevels.forEach { (level, progress) ->
                LanguageLevelProgressBar(level, progress)
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun LanguageLevelProgressBar(level: String, progress: Float) {
    Column {
        Text(
            text = level,
            color = MaterialTheme.colorScheme.onSurface, // Ensure text is visible on the surface color
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        LinearProgressIndicator(
            progress = progress,
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp),
            color = MaterialTheme.colorScheme.primary, // Use primary color for the progress indicator
            trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.24f) // Lighter track color
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardPagePreview() {
    MaterialTheme {
        DashboardPage {}
    }
}
