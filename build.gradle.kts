buildscript {
    repositories{
        mavenCentral()
        google()
    }
    dependencies {
        classpath("com.google.gms:google-services:4.4.1")
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.51")
        classpath("com.google.devtools.ksp:symbol-processing-gradle-plugin:1.9.22-1.0.17") // Use the latest version available
        classpath("com.google.android.libraries.mapsplatform.secrets-gradle-plugin:secrets-gradle-plugin:2.0.1")
    }
}
// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.2.2" apply false
    id("org.jetbrains.kotlin.android") version "1.9.0" apply false
    id("com.google.dagger.hilt.android") version "2.51" apply false
    id ("com.google.devtools.ksp") version "1.9.22-1.0.17" apply false
    //id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}