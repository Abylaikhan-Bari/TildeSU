package com.ashim_bari.tildesu.data.api.service

import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class TranslationService {

    private val apiKey = "a_4RyuRWsTq7K9eMMGj2vJY1PnK722FQlt2kdVAgt6poI1VUInvfgHfa5g4oT5nbmjo3rRc7M0rZAF0qGF"

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.lingvanex.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val apiService = retrofit.create(LingvanexApiService::class.java)

    suspend fun translateText(sourceText: String, sourceLang: String, targetLang: String): String {
        val response: Response<LingvanexApiService.TranslationResponse> = apiService.translateText(
            text = sourceText,
            from = sourceLang,
            to = targetLang,
            apiKey = "Bearer $apiKey"
        )

        if (response.isSuccessful) {
            return response.body()?.result ?: "Error: Translation unsuccessful"
        } else {
            return "Error: ${response.errorBody()?.string()}"
        }
    }
}
