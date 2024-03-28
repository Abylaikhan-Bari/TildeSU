package com.ashim_bari.tildesu.di

import com.ashim_bari.tildesu.data.api.service.LingvanexApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit = Retrofit.Builder()
        .baseUrl("https://lingvanex.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    @Provides
    @Singleton
    fun provideLingvanexApiService(retrofit: Retrofit): LingvanexApiService =
        retrofit.create(LingvanexApiService::class.java)
}