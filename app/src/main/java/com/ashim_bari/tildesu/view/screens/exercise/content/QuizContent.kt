package com.ashim_bari.tildesu.view.screens.exercise.content

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ashim_bari.tildesu.model.exercise.ExerciseType
import com.ashim_bari.tildesu.viewmodel.exercise.ExerciseViewModel
import com.ashim_bari.tildesu.viewmodel.exercise.ExerciseViewModelFactory



@Composable
fun QuizContent(level: String, exerciseViewModelFactory: ExerciseViewModelFactory) {
    val exerciseViewModel: ExerciseViewModel = viewModel(factory = exerciseViewModelFactory)

    // Assuming your ViewModel has a function to load exercises for the given level and type
    LaunchedEffect(level) {
        exerciseViewModel.loadExercisesForLevelAndType(level, ExerciseType.QUIZ)
    }

    val exercises by exerciseViewModel.exercises.observeAsState(initial = emptyList())

    // Display logic for quizzes
    if (exercises.isEmpty()) {
        Text("Loading quizzes...")
    } else {
        // Example: Display the first quiz question and options
        // You'll likely want a more complex UI to allow navigating between questions
        val firstQuiz = exercises.first()
        firstQuiz.question?.let { Text(it) }
        Column {
            firstQuiz.options?.forEachIndexed { index, option ->
                Text("Option ${index + 1}: $option")
                // Add interaction logic, such as selecting an option
            }
        }
    }
}

//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun ExerciseScreen(
//    navController: NavController,
//    exerciseViewModelFactory: ExerciseViewModelFactory,
//    level: String,
//    navBackStackEntry: NavBackStackEntry? = null
//) {
//    val exerciseViewModel: ExerciseViewModel = viewModel(factory = exerciseViewModelFactory)
//
//    val currentLevel by rememberSaveable { mutableStateOf(level) }
//    var showDialog by rememberSaveable { mutableStateOf(false) }
//
//    LaunchedEffect(key1 = level) {
//        Log.d("ExerciseScreen", "LaunchedEffect triggered for level: $level")
//        exerciseViewModel.loadExercisesForLevel(level)
//    }
//
//    val exercises = exerciseViewModel.exercises.observeAsState(initial = emptyList()).value
//    val currentQuestionIndex = exerciseViewModel.currentQuestionIndex.observeAsState().value ?: 0
//    var selectedOption by rememberSaveable { mutableIntStateOf(-1) }
//    val quizCompleted = exerciseViewModel.quizCompleted.observeAsState().value ?: false
//    val quizPassed = exerciseViewModel.quizPassed.observeAsState()
//    fun showConfirmationDialog() {
//        showDialog = true
//    }
//
//    Log.d("ExerciseScreen", "Composing ExerciseScreen, Current Level: $currentLevel, Current Question Index: $currentQuestionIndex, Quiz Completed: $quizCompleted")
//    if (quizCompleted) {
//        quizPassed.value?.let { passed ->
//            if (passed) {
//                SuccessScreen(navController, exerciseViewModel.score.value ?: 0)
//                return@ExerciseScreen
//            } else {
//                FailureScreen(navController) {
//                    // Implement what should happen when retrying the quiz, e.g., resetting quiz state
//                    exerciseViewModel.resetQuiz()
//                    // Navigate as needed or reset UI state
//                }
//                return@ExerciseScreen
//            }
//        }
//    }
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = { Text(
//                    text = stringResource(
//                        R.string.exercise_level,
//                        currentLevel
//                    ))  },
//                navigationIcon = {
//                    if (!quizCompleted) {
//                        IconButton(
//                            onClick = { showConfirmationDialog() }
//                        ) {
//                            Icon(Icons.Filled.ArrowBack, contentDescription = "Go Back")
//                        }
//                    }
//                },
//                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
//            )
//        }
//    ) { paddingValues ->
//        BackHandler {
//            showConfirmationDialog()
//            Log.d("ExerciseScreen", "BackHandler triggered")
//        }
//        Surface(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(paddingValues)
//            //.verticalScroll(rememberScrollState()) // Add vertical scroll modifier here
//        ) {
//            Column(
//                modifier = Modifier
//                    .padding(16.dp)
//                    .fillMaxWidth(),
//                verticalArrangement = Arrangement.Center // Center the content vertically
//            ) {
//                // Log action: Checking quiz completion
//                Log.d("ExerciseScreen", "Checking quiz completion")
//
//                if (quizCompleted) {
//                    // Log action: Quiz completed
//                    Log.d("ExerciseScreen", "Quiz completed")
//
//                    Text(stringResource(id = R.string.exercise_completed), style = MaterialTheme.typography.bodyMedium, modifier = Modifier
//                        .padding(bottom = 8.dp)
//                        .align(Alignment.CenterHorizontally))
//                    Card(
//                        onClick = { navController.navigate("main") },
//                        modifier = Modifier
//                            .padding(top = 16.dp)
//                            .align(Alignment.CenterHorizontally)
//                            .width(200.dp) // Set the width to a specific value or use Modifier.fillMaxWidth() for full width
//                            .height(100.dp), // Set the height to a specific value
//                        shape = RoundedCornerShape(16.dp), // Use a larger value for more rounded corners
//                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
//                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
//                    ) {
//                        Text(stringResource(id = R.string.go_home_card), style = MaterialTheme.typography.labelLarge, modifier = Modifier
//                            .padding(16.dp)
//                            .align(Alignment.CenterHorizontally))
//                    }
//                    Text(
//                        text = stringResource(
//                            R.string.your_score,
//                            exerciseViewModel.score.observeAsState().value ?: 0
//                        ), modifier = Modifier.align(
//                        Alignment.CenterHorizontally))
//                } else if (exercises.isNotEmpty() && currentQuestionIndex < exercises.size) {
//                    val currentExercise = exercises[currentQuestionIndex]
//                    Text(text = currentExercise.question, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(bottom = 8.dp))
//
//                    // Use a LazyVerticalGrid to create a grid layout for the option cards
//                    LazyVerticalGrid(
//                        columns = GridCells.Fixed(2), // We want 2 columns
//                        contentPadding = PaddingValues(8.dp),
//                        modifier = Modifier.fillMaxWidth(),
//                        horizontalArrangement = Arrangement.spacedBy(8.dp),
//                        verticalArrangement = Arrangement.spacedBy(8.dp)
//                    ) {
//                        items(currentExercise.options.size) { index ->
//                            OptionCard(
//                                option = currentExercise.options[index],
//                                isSelected = selectedOption == index,
//                                modifier = Modifier.padding(8.dp)
//                            ) {
//                                selectedOption = index
//                            }
//                        }
//                    }
//
//
//                    Button(
//                        onClick = {
//                            exerciseViewModel.submitAnswer(selectedOption)
//                            exerciseViewModel.moveToNextQuestion()
//                            selectedOption = -1 // Reset for next question
//                        },
//                        modifier = Modifier
//                            .padding(top = 16.dp)
//                            .align(Alignment.CenterHorizontally), // Align the button to the center horizontally
//                        enabled = selectedOption != -1
//                    ) {
//                        Text(stringResource(id = R.string.next_button))
//                    }
//                } else {
//                    Text(
//                        text = stringResource(
//                            R.string.no_exercises_found,
//                            currentLevel
//                        ), style = MaterialTheme.typography.bodyMedium, modifier = Modifier.align(
//                        Alignment.CenterHorizontally)) // Use currentLevel here
//                }
//            }
//        }
//    }
//
//    // Confirmation Dialog
//    if (showDialog) {
//        AlertDialog(
//            onDismissRequest = { showDialog = false },
//            title = { Text(stringResource(id = R.string.exit_exercise_dialog_title)) },
//            text = { Text(stringResource(id = R.string.exit_exercise_dialog_content)) },
//            confirmButton = {
//                Button(
//                    onClick = {
//                        showDialog = false
//                        navController.navigate("main")
//                    }
//                ) {
//                    Text(stringResource(id = R.string.exit_dialog_yes))
//                }
//            },
//            dismissButton = {
//                Button(
//                    onClick = { showDialog = false }
//                ) {
//                    Text(stringResource(id = R.string.exit_dialog_no))
//                }
//            }
//        )
//    }
//}
//@Composable
//fun SuccessScreen(navController: NavController, score: Int) {
//    Column(
//        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.Center,
//        modifier = Modifier.fillMaxSize().padding(16.dp)
//    ) {
//        Text(
//            text = stringResource(id = R.string.congratulations),
//            style = MaterialTheme.typography.headlineLarge,
//            modifier = Modifier.padding(bottom = 8.dp).align(Alignment.CenterHorizontally)
//        )
//        Icon(
//            imageVector = Icons.Filled.EmojiEvents,
//            contentDescription = "Trophy",
//            modifier = Modifier.size(100.dp).padding(bottom = 16.dp)
//        )
//
//        Text(
//            text = stringResource(id = R.string.you_scored_points, score),
//            style = MaterialTheme.typography.bodyMedium,
//            modifier = Modifier.padding(bottom = 24.dp).align(Alignment.CenterHorizontally)
//        )
//
//        Card(
//            onClick = { navController.navigate("main") },
//            modifier = Modifier
//                .padding(top = 16.dp)
//                .align(Alignment.CenterHorizontally)
//                .width(200.dp) // Set the width to a specific value or use Modifier.fillMaxWidth() for full width
//                .height(100.dp), // Set the height to a specific value
//            shape = RoundedCornerShape(16.dp), // Use a larger value for more rounded corners
//            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
//            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
//        ) {
//            Text(stringResource(id = R.string.go_home_card), style = MaterialTheme.typography.labelLarge, modifier = Modifier.padding(16.dp).align(Alignment.CenterHorizontally))
//        }
//
//    }
//}
//
//
//@Composable
//fun FailureScreen(navController: NavController, restartQuiz: () -> Unit) {
//    Column(
//        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.Center,
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(16.dp)
//    ) {
//        Text(
//            text = stringResource(id = R.string.oops_sorry),
//            style = MaterialTheme.typography.headlineLarge,
//            modifier = Modifier.padding(bottom = 8.dp).align(Alignment.CenterHorizontally)
//        )
//        Icon(
//            imageVector = Icons.Filled.SentimentDissatisfied,
//            contentDescription = "Sad face",
//            modifier = Modifier
//                .size(100.dp)
//                .padding(bottom = 16.dp)
//        )
//
//        Text(
//            text = stringResource(id = R.string.dont_worry_try_again),
//            style = MaterialTheme.typography.bodyMedium,
//            modifier = Modifier.padding(bottom = 24.dp).align(Alignment.CenterHorizontally)
//        )
//        Card(
//            onClick = restartQuiz,
//            modifier = Modifier
//                .padding(top = 16.dp)
//                .align(Alignment.CenterHorizontally)
//                .width(200.dp) // Set the width to a specific value or use Modifier.fillMaxWidth() for full width
//                .height(100.dp), // Set the height to a specific value
//            shape = RoundedCornerShape(16.dp), // Use a larger value for more rounded corners
//            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
//            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
//        ) {
//            Text(stringResource(id = R.string.try_again), style = MaterialTheme.typography.labelLarge, modifier = Modifier.padding(16.dp).align(Alignment.CenterHorizontally))
//        }
//
//
//        Card(
//            onClick = { navController.navigate("main") },
//            modifier = Modifier
//                .padding(top = 16.dp)
//                .align(Alignment.CenterHorizontally)
//                .width(200.dp) // Set the width to a specific value or use Modifier.fillMaxWidth() for full width
//                .height(100.dp), // Set the height to a specific value
//            shape = RoundedCornerShape(16.dp), // Use a larger value for more rounded corners
//            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
//            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
//        ) {
//            Text(stringResource(id = R.string.go_home_card), style = MaterialTheme.typography.labelLarge, modifier = Modifier.padding(16.dp).align(Alignment.CenterHorizontally))
//        }
//
//    }
//}
//
//
//
//
//@Composable
//fun OptionCard(option: String, isSelected: Boolean, modifier: Modifier = Modifier, onSelect: () -> Unit) {
//    Card(
//        modifier = modifier
//            .padding(vertical = 4.dp)
//            .clickable(onClick = onSelect)
//            .aspectRatio(1f), // Optional: Makes the card square-shaped
//        shape = RoundedCornerShape(8.dp),
//        colors = CardDefaults.cardColors(
//            containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
//        )
//    ) {
//        Text(
//            text = option,
//            style = MaterialTheme.typography.bodyLarge,
//            modifier = Modifier
//                .padding(16.dp)
//                .align(Alignment.CenterHorizontally)
//        )
//    }
//}
