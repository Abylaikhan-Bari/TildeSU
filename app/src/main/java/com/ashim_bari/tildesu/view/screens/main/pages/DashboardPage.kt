import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ashim_bari.tildesu.viewmodel.main.MainViewModel
import androidx.compose.runtime.livedata.observeAsState

@Composable
fun DashboardPage(mainViewModel: MainViewModel) {
    val progressData by mainViewModel.progressData.observeAsState(mapOf())

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (progressData.isNotEmpty()) {
                progressData.forEach { (level, progress) ->
                    LanguageLevelProgressBar(level, progress)
                    Spacer(modifier = Modifier.height(16.dp))
                }
            } else {
                Text("No progress data available. Start learning to see your progress!",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(16.dp))
            }
        }
    }

    LaunchedEffect(key1 = Unit) {
        mainViewModel.loadUserProgress()
    }
}







@Composable
fun LanguageLevelProgressBar(level: String, progressPair: Pair<Float, Int>) {
    val (progress, _) = progressPair // Extract progress value from the pair

    Column {
        Text(
            text = level,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        LinearProgressIndicator(
            progress = {
                progress // Use the extracted progress value
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.24f),
        )
    }
}





