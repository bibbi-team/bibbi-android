import java.util.Properties

plugins {
    id("com.android.library")
    id("kotlin-parcelize")
    id("org.jetbrains.kotlin.android")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
    jvmToolchain(17)
}

secrets {
    propertiesFileName = "secrets.properties"
}
val secretFile = file("../secrets.properties")
val secretProperties = Properties().apply {
    load(secretFile.inputStream())
}

android {
    namespace = "com.no5ing.bbibbi.widget"
    compileSdk = 34

    defaultConfig {
        minSdk = 26
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    buildFeatures{
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.10"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test:monitor:1.6.1")
    androidTestImplementation("junit:junit:4.13.2")
    api("androidx.compose.runtime:runtime:1.6.1")
    api("androidx.datastore:datastore-preferences-core:1.0.0")
    api("androidx.glance:glance-appwidget:1.0.0")
    api("androidx.glance:glance:1.0.0")
    api("androidx.work:work-runtime:2.9.0")
    implementation("androidx.annotation:annotation:1.7.1")
    implementation("androidx.compose.ui:ui-graphics:1.6.1")
    implementation("androidx.compose.ui:ui-unit:1.6.1")
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.core:core:1.12.0")
    implementation("androidx.security:security-crypto:1.1.0-alpha06")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okio:okio:3.6.0")
    implementation("io.coil-kt:coil-base:2.5.0")
    implementation("io.coil-kt:coil:2.5.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    testImplementation("junit:junit:4.13.2")
}