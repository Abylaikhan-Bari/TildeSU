import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.ashim_bari.tildesu.view.navigation.Navigation

@Composable
fun HomePage(navController: NavHostController) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(16.dp)
        ) {
            // Define button texts and their intended alignments
            val buttons = listOf(
                "A1 деңгейі" to "a1_level",
                "A2 деңгейі" to "a2_level",
                "B1 деңгейі" to "b1_level",
                "B2 деңгейі" to "b2_level",
                "C1 деңгейі" to "c1_level",
                "C2 деңгейі" to "c2_level"
            )

            buttons.forEachIndexed { index, (text, route) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = if (index % 2 == 0) Arrangement.Start else Arrangement.End
                ) {
                    Button(
                        onClick = { navController.navigate(route) },
                        modifier = Modifier.width(200.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Text(
                            text = text,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
    }
}



