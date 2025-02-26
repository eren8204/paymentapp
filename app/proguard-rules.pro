# ---- WebView (if using JavaScript Interface) ----
# Keep JavaScript interface class
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

# ---- General Rules ----
# Keep model classes (if using Gson, Retrofit, or similar)
-keep class com.unotag.unopay.** { *; }
-keep interface com.unotag.unopay.** { *; }

# Keep all annotated classes (for Gson, Retrofit, Room, etc.)
-keepattributes *Annotation*

# ---- Prevent Warnings ----
-dontwarn com.unotag.unopay.**
-dontwarn org.bouncycastle.jsse.BCSSLParameters
-dontwarn org.bouncycastle.jsse.BCSSLSocket
-dontwarn org.bouncycastle.jsse.provider.BouncyCastleJsseProvider
-dontwarn org.conscrypt.Conscrypt$Version
-dontwarn org.conscrypt.Conscrypt
-dontwarn org.openjsse.javax.net.ssl.SSLParameters
-dontwarn org.openjsse.javax.net.ssl.SSLSocket
-dontwarn org.openjsse.net.ssl.OpenJSSE
-dontwarn com.oracle.svm.core.annotate.Delete
-dontwarn com.oracle.svm.core.annotate.Substitute
-dontwarn com.oracle.svm.core.annotate.TargetClass

# ---- Keep Google Install Referrer API ----
-keep class com.android.installreferrer.** { *; }

# ---- Keep Gson (Prevent Serialization Issues) ----
-keep class com.google.gson.** { *; }
-keep class * extends com.google.gson.TypeAdapter { *; }

# Keep Retrofit CallAdapter and Converter factories
-keep class retrofit2.** { *; }
-keep class retrofit2.Converter$Factory { *; }
-keep class retrofit2.CallAdapter$Factory { *; }
-keep class okhttp3.** { *; }
-keep class com.squareup.okhttp3.** { *; }
-keep class okhttp3.logging.** { *; }

# ---- Keep Glide (Prevent Image Loading Issues) ----
-keep class com.bumptech.glide.** { *; }
-keep class * implements com.bumptech.glide.module.AppGlideModule { *; }
-keep class * extends com.bumptech.glide.module.LibraryGlideModule { *; }

# ---- Keep Lottie (Prevent Animation Parsing Issues) ----
-keep class com.airbnb.lottie.** { *; }
-dontwarn com.airbnb.lottie.**

# ---- Keep Volley (Ensure Network Requests Work) ----
-keep class com.android.volley.** { *; }

# ---- Keep ViewBinding (if using ViewBinding) ----
-keep class *Binding { *; }

# ---- Keep Parcelable & Serializable Classes ----
-keep class * implements android.os.Parcelable { *; }
-keep class * implements java.io.Serializable { *; }

# ---- Keep AndroidX Lifecycle (For MVVM ViewModel & LiveData) ----
-keep class androidx.lifecycle.LiveData { *; }
-keep class androidx.lifecycle.ViewModel { *; }


# ---- Optional Debugging (Remove for Production) ----
# Uncomment this to keep line numbers for debugging stack traces
-keepattributes SourceFile,LineNumberTable