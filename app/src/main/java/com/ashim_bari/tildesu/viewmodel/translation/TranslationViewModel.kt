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
                val result = translationRepository.translateText(sourceText, sourceLang, targetLang)
                _translationResult.value =
                    result.toString()  // Correctly updating the MutableStateFlow
            } catch (e: Exception) {
                errorMessage.value = "Failed to translate text. Please try again."
            } finally {
                isLoading.value = false
            }
        }

    }
}
