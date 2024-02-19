import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.ashim_bari.tildesu.viewmodel.exercise.ExerciseViewModel
import com.ashim_bari.tildesu.viewmodel.exercise.ExerciseViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseScreen(navController: NavHostController, exerciseViewModelFactory: ExerciseViewModelFactory, level: String) {
    val exerciseViewModel: ExerciseViewModel = viewModel(factory = exerciseViewModelFactory)

    LaunchedEffect(key1 = level) {
        exerciseViewModel.loadExercisesForLevel(level)
    }

    val exercises = exerciseViewModel.exercises.observeAsState(initial = emptyList()).value
    val currentQuestionIndex = exerciseViewModel.currentQuestionIndex.observeAsState().value ?: 0
    var selectedOption by rememberSaveable { mutableIntStateOf(-1) } // Use rememberSaveable for the selected option
    val quizCompleted = exerciseViewModel.quizCompleted.observeAsState().value ?: false

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Level $level") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary))
        }
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState()) // Add vertical scroll modifier here
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Center // Center the content vertically
            ) {
                if (quizCompleted) {
                    Text("Quiz Completed!", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(bottom = 8.dp).align(Alignment.CenterHorizontally))
                    Button(onClick = { navController.navigate("main") },
                        modifier = Modifier.padding(top = 16.dp).align(Alignment.CenterHorizontally),) {
                        Text("Go to Home Page")
                    }
                    Text("Your score: ${exerciseViewModel.score.observeAsState().value}", modifier = Modifier.align(
                        Alignment.CenterHorizontally))
                } else if (exercises != null && exercises.isNotEmpty() && currentQuestionIndex < exercises.size) {
                    val currentExercise = exercises[currentQuestionIndex]
                    Text(text = currentExercise.question, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(bottom = 8.dp))
                    exercises[currentQuestionIndex].options.forEachIndexed { index, option ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                                .clickable { selectedOption = index }, // Update selectedOption directly
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (selectedOption == index) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Row(modifier = Modifier.padding(16.dp)) {
                                Text(text = option, style = MaterialTheme.typography.bodyLarge)
                            }
                        }
                    }
                    Button(
                        onClick = {
                            exerciseViewModel.submitAnswer(selectedOption)
                            exerciseViewModel.moveToNextQuestion()
                            selectedOption = -1 // Reset for next question
                        },
                        modifier = Modifier.padding(top = 16.dp).align(Alignment.CenterHorizontally), // Align the button to the center horizontally
                        enabled = selectedOption != -1
                    ) {
                        Text("Next")
                    }
                } else {
                    Text("No exercises found for $level", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}
