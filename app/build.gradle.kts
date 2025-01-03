plugins {
    alias(libs.plugins.androidApplication)

}
android {
    namespace = "com.example.paymentapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.paymentapp"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation ("com.github.barteksc:android-pdf-viewer:3.2.0-beta.1")
    implementation (libs.gson)
    implementation(libs.blurry)
    implementation(libs.volley)
    implementation(libs.lottie)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.logging.interceptor)
    implementation(libs.imageslideshow)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.material.v190)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.gridlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation(libs.imageslideshow)
    implementation(libs.sdp.android)
    implementation(libs.kotlin.script.runtime)

}