package com.ashim_bari.tildesu.viewmodel.translation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ashim_bari.tildesu.model.translation.TranslationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TranslationViewModel @Inject constructor(
    private val translationRepository: TranslationRepository
) : ViewModel() {
    // Backing property to avoid exposing a mutable flow
    private val _translationResult = MutableStateFlow<String?>(null)

    // The UI collects from this StateFlow to get updates
    val translationResult: StateFlow<String?> = _translationResult.asStateFlow()
    val isLoading = MutableStateFlow(false)
    val errorMessage = MutableStateFlow("")
    fun translateText(sourceText: String, sourceLang: String, targetLang: String) {
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = ""
            try {
                // This will call the repository to get the translation
                val result = translationRepository.translateText(sourceText, sourceLang, targetLang)
                // Unwrap the Result object and update _translationResult.value with the text only
                _translationResult.value = result.getOrNull()
                // In case of error, you can update an error message
                result.onFailure { throwable ->
                    errorMessage.value = throwable.message ?: "Unknown translation error"
                }
            } finally {
                isLoading.value = false
            }
        }
    }
}