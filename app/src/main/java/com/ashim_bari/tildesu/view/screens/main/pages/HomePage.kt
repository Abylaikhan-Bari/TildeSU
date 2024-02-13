import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun HomePage(function: () -> Unit) {
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
                "A1 деңгейі" to Alignment.Start,
                "A2 деңгейі" to Alignment.End,
                "B1 деңгейі" to Alignment.Start,
                "B2 деңгейі" to Alignment.End,
                "C1 деңгейі" to Alignment.Start,
                "C2 деңгейі" to Alignment.End
            )

            buttons.forEachIndexed { index, (text, alignment) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = if (alignment == Alignment.Start) Arrangement.Start else Arrangement.End
                ) {
                    Button(
                        onClick = { /* Handle button click */ },
                        modifier = Modifier.width(200.dp),
                        colors = ButtonDefaults.buttonColors( // Optional: Customize button colors
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

@Preview(showBackground = true)
@Composable
fun HomePagePreview() {
    MaterialTheme {
        HomePage {}
    }
}
