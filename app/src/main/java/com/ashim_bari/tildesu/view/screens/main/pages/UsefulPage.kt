
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.ashim_bari.tildesu.R

@Composable
fun UsefulPage(navController: NavHostController, function: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background) // Use theme's background color
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val grammarTips = listOf(
            stringResource(id = R.string.grammar_tip_1),
            stringResource(id = R.string.grammar_tip_2),
            stringResource(id = R.string.grammar_tip_3),
            stringResource(id = R.string.grammar_tip_4),
            stringResource(id = R.string.grammar_tip_5),
            stringResource(id = R.string.grammar_tip_6),
            stringResource(id = R.string.grammar_tip_7),
            stringResource(id = R.string.grammar_tip_8),
            stringResource(id = R.string.grammar_tip_9),
            stringResource(id = R.string.grammar_tip_10)
            // Add more stringResource calls for other grammar tips
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
            .clickable { expanded = !expanded },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = tip,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = if (expanded) 8.dp else 0.dp),
                maxLines = if (expanded) Int.MAX_VALUE else 3, // Show all lines if expanded, else limit to 3
                fontWeight = if (expanded) FontWeight.Normal else FontWeight.Bold
            )
            AnimatedVisibility(
                visible = expanded,
                enter = fadeIn(animationSpec = tween(durationMillis = 300)) + expandVertically(animationSpec = tween(durationMillis = 300)),
                exit = fadeOut(animationSpec = tween(durationMillis = 300)) + shrinkVertically(animationSpec = tween(durationMillis = 300))
            ) {
                Column {
                    // Additional content for expanded state
                    Text(
                        text = stringResource(id = R.string.read_less),
                        modifier = Modifier.align(Alignment.End),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            if (!expanded) {
                Text(
                    text = stringResource(id = R.string.read_more),
                    modifier = Modifier.align(Alignment.End),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
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