package com.ashim_bari.tildesu.view.screens.main.pages

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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.ashim_bari.tildesu.R
import com.ashim_bari.tildesu.model.usefultips.UsefulTipsViewModel

@Composable
fun UsefulPage(navController: NavHostController, viewModel: UsefulTipsViewModel = hiltViewModel()) {
    val usefulTips by viewModel.usefulTips.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (usefulTips.isEmpty()) {
            Text(
                text = stringResource(R.string.no_tips_available),
                style = MaterialTheme.typography.bodyLarge
            )
        } else {
            usefulTips.forEach { tip ->
                GrammarTipCard(tip.title, tip.content)
            }
        }
    }
}

@Composable
fun GrammarTipCard(title: String, content: String) {
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
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = if (expanded) 8.dp else 0.dp),
                maxLines = if (expanded) Int.MAX_VALUE else 1,
                fontWeight = if (expanded) FontWeight.Bold else FontWeight.Normal
            )
            AnimatedVisibility(
                visible = expanded,
                enter = fadeIn(animationSpec = tween(durationMillis = 300)) + expandVertically(
                    animationSpec = tween(durationMillis = 300)
                ),
                exit = fadeOut(animationSpec = tween(durationMillis = 300)) + shrinkVertically(
                    animationSpec = tween(durationMillis = 300)
                )
            ) {
                Column {
                    Text(
                        text = content,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                    Text(
                        text = stringResource(id = R.string.read_less),
                        modifier = Modifier.align(Alignment.End).padding(top = 8.dp),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            if (!expanded) {
                Text(
                    text = stringResource(id = R.string.read_more),
                    modifier = Modifier.align(Alignment.End).padding(top = 8.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
