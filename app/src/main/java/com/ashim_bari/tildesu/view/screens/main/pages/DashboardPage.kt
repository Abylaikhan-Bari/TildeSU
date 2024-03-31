import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ashim_bari.tildesu.R
import com.ashim_bari.tildesu.viewmodel.main.MainViewModel

@Composable
fun DashboardPage() {
    val mainViewModel: MainViewModel = hiltViewModel()
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
                        trueFalseProgress = userProgress.trueFalseProgress,
                        imageQuizProgress = userProgress.imageQuizProgress
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
    trueFalseProgress: Float,
    imageQuizProgress: Float
) {
    var isExpanded by rememberSaveable { mutableStateOf(false) }
    LanguageLevelProgressBar(
        level = level,
        progress = overallProgress,
        onBarClick = { isExpanded = !isExpanded })
    AnimatedVisibility(
        visible = isExpanded,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut()
    ) {
        Column(modifier = Modifier.padding(start = 16.dp)) {
            // Your sub-progress bars here, unchanged
            if (puzzleProgress > 0) {
                Text(
                    text = stringResource(id = R.string.puzzle),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                )
                ProgressBar(progress = puzzleProgress)
            }
            if (quizProgress > 0) {
                Text(
                    text = stringResource(id = R.string.quiz),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                )
                ProgressBar(progress = quizProgress)
            }
            if (trueFalseProgress > 0) {
                Text(
                    text = stringResource(id = R.string.true_false),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                )
                ProgressBar(progress = trueFalseProgress)
            }
            if (imageQuizProgress > 0) {
                Text(
                    text = stringResource(id = R.string.image_quiz),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                )
                ProgressBar(progress = imageQuizProgress)
            }
        }
    }
}

@Composable
fun ProgressBar(progress: Float) {
    // Using SafeLinearProgressIndicator instead of LinearProgressIndicator directly
    LinearProgressIndicator(
        progress = progress,
        modifier = Modifier
            .fillMaxWidth()
            .height(25.dp)
            .clip(RoundedCornerShape(4.dp)),
        color = MaterialTheme.colorScheme.primary,
        trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.24f)
    )
}

@Composable
fun LanguageLevelProgressBar(level: String, progress: Float, onBarClick: () -> Unit) {
    val animatedProgress by animateFloatAsState(
        targetValue = if (progress.isNaN()) 0.0f else progress.coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 1000, delayMillis = 500), label = ""
    )
    Column {
        Text(
            text = level,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Box(
            modifier = Modifier
                .height(50.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .clickable(onClick = onBarClick) // This makes the entire Box clickable
        ) {
            LinearProgressIndicator(
                progress = animatedProgress,
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.24f)
            )
        }
    }
}