
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ashim_bari.tildesu.R
import com.ashim_bari.tildesu.viewmodel.main.MainViewModel

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
                progressData.forEach { (level, userProgress) ->
                    ExpandableProgressBar(
                        level = level,
                        overallProgress = userProgress.overallProgress,
                        puzzleProgress = userProgress.puzzleProgress,
                        quizProgress = userProgress.quizProgress,
                        trueFalseProgress = userProgress.trueFalseProgress
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            } else {
                Text(
                    stringResource(R.string.no_progress_data),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }

    LaunchedEffect(key1 = Unit) {
        mainViewModel.loadUserProgress()
    }
}

@Composable
fun ExpandableProgressBar(
    level: String,
    overallProgress: Float,
    puzzleProgress: Float,
    quizProgress: Float,
    trueFalseProgress: Float
){
    var isExpanded by remember { mutableStateOf(false) }

    Column {
        LanguageLevelProgressBar(level, overallProgress)

        IconButton(onClick = { isExpanded = !isExpanded }) {
            Icon(
                imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = if (isExpanded) "Collapse" else "Expand"
            )
        }
        AnimatedVisibility(visible = isExpanded, enter = expandVertically(), exit = fadeOut()) {
            Column(modifier = Modifier.padding(start = 16.dp)) {
                Text(text = stringResource(R.string.puzzle), // Replace with actual string resource or hardcoded string
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(top = 8.dp, bottom = 4.dp))
                ProgressBar(progress = puzzleProgress)

                Text(text = stringResource(R.string.quiz), // Replace with actual string resource or hardcoded string
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(top = 8.dp, bottom = 4.dp))
                ProgressBar(progress = quizProgress)

                Text(text = stringResource(R.string.true_false), // Replace with actual string resource or hardcoded string
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(top = 8.dp, bottom = 4.dp))
                ProgressBar(progress = trueFalseProgress)
            }
        }
    }
}


@Composable
fun ProgressBar(progress: Float) {
    LinearProgressIndicator(
        progress = { progress },
        modifier = Modifier
            .fillMaxWidth()
            .height(8.dp)
            .clip(RoundedCornerShape(4.dp)),
        color = MaterialTheme.colorScheme.primary,
        trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.24f),
    )
}



@Composable
fun LanguageLevelProgressBar(level: String, progress: Float) {
    // Use the 'progress' directly as it's already a Float value

    // Animate the progress value
    val animatedProgress by animateFloatAsState(
        targetValue = progress, // Use 'progress' directly here
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
                    progress = { animatedProgress },
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







