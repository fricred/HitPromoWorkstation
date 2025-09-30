# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# ================================================================================================
# General Android Rules
# ================================================================================================

# Preserve line number information for debugging stack traces
-keepattributes SourceFile,LineNumberTable

# Keep annotations for reflection and runtime processing
-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod

# Keep generic signatures for reflection
-keepattributes Signature

# ================================================================================================
# AWS Amplify SDK Rules
# ================================================================================================

# Keep all AWS Amplify classes and their members
-keep class com.amplifyframework.** { *; }
-keep interface com.amplifyframework.** { *; }
-dontwarn com.amplifyframework.**

# Keep AWS SDK Core classes
-keep class com.amazonaws.** { *; }
-keep interface com.amazonaws.** { *; }
-dontwarn com.amazonaws.**

# AWS Cognito Auth Plugin
-keep class com.amplifyframework.auth.cognito.** { *; }
-keep interface com.amplifyframework.auth.cognito.** { *; }
-dontwarn com.amplifyframework.auth.cognito.**

# AWS Mobile Client
-keep class com.amazonaws.mobile.client.** { *; }
-dontwarn com.amazonaws.mobile.client.**

# AWS Auth Core
-keep class com.amazonaws.mobile.auth.** { *; }
-dontwarn com.amazonaws.mobile.auth.**

# Keep all model classes for AWS
-keepclassmembers class * extends com.amazonaws.mobileconnectors.appsync.** {
    *;
}

# Preserve AWS SDK exceptions
-keep class com.amazonaws.**.*Exception* { *; }
-keep class com.amplifyframework.**.*Exception* { *; }

# ================================================================================================
# Kotlin Coroutines Rules
# ================================================================================================

# Keep Kotlin coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}

# Keep all coroutine-related classes
-keep class kotlinx.coroutines.** { *; }
-dontwarn kotlinx.coroutines.**

# Preserve coroutine debug metadata
-keepattributes *Annotation*

# Keep Flow, StateFlow, and SharedFlow
-keep class kotlinx.coroutines.flow.** { *; }

# ServiceLoader support for coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}

# Most of volatile fields are updated with AFU and should not be mangled
-keepclassmembers class kotlinx.coroutines.** {
    volatile <fields>;
}

# ================================================================================================
# Kotlin Metadata and Reflection
# ================================================================================================

# Keep Kotlin metadata for reflection
-keep class kotlin.Metadata { *; }
-keep class kotlin.reflect.** { *; }
-dontwarn kotlin.reflect.**

# Keep Kotlin annotation processing
-keepclassmembers class **$WhenMappings {
    <fields>;
}

# Keep sealed classes
-keep class * extends kotlin.coroutines.Continuation

# ================================================================================================
# Hilt / Dagger Dependency Injection
# ================================================================================================

# Keep Hilt generated classes
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep class * extends dagger.hilt.android.internal.managers.ViewComponentManager$FragmentContextWrapper { *; }

# Keep @Inject constructors
-keepclasseswithmembers class * {
    @javax.inject.Inject <init>(...);
}

# Keep @Inject fields
-keepclasseswithmembers class * {
    @javax.inject.Inject <fields>;
}

# Keep @Inject methods
-keepclasseswithmembers class * {
    @javax.inject.Inject <methods>;
}

# ================================================================================================
# Jetpack Compose Rules
# ================================================================================================

# Keep Compose compiler generated classes
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**

# Keep Composable functions
-keep @androidx.compose.runtime.Composable class * { *; }
-keep @androidx.compose.runtime.Composable interface * { *; }

# ================================================================================================
# AndroidX Security Crypto (EncryptedSharedPreferences)
# ================================================================================================

# Keep security crypto classes
-keep class androidx.security.crypto.** { *; }
-dontwarn androidx.security.crypto.**

# Keep Tink crypto library (used by security-crypto)
-keep class com.google.crypto.tink.** { *; }
-dontwarn com.google.crypto.tink.**

# ================================================================================================
# Retrofit / OkHttp / Moshi (if used)
# ================================================================================================

# Retrofit
-keepattributes Signature, InnerClasses, EnclosingMethod
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations
-keepattributes AnnotationDefault

-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}

-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
-dontwarn javax.annotation.**
-dontwarn kotlin.Unit

# OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase

# Moshi
-keepclasseswithmembers class * {
    @com.squareup.moshi.* <methods>;
}
-keep @com.squareup.moshi.JsonQualifier @interface *
-keepclassmembers @com.squareup.moshi.JsonClass class * extends java.lang.Enum {
    <fields>;
}

# ================================================================================================
# Project-Specific Rules
# ================================================================================================

# Keep all data models and DTOs
-keep class net.hitpromo.hitpromoworkstation.data.remote.dto.** { *; }
-keep class net.hitpromo.hitpromoworkstation.domain.model.** { *; }

# Keep ViewModel classes
-keep class * extends androidx.lifecycle.ViewModel {
    <init>(...);
}

# Keep all classes with @Serializable annotation
-keepclassmembers class * {
    @kotlinx.serialization.Serializable *;
}

# ================================================================================================
# Additional Warnings to Suppress
# ================================================================================================

# Suppress warnings for missing classes that are not used
-dontwarn org.conscrypt.**
-dontwarn org.bouncycastle.**
-dontwarn org.openjsse.**
-dontwarn javax.annotation.**
-dontwarn edu.umd.cs.findbugs.annotations.**

# ================================================================================================
# Debugging Support (Remove in production if not needed)
# ================================================================================================

# Keep source file names and line numbers for better crash reports
-keepattributes SourceFile,LineNumberTable

# Rename source file attribute to hide actual source file name
-renamesourcefileattribute SourceFile