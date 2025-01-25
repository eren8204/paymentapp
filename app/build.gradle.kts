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
android {
    namespace = "com.example.paymentapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.paymentapp"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String","api_url","\"$api_url\"")
    }
    buildFeatures{
        buildConfig = true
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
}

dependencies {

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
    implementation(libs.imageslideshow)
    implementation(libs.sdp.android)
    implementation(libs.kotlin.script.runtime)
    implementation (libs.collection)


}