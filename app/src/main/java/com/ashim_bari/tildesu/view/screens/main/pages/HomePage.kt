import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun HomePage(function: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Define button texts and alignments
        val buttons = listOf(
            Pair("A1 деңгейі", Alignment.Start),
            Pair("A2 деңгейі", Alignment.End),
            Pair("B1 деңгейі", Alignment.Start),
            Pair("B2 деңгейі", Alignment.End),
            Pair("C1 деңгейі", Alignment.Start),
            Pair("C2 деңгейі", Alignment.End)
        )

        buttons.forEach { (text, alignment) ->
            Box(

                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp)
            ) {
                Button(
                    onClick = { /* Handle button click */ },
                    modifier = Modifier
                        .width(200.dp)
                        .height(50.dp),
                ) {
                    Text(text = text, fontSize = MaterialTheme.typography.titleMedium.fontSize)
                }
            }
        }
    }
}

@Preview
@Composable
fun HomePagePreview() {
    // Assume navController is not needed for preview
    HomePage {}
}
