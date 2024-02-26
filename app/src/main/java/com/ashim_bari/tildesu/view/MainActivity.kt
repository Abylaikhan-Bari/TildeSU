package com.ashim_bari.tildesu.view

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
import com.ashim_bari.tildesu.R
import com.ashim_bari.tildesu.model.language.LanguageManager
import com.ashim_bari.tildesu.view.navigation.Navigation
import com.ashim_bari.tildesu.view.navigation.NavigationGraph
import com.ashim_bari.tildesu.view.ui.theme.TildeSUTheme
import com.ashim_bari.tildesu.viewmodel.language.LanguageViewModel
import com.google.firebase.Firebase
import com.google.firebase.appcheck.appCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.firebase.initialize

//import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory

class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize Firebase before setting the content view
        initializeFirebase()

        // Apply language change here or determine the initial screen based on some condition
        applyLanguageChange()

        val initialScreen = determineInitialScreen()

        setContent {
            val languageViewModel: LanguageViewModel = viewModel()
            val currentLanguageCode by languageViewModel.language.collectAsState()
            val localLanguageCode = staticCompositionLocalOf { "en" }
            CompositionLocalProvider(localLanguageCode provides currentLanguageCode) {
                TildeSUTheme {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        val navController = rememberNavController()
                        NavigationGraph(navController, initialScreen)
                    }
                }
            }
        }
    }



private fun determineInitialScreen(): String {
    // Implement logic to determine which screen to show first
    // This could be based on authentication status, user preferences, etc.
    // For example, return Navigation.AUTHENTICATION_ROUTE or Navigation.MAIN_ROUTE
    return Navigation.AUTHENTICATION_ROUTE // or some logic to choose the screen
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
        // Use the custom animations for a more pleasant effect
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }



}


