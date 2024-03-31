package com.ashim_bari.tildesu.viewmodel.language

import android.content.Context
import android.content.res.Configuration
import androidx.lifecycle.ViewModel
import com.ashim_bari.tildesu.model.language.LanguageManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class LanguageViewModel @Inject constructor(
) : ViewModel() {
    private val _language = MutableStateFlow(LanguageManager.DEFAULT_LANGUAGE)
    val language: StateFlow<String> = _language
    fun setLanguage(context: Context, language: String) {
        if (_language.value != language) {
            _language.value = language
            LanguageManager.setLocale(context, language)
            // No need to manually update Locale here as com.ashim_bari.tildesu.model.language.LanguageManager already handles it
        }
    }

    private fun updateLocale(context: Context, language: String) {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        context.createConfigurationContext(config)
    }
}