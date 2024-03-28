package com.ashim_bari.tildesu.data.api.service

import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.POST

interface LingvanexApiService {
    @FormUrlEncoded
    @POST("translate")
    suspend fun translateText(
        @Field("text") text: String,
        @Field("from") from: String,
        @Field("to") to: String,
        @Header("Authorization") apiKey: String
    ): Response<TranslationResponse>
    data class TranslationResponse(
        val result: String // Adjust according to the actual API response
    )
}