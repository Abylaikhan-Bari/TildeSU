package com.ashim_bari.tildesu.view.screens.main.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.ashim_bari.tildesu.viewmodel.translation.TranslationViewModel

@Composable
fun TranslatePage(navController: NavHostController, viewModel: TranslationViewModel = hiltViewModel()) {
    var sourceText by remember { mutableStateOf("") }
    val translationResult by viewModel.translationResult.collectAsState(initial = "")
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    // Language options
    val languagesMap = mapOf(
        "Kazakh" to "kk_KZ",
        "English" to "en_US",
        "Russian" to "ru_RU"
    )
    val languageOptions = languagesMap.keys.toList()

    var sourceLanguage by remember { mutableStateOf(languageOptions.first()) }
    var targetLanguage by remember { mutableStateOf(languageOptions[1]) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Translate",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Language selection
        Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.SpaceBetween) {
            DropdownMenu(
                options = languageOptions,
                selectedOption = sourceLanguage,
                onOptionSelected = { sourceLanguage = it },
                label = "Source Language"
            )
            DropdownMenu(
                options = languageOptions.filter { it != sourceLanguage },
                selectedOption = targetLanguage,
                onOptionSelected = { targetLanguage = it },
                label = "Target Language"
            )
        }

        OutlinedTextField(
            value = sourceText,
            onValueChange = { sourceText = it },
            label = { Text("Enter text") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            singleLine = false,
            maxLines = 5
        )

        Button(
            onClick = { viewModel.translateText(sourceText, languagesMap[sourceLanguage]!!, languagesMap[targetLanguage]!!) },
            modifier = Modifier
                .padding(top = 8.dp)
                .align(Alignment.CenterHorizontally),
            enabled = !isLoading
        ) {
            Text("Translate")
        }

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.padding(top = 16.dp))
        }

        // Display translation result or a pending message
        if (translationResult?.isNotEmpty() == true) {
            Text(
                text = translationResult ?: "No translation available", // Provide a default value
                modifier = Modifier
                    .padding(top = 16.dp)
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(16.dp),
                style = MaterialTheme.typography.bodyLarge
            )
        }

        else if (!isLoading && sourceText.isNotEmpty()) {
            // Optionally show a message when there's no result but the source text is not empty
            Text(
                text = "No translation found.",
                modifier = Modifier
                    .padding(top = 16.dp)
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(16.dp),
                style = MaterialTheme.typography.bodyLarge
            )
        }

        if (errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}


@Composable
fun DropdownMenu(
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit,
    label: String
) {
    var expanded by remember { mutableStateOf(false) }
    Box(modifier = Modifier.padding(8.dp)) {
        OutlinedTextField(
            value = selectedOption,
            onValueChange = { },
            label = { Text(label) },
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(Icons.Default.ArrowDropDown, "dropdown")
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}