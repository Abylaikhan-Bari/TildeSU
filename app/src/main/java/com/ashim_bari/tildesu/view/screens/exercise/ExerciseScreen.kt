import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import com.ashim_bari.tildesu.viewmodel.exercise.ExerciseViewModel
import com.ashim_bari.tildesu.viewmodel.exercise.ExerciseViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseScreen(
    navController: NavController,
    exerciseViewModelFactory: ExerciseViewModelFactory,
    level: String,
    navBackStackEntry: NavBackStackEntry? = null // Pass NavBackStackEntry as a parameter
) {
    val exerciseViewModel: ExerciseViewModel = viewModel(factory = exerciseViewModelFactory)

    // Remember the level when the screen is created
    val currentLevel by rememberSaveable { mutableStateOf(level) }

    // State variable to track if the confirmation dialog is shown
    var showDialog by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(key1 = level) {
        exerciseViewModel.loadExercisesForLevel(level)
    }

    // Use rememberSaveable for state variables
    val exercises = exerciseViewModel.exercises.observeAsState(initial = emptyList()).value
    val currentQuestionIndex = exerciseViewModel.currentQuestionIndex.observeAsState().value ?: 0
    var selectedOption by rememberSaveable { mutableStateOf(-1) } // Use rememberSaveable for the selected option
    val quizCompleted = exerciseViewModel.quizCompleted.observeAsState().value ?: false

    // Function to show the confirmation dialog
    fun showConfirmationDialog() {
        showDialog = true
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Exercise - Level $currentLevel") }, // Display level information in the app bar title
                navigationIcon = {
                    if (!quizCompleted) {
                        IconButton(
                            onClick = { showConfirmationDialog() } // Show confirmation dialog when back button is clicked
                        ) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = "Go Back") // Icon for going back
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
            )
        }
    ) { paddingValues ->
        BackHandler {
            // Do nothing to prevent navigation when any screen is opened
        }
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                //.verticalScroll(rememberScrollState()) // Add vertical scroll modifier here
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Center // Center the content vertically
            ) {
                if (quizCompleted) {
                    Text("Quiz Completed!", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(bottom = 8.dp).align(Alignment.CenterHorizontally))
                    Card(
                        onClick = { navController.navigate("main") },
                        modifier = Modifier
                            .padding(top = 16.dp)
                            .align(Alignment.CenterHorizontally)
                            .width(200.dp) // Set the width to a specific value or use Modifier.fillMaxWidth() for full width
                            .height(100.dp), // Set the height to a specific value
                        shape = RoundedCornerShape(16.dp), // Use a larger value for more rounded corners
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("Go to Home Page", style = MaterialTheme.typography.labelLarge, modifier = Modifier.padding(16.dp).align(Alignment.CenterHorizontally))
                    }
                    Text("Your score: ${exerciseViewModel.score.observeAsState().value}", modifier = Modifier.align(
                        Alignment.CenterHorizontally))
                } else if (exercises != null && exercises.isNotEmpty() && currentQuestionIndex < exercises.size) {
                    val currentExercise = exercises[currentQuestionIndex]
                    Text(text = currentExercise.question, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(bottom = 8.dp))

                    // Use a LazyVerticalGrid to create a grid layout for the option cards
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2), // We want 2 columns
                        contentPadding = PaddingValues(8.dp),
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(currentExercise.options.size) { index ->
                            OptionCard(
                                option = currentExercise.options[index],
                                isSelected = selectedOption == index,
                                modifier = Modifier.padding(8.dp)
                            ) {
                                selectedOption = index
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
                    Text("No exercises found for $currentLevel", style = MaterialTheme.typography.bodyMedium) // Use currentLevel here
                }
            }
        }
    }

    // Confirmation Dialog
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Confirmation") },
            text = { Text("Are you sure you want to go back to the main screen?") },
            confirmButton = {
                Button(
                    onClick = {
                        showDialog = false
                        navController.navigate("main")
                    }
                ) {
                    Text("Yes")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showDialog = false }
                ) {
                    Text("No")
                }
            }
        )
    }
}
@Composable
fun OptionCard(option: String, isSelected: Boolean, modifier: Modifier = Modifier, onSelect: () -> Unit) {
    Card(
        modifier = modifier
            .padding(vertical = 4.dp)
            .clickable(onClick = onSelect)
            .aspectRatio(1f), // Optional: Makes the card square-shaped
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Text(
            text = option,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(16.dp)
        )
    }
}
