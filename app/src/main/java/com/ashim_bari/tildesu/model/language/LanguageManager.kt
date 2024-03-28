package com.ashim_bari.tildesu.model.language

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import java.util.Locale

object LanguageManager {
    private const val LANGUAGE_KEY = "language_key"
    const val DEFAULT_LANGUAGE = "en"
    fun setLocale(context: Context, languageCode: String): Context {
        persistLanguagePreference(context, languageCode)
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val resources = context.resources
        val configuration = Configuration(resources.configuration)
        configuration.setLocale(locale)
        return context.createConfigurationContext(configuration)
    }
    private fun persistLanguagePreference(context: Context, languageCode: String) {
        val preferences: SharedPreferences = context.getSharedPreferences("language_settings", Context.MODE_PRIVATE)
        preferences.edit().putString(LANGUAGE_KEY, languageCode).apply()
    }
    fun getLanguagePreference(context: Context): String {
        val preferences: SharedPreferences = context.getSharedPreferences("language_settings", Context.MODE_PRIVATE)
        return preferences.getString(LANGUAGE_KEY, DEFAULT_LANGUAGE) ?: DEFAULT_LANGUAGE
    }
}