package com.ashim_bari.tildesu.di

import com.ashim_bari.tildesu.model.language.LanguageManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LanguageModule {
    @Singleton
    @Provides
    fun provideLanguageManager(): LanguageManager = LanguageManager
}
