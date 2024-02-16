package com.ashim_bari.tildesu.view.screens.exercise

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun B2_Level(navController: NavHostController) {
    // This should be the actual quiz state management logic.
    // For demonstration, using a simple counter.
    var currentQuestion by rememberSaveable { mutableStateOf(1) }
    val totalQuestions = 15
    val isLastQuestion = currentQuestion == totalQuestions

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Course (Quiz ${currentQuestion})", fontSize = 18.sp)
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary),
                navigationIcon = {
                    IconButton(onClick = { /* Handle back action */ }) {
//                        Icon(
//                            imageVector = Icons.Filled.Close,
//                            contentDescription = "Close"
//                        )
                    }
                },
                actions = {
                    // Place actions if needed
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Quiz content goes here
            Text(
                text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit.",
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            // Dynamic question/answer UI based on the current question
            // For demonstration, placeholders are used here
            BasicText(text = "Question content...")

            Spacer(modifier = Modifier.height(16.dp))

            // Options
            Column {
                ('A'..'D').forEach { option ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        RadioButton(
                            selected = false,
                            onClick = { /* Handle option select */ }
                        )
                        Text(text = "$option. Lorem Ipsum", modifier = Modifier.padding(start = 8.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Progress indicator
            Text(
                text = "$currentQuestion of $totalQuestions",
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Continue/Finish button
            Button(
                onClick = {
                    if (!isLastQuestion) {
                        currentQuestion++
                    } else {
                        // Handle finish action
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text(text = if (!isLastQuestion) "Continue" else "Finish")
            }
        }
    }
}
