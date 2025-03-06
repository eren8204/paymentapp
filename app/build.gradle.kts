import java.util.Properties
plugins {
    alias(libs.plugins.androidApplication)

}
val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")

if (localPropertiesFile.exists()) {
    localProperties.load(localPropertiesFile.inputStream())
}
val api_url: String = localProperties.getProperty("unopay_api_url")
val api_url_non_auth: String = localProperties.getProperty("unopay_url")
android {
    namespace = "com.unotag.unopay"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.unotag.unopay"
        minSdk = 24
        targetSdk = 34
        versionCode = 12
        versionName = "1.0.11"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String","api_url","\"$api_url\"")
        buildConfigField("String","api_url_non_auth","\"$api_url_non_auth\"")
    }
    buildFeatures{
        buildConfig = true
    }
    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
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
}

dependencies {

    implementation(libs.vanniktech.android.image.cropper)
    implementation(libs.okhttp3.logging.interceptor.v493)
    implementation(libs.google.app.update)
    implementation(libs.installreferrer)
    implementation (libs.android.pdf.viewer)
    implementation(libs.recyclerview)
    implementation(libs.glide)
    implementation(libs.gson)
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
    implementation(libs.sdp.android)
    implementation(libs.kotlin.script.runtime)
    implementation (libs.collection)


}