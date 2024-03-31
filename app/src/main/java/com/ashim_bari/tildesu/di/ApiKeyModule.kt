package com.ashim_bari.tildesu.di

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiKeyModule {
    @Provides
    @Singleton
    @Named("LingvanexApiKey")
    fun provideLingvanexApiKey(@ApplicationContext context: Context): String {
        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
        val sharedPreferences = EncryptedSharedPreferences.create(
            "secure_prefs",
            masterKeyAlias,
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
        // Return the API key stored securely; for example purposes only
        return sharedPreferences.getString(
            "a_4RyuRWsTq7K9eMMGj2vJY1PnK722FQlt2kdVAgt6poI1VUInvfgHfa5g4oT5nbmjo3rRc7M0rZAF0qGF",
            "a_4RyuRWsTq7K9eMMGj2vJY1PnK722FQlt2kdVAgt6poI1VUInvfgHfa5g4oT5nbmjo3rRc7M0rZAF0qGF"
        ) ?: "your_default_api_key"
    }
}