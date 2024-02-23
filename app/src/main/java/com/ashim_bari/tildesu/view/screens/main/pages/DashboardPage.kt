import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ashim_bari.tildesu.viewmodel.main.MainViewModel
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.draw.clip

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
    val (targetProgress, _) = progressPair

    // Animate the progress value
    val animatedProgress by animateFloatAsState(
        targetValue = targetProgress,
        animationSpec = tween(
            durationMillis = 1000, // Duration of the animation in milliseconds
            delayMillis = 500 // Start delay in milliseconds
        ), label = ""
    )

    // Entry animation for the progress bar container
    AnimatedVisibility(
        visible = true, // You can control visibility with a state if needed
        enter = fadeIn(animationSpec = tween(1000)) + expandVertically(animationSpec = tween(1000)),
        exit = fadeOut(animationSpec = tween(1000)),
    ) {
        Column {
            Text(
                text = level,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Box(
                modifier = Modifier
                    .height(56.dp) // Adjust the height to match your design
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp)) // Apply rounded corners, adjust corner size as necessary
            ) {
                LinearProgressIndicator(
                    progress = animatedProgress,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp) // Match the height of the Box to fill it completely
                        .clip(RoundedCornerShape(12.dp)), // Apply the same rounded corners to the progress indicator
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.24f),
                )
            }
        }
    }
}






