plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
    kotlin("kapt")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.ashim_bari.tildesu"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.ashim_bari.tildesu"
        minSdk = 25
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation("androidx.room:room-common:2.6.1")
    implementation("androidx.security:security-crypto-ktx:1.0.0")
    // Core library desugaring enabled
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:1.1.5")

    // AndroidX core, ktx, and UI
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation("androidx.autofill:autofill:1.1.0")
    implementation("androidx.constraintlayout:constraintlayout-compose:1.0.1")
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.media3:media3-common:1.2.1")
    implementation("androidx.navigation:navigation-compose:2.7.7")
    // adding the dependency for the Google AI client SDK for Android
    implementation("com.google.ai.client.generativeai:generativeai:0.3.0")

    //Room database
    val room_version = "2.6.1"
    implementation("androidx.room:room-runtime:$room_version")
    kapt("androidx.room:room-compiler:$room_version") // Correct for Kotlin
    implementation ("androidx.room:room-ktx:$room_version")
    //Dagger-Hilt DI
    implementation("com.google.dagger:hilt-android:2.51")
    kapt("com.google.dagger:hilt-compiler:2.51")
//    implementation("com.google.dagger:dagger:2.44")
//    kapt("com.google.dagger:dagger-compiler:2.44")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")
    //Security
    implementation ("androidx.security:security-crypto-ktx:1.1.0-alpha06")

    //Retrofit
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    //OKHTTP
    implementation ("com.squareup.okhttp3:logging-interceptor:4.9.0")
    // Compose dependencies
    implementation("androidx.compose.foundation:foundation:1.6.1")
    implementation("androidx.compose.material:material-icons-extended:1.6.1")
    implementation("androidx.compose.material3:material3:1.2.0")
    implementation("androidx.compose.material3:material3-window-size-class:1.2.0")
    implementation("androidx.compose.material3:material3-adaptive:1.0.0-alpha06")
    implementation("androidx.compose.material3:material3-adaptive-navigation-suite:1.0.0-alpha03")
    implementation("androidx.compose.runtime:runtime-livedata:1.6.1")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation(platform("androidx.compose:compose-bom:2023.08.00"))
    implementation("androidx.wear.compose:compose-material:1.3.0")

    // Firebase dependencies
    implementation("com.google.firebase:firebase-auth:22.3.1")
    implementation("com.google.firebase:firebase-database:20.3.1")
    implementation("com.google.firebase:firebase-firestore:24.11.0")
    implementation("com.google.firebase:firebase-storage-ktx:20.3.0")
    implementation(platform("com.google.firebase:firebase-bom:32.7.2"))
    implementation("com.google.firebase:firebase-appcheck-debug")
    // Firebase App Check for debug builds
    //implementation("com.google.firebase:firebase-appcheck-playintegrity:17.1.2") // Firebase App Check for Play Integrity, commented as it might not be needed for all builds

    // Image loading library for Compose
    implementation("io.coil-kt:coil-compose:2.4.0")

    // Testing libraries
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.08.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")

    // Debugging and tooling for Compose
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}
// Allow references to generated code
kapt {
    correctErrorTypes = true
}
hilt {
    enableAggregatingTask = true
    enableTransformForLocalTests = true
}