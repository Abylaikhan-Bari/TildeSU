package com.ashim_bari.tildesu.view

import LanguageManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.ashim_bari.tildesu.view.navigation.NavigationGraph
import com.ashim_bari.tildesu.view.ui.theme.TildeSUTheme
import com.ashim_bari.tildesu.viewmodel.LanguageViewModel
import com.google.firebase.Firebase
import com.google.firebase.appcheck.appCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.firebase.initialize

//import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory

class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val languageViewModel: LanguageViewModel = viewModel()
            val currentLanguageCode by languageViewModel.language.collectAsState()
            val LocalLanguageCode = staticCompositionLocalOf { "en" }
            CompositionLocalProvider(LocalLanguageCode provides currentLanguageCode) {
                TildeSUTheme {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        val navController = rememberNavController()
                        NavigationGraph(navController)
                    }
                }
            }
        }


        initializeFirebase()
        applyLanguageChange()
    }

    private fun initializeFirebase() {
        Firebase.initialize(context = this)
        Firebase.appCheck.installAppCheckProviderFactory(DebugAppCheckProviderFactory.getInstance())
    }

    private fun applyLanguageChange() {
        val languageCode = LanguageManager.getLanguagePreference(this)
        LanguageManager.setLocale(this, languageCode)
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LanguageManager.setLocale(newBase, LanguageManager.getLanguagePreference(newBase)))
    }
    fun restartActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
        overridePendingTransition(0, 0) // Optional: Smooth transition or no animation.
    }


}


