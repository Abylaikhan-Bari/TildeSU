package com.ashim_bari.tildesu.di

import android.content.Context
import android.content.SharedPreferences
import com.ashim_bari.tildesu.data.api.service.TranslationService
import com.ashim_bari.tildesu.data.db.dao.UserDao
import com.ashim_bari.tildesu.model.exercise.ExerciseRepository
import com.ashim_bari.tildesu.model.user.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Singleton
    @Provides
    fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Singleton
    @Provides
    fun provideUserRepository(
        firebaseAuth: FirebaseAuth,
        firestore: FirebaseFirestore,
        userDao: UserDao
    ): UserRepository = UserRepository(firebaseAuth, firestore, userDao)

    @Singleton
    @Provides
    fun provideExerciseRepository(firebaseFirestore: FirebaseFirestore): ExerciseRepository =
        ExerciseRepository(firebaseFirestore)


    @Singleton
    @Provides
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences =
        context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    @Provides
    fun provideTranslationService(): TranslationService = TranslationService()

//    @Singleton
//    @Provides
//    fun provideLanguageManager(@ApplicationContext context: Context): LanguageManager {
//        return com.ashim_bari.tildesu.model.language.LanguageManager(context)
//    }
}
