package com.ashim_bari.tildesu.viewmodel

import android.content.Context
import android.content.res.Configuration
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.Locale

class LanguageViewModel : ViewModel() {
    private val _language = MutableLiveData<String>()
    val language: LiveData<String> = _language

    fun setLanguage(context: Context, language: String) {
        _language.value = language
        updateLocale(context, language)
    }

    private fun updateLocale(context: Context, language: String) {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        context.createConfigurationContext(config)
    }
}


