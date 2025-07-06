import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("kotlin-parcelize")
    id("dagger.hilt.android.plugin")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

kotlin {
    jvmToolchain(21)
}

secrets {
    propertiesFileName = "secrets.properties"
}
val secretFile = file("../secrets.properties")
val secretProperties = Properties().apply {
    load(secretFile.inputStream())
}


android {
    namespace = "com.no5ing.bbibbi"
    compileSdk = 35

    signingConfigs {
        create("release") {
            keyPassword = secretProperties["keyPassword"]?.toString()
            keyAlias = secretProperties["keyAlias"]?.toString()
            storeFile = file(secretProperties["storeFile"]?.toString() ?: "")
            storePassword = secretProperties["storePassword"]?.toString()
        }
    }

    defaultConfig {
        applicationId = "com.no5ing.bbibbi"
        minSdk = 26
        targetSdk = 35
        versionCode = 11015
        versionName = "1.3.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
        create("benchmark") {
            initWith(buildTypes.getByName("release"))
            matchingFallbacks += listOf("release")
            isDebuggable = false
            signingConfig = signingConfigs.getByName("debug")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.10"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test:monitor:1.6.1")
    androidTestImplementation("junit:junit:4.13.2")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.10.01"))
    debugImplementation("androidx.compose.ui:ui-tooling")
    implementation("com.github.lincollincol:amplituda:2.3.0")
    implementation("androidx.media3:media3-exoplayer:1.1.0")
    implementation("androidx.camera:camera-view:1.4.0-alpha04")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation("androidx.activity:activity-ktx:1.8.2")
    implementation("androidx.annotation:annotation-experimental:1.4.0")
    implementation("androidx.annotation:annotation:1.7.1")
    implementation("androidx.browser:browser:1.8.0")
    implementation("androidx.camera:camera-core:1.4.0-alpha04")
    implementation("androidx.camera:camera-lifecycle:1.4.0-alpha04")
    implementation("androidx.camera:camera-camera2:1.4.0-alpha04")
    implementation("androidx.compose.animation:animation-core:1.6.3")
    implementation("androidx.compose.animation:animation:1.6.3")
    implementation("androidx.compose.foundation:foundation-layout:1.6.3")
    implementation("androidx.compose.foundation:foundation:1.6.3")
    implementation("androidx.compose.material3:material3:1.2.1")
    implementation("androidx.compose.material:material-icons-core:1.6.3")
    implementation("androidx.compose.material:material:1.6.3")
    implementation("androidx.compose.runtime:runtime-livedata:1.6.3")
    implementation("androidx.compose.runtime:runtime-saveable:1.6.3")
    implementation("androidx.compose.runtime:runtime:1.6.3")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-geometry:1.7.8")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-text:1.7.8")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.ui:ui-unit:1.7.8")
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.core:core-splashscreen:1.0.1")
    implementation("androidx.glance:glance-appwidget:1.0.0")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")
    implementation("androidx.lifecycle:lifecycle-common:2.7.0")
    implementation("androidx.lifecycle:lifecycle-livedata-core-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-savedstate:2.7.0")
    implementation("androidx.navigation:navigation-common:2.7.7")
    implementation("androidx.navigation:navigation-compose:2.7.7")
    implementation("androidx.navigation:navigation-runtime-ktx:2.7.7")
    implementation("androidx.paging:paging-common-ktx:3.3.0-alpha04")
    implementation("androidx.paging:paging-compose:3.3.0-alpha04")
    implementation("androidx.savedstate:savedstate:1.2.1")
    implementation("androidx.security:security-crypto:1.1.0-alpha06")
    implementation("com.airbnb.android:lottie-compose:6.3.0")
    implementation("com.airbnb.android:lottie:6.3.0")
    implementation("com.android.installreferrer:installreferrer:2.2")
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.16.0")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.16.0")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.16.0")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.16.0")
    implementation("com.github.skydoves:balloon-compose:1.6.4")
    implementation("com.github.skydoves:balloon:1.6.4")
    implementation("com.github.skydoves:sandwich-retrofit:2.0.5")
    implementation("com.github.skydoves:sandwich:2.0.5")
    implementation("com.google.accompanist:accompanist-permissions:0.33.2-alpha")
    implementation("com.google.android.gms:play-services-auth:21.0.0")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("com.google.dagger:dagger:2.49")
    implementation("com.google.dagger:hilt-android:2.49")
    implementation("com.google.dagger:hilt-core:2.49")
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("com.google.firebase:firebase-appcheck-playintegrity")
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-crashlytics-ktx")
    implementation("com.google.firebase:firebase-messaging-ktx")
    implementation("com.google.guava:guava:31.1-jre")
    implementation("com.jakewharton.timber:timber:5.0.1")
    implementation("com.kakao.sdk:v2-auth:2.19.0")
    implementation("com.kakao.sdk:v2-common:2.19.0")
    implementation("com.kakao.sdk:v2-user:2.19.0")
    implementation("com.mixpanel.android:mixpanel-android:7.4.0")
    implementation("com.squareup.okhttp3:okhttp:5.0.0-alpha.2")
    implementation("com.squareup.okio:okio:3.6.0")
    implementation("com.squareup.retrofit2:converter-jackson:2.9.0")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("io.coil-kt:coil-base:2.5.0")
    implementation("io.coil-kt:coil-compose-base:2.5.0")
    implementation("io.coil-kt:coil-compose:2.5.0")
    implementation("io.coil-kt:coil:2.5.0")
    implementation("io.github.boguszpawlowski.composecalendar:composecalendar:1.1.1")
    implementation("javax.inject:javax.inject:1")
    implementation("org.jetbrains.kotlin:kotlin-parcelize-runtime:1.9.22")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")
    implementation(platform("androidx.compose:compose-bom:2023.10.01"))
    implementation(platform("com.google.firebase:firebase-bom:32.7.0"))
    implementation("com.google.android.gms:play-services-ads:22.0.0")
    implementation(project(":widget"))
    kapt("com.google.dagger:dagger-compiler:2.49")
    kapt("com.google.dagger:hilt-android-compiler:2.49")
    testImplementation("junit:junit:4.13.2")
}
