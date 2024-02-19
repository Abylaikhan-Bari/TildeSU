import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.ashim_bari.tildesu.view.navigation.Navigation

@Composable
fun HomePage(navController: NavHostController) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        // Use a grid layout for chessboard-like arrangement
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(16.dp)
        ) {
            val levels = listOf(
                listOf("A1 Level", "A2 Level"),
                listOf("B1 Level", "B2 Level"),
                listOf("C1 Level", "C2 Level")
            )
            val routes = listOf(
                listOf("exercise/A1", "exercise/A2"),
                listOf("exercise/B1", "exercise/B2"),
                listOf("exercise/C1", "exercise/C2")
            )

            levels.zip(routes).forEachIndexed { rowIndex, (levelRow, routeRow) ->
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    levelRow.zip(routeRow).forEachIndexed { colIndex, (level, route) ->
                        // Determine card color based on position for chessboard effect
                        val cardColor = if ((rowIndex + colIndex) % 2 == 0) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.onSurfaceVariant

                        Card(
                            onClick = { navController.navigate(route) },
                            modifier = Modifier
                                .padding(8.dp)
                                .weight(1f) // Makes the cards share the row equally
                                .aspectRatio(1f), // Makes the card square
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = cardColor)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(level, style = MaterialTheme.typography.bodyLarge, color = if (cardColor == MaterialTheme.colorScheme.surfaceVariant) Color.Black else Color.White)
                            }
                        }
                    }
                }
            }
        }
    }
}


