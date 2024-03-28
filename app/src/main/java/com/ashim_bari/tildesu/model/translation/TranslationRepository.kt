package com.ashim_bari.tildesu.model.translation

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
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
                    val jsonResponse = response.body?.string()
                    jsonResponse?.let {
                        val jsonObject = JSONObject(it)
                        if (jsonObject.isNull("err")) {
                            Result.success(jsonObject.getString("result"))
                        } else {
                            Result.failure(Exception("Error in translation: ${jsonObject.getString("err")}"))
                        }
                    } ?: Result.failure(Exception("No response from the server"))
                } else {
                    Result.failure(Exception("Server responded with error: ${response.message}"))
                }
            }
        } catch (e: Exception) {
            Result.failure(Exception("Translation error: ${e.localizedMessage}", e))
        }
    }
}