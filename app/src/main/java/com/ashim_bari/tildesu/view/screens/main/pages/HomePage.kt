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
            val buttons = listOf(
                "A1 Level" to "exercise/A1",
                "A2 Level" to "exercise/A2",
                "B1 Level" to "exercise/B1",
                "B2 Level" to "exercise/B2",
                "C1 Level" to "exercise/C1",
                "C2 Level" to "exercise/C2"
            )

            buttons.forEach { (text, route) ->
                Button(
                    onClick = { navController.navigate(route) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text(text, style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }
}




