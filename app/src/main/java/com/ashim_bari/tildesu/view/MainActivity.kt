package com.ashim_bari.tildesu.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.ashim_bari.tildesu.view.navigation.NavigationGraph
import com.ashim_bari.tildesu.view.ui.theme.TildeSUTheme
import com.google.firebase.BuildConfig
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.appCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.firebase.initialize

//import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Firebase.initialize(context = this)
        Firebase.appCheck.installAppCheckProviderFactory(
            DebugAppCheckProviderFactory.getInstance(),
        )
        // Initialize Firebase
//        FirebaseApp.initializeApp(this)
//
//        val appCheck = FirebaseAppCheck.getInstance()
//        // Conditional App Check provider installation based on build type
//        if (BuildConfig.DEBUG) {
//            appCheck.installAppCheckProviderFactory(DebugAppCheckProviderFactory.getInstance())
//        } else {
//            appCheck.installAppCheckProviderFactory(PlayIntegrityAppCheckProviderFactory.getInstance())
//        }

        setContent {
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
}



