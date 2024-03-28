package com.ashim_bari.tildesu.model.translation

import com.ashim_bari.tildesu.data.api.service.LingvanexApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class TranslationRepository @Inject constructor(
    private val service: LingvanexApiService,
    @Named("LingvanexApiKey") private val apiKey: String
) {

    suspend fun translateText(sourceText: String, sourceLang: String, targetLang: String): String = withContext(Dispatchers.IO) {
        try {
            val response = service.translateText(
                text = sourceText,
                from = sourceLang,
                to = targetLang,
                apiKey = "Bearer $apiKey"
            )
            if (response.isSuccessful) {
                response.body()?.result ?: "Translation failed."
            } else {
                "Error: ${response.errorBody()?.string()}"
            }
        } catch (e: Exception) {
            "Translation error: ${e.message}"
        }
    }
}

