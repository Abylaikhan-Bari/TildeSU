package com.ashim_bari.tildesu.view.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SentimentDissatisfied
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ashim_bari.tildesu.R

@Composable
fun FailureScreen(navController: NavController, restartExercise: () -> Unit = {}) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(id = R.string.oops_sorry),
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 8.dp).align(Alignment.CenterHorizontally)
        )
        Icon(
            imageVector = Icons.Filled.SentimentDissatisfied,
            contentDescription = "Sad face",
            modifier = Modifier
                .size(100.dp)
                .padding(bottom = 16.dp)
        )

        Text(
            text = stringResource(id = R.string.dont_worry_try_again),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 24.dp).align(Alignment.CenterHorizontally)
        )
        Card(
            onClick = restartExercise,
            modifier = Modifier
                .padding(top = 16.dp)
                .align(Alignment.CenterHorizontally)
                .width(200.dp) // Set the width to a specific value or use Modifier.fillMaxWidth() for full width
                .height(100.dp), // Set the height to a specific value
            shape = RoundedCornerShape(16.dp), // Use a larger value for more rounded corners
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text(
                stringResource(id = R.string.try_again), style = MaterialTheme.typography.labelLarge, modifier = Modifier.padding(16.dp).align(
                    Alignment.CenterHorizontally))
        }


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
            Text(
                stringResource(id = R.string.go_home_card), style = MaterialTheme.typography.labelLarge, modifier = Modifier.padding(16.dp).align(
                    Alignment.CenterHorizontally))
        }

    }
}


@Composable
fun TrueFalseFailureScreen(navController: NavController, restartTrueFalseExercise: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(id = R.string.oops_sorry),
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 8.dp).align(Alignment.CenterHorizontally)
        )
        Icon(
            imageVector = Icons.Filled.SentimentDissatisfied,
            contentDescription = "Sad face",
            modifier = Modifier
                .size(100.dp)
                .padding(bottom = 16.dp)
        )

//        Text(
//            text = stringResource(id = R.string.dont_worry_try_again),
//            style = MaterialTheme.typography.bodyMedium,
//            modifier = Modifier.padding(bottom = 24.dp).align(Alignment.CenterHorizontally)
//        )
//        Card(
//            onClick = restartTrueFalseExercise, // Use restartTrueFalseExercise instead of restartExercise
//            modifier = Modifier
//                .padding(top = 16.dp)
//                .align(Alignment.CenterHorizontally)
//                .width(200.dp)
//                .height(100.dp),
//            shape = RoundedCornerShape(16.dp),
//            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
//            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
//        ) {
//            Text(
//                stringResource(id = R.string.try_again),
//                style = MaterialTheme.typography.labelLarge,
//                modifier = Modifier.padding(16.dp).align(Alignment.CenterHorizontally)
//            )
//        }

        Card(
            onClick = { navController.navigate("main") },
            modifier = Modifier
                .padding(top = 16.dp)
                .align(Alignment.CenterHorizontally)
                .width(200.dp)
                .height(100.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text(
                stringResource(id = R.string.go_home_card),
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(16.dp).align(Alignment.CenterHorizontally)
            )
        }
    }
}
