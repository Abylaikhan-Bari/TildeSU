import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@Composable
fun UsefulPage(navController: NavHostController, function: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background) // Use theme's background color
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Useful Grammar Tips",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground, // Use theme's onBackground color for text
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Example of grammar tips
        val grammarTips = listOf(
            "Use the present simple tense to talk about permanent states or regular actions.",
            "Use the present simple tense to talk about permanent states or regular actions.",
            "Use the present simple tense to talk about permanent states or regular actions.",
            "Use the present simple tense to talk about permanent states or regular actions.",
            "Use the present continuous tense to talk about actions happening now or around the current time.",
            "Use the present continuous tense to talk about actions happening now or around the current time.",
            "Use the present continuous tense to talk about actions happening now or around the current time.",
            "Use the present continuous tense to talk about actions happening now or around the current time.",
            // Add more grammar tips here
        )

        grammarTips.forEach { tip ->
            GrammarTipCard(tip)
        }
    }
}

@Composable
fun GrammarTipCard(tip: String) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .animateContentSize() // This will animate the size change
            .clickable { expanded = !expanded },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = tip,
                modifier = Modifier.padding(bottom = 8.dp),
                maxLines = if (expanded) Int.MAX_VALUE else 2 // Show all lines if expanded, else show a preview
            )
            AnimatedVisibility(visible = expanded) {
                // Additional content that is shown when the card is expanded
                Text(text = "More details about the grammar tip...")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun UsefulPagePreview() {
    MaterialTheme {
        UsefulPage(NavHostController(context = LocalContext.current)) {}
    }
}
