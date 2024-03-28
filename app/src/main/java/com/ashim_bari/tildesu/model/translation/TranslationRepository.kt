package com.ashim_bari.tildesu.model.translation

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class TranslationRepository @Inject constructor(
    @Named("LingvanexApiKey") private val apiKey: String
) {
    private val client = OkHttpClient()

    suspend fun translateText(sourceText: String, sourceLang: String, targetLang: String): Result<String> = withContext(Dispatchers.IO) {
        val mediaType = "application/json".toMediaType()
        val requestBody = "{\"text\":\"$sourceText\",\"from\":\"$sourceLang\",\"to\":\"$targetLang\"}".toRequestBody(mediaType)

        val request = Request.Builder()
            .url("https://api-b2b.backenster.com/b1/api/v3/translate")
            .post(requestBody)
            .addHeader("accept", "application/json")
            .addHeader("content-type", "application/json")
            .addHeader("Authorization", "Bearer $apiKey")
            .build()

        return@withContext try {
            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    // Assuming the response body can be directly converted to a String
                    Result.success(response.body?.string() ?: "Translation failed.")
                } else {
                    Result.failure(Exception("Error: ${response.message}"))
                }
            }
        } catch (e: Exception) {
            Result.failure(Exception("Translation error: ${e.message}", e))
        }
    }
}
