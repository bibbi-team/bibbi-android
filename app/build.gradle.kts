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

secrets {
    propertiesFileName = "secrets.properties"
}
val secretFile = file("../secrets.properties")
val secretProperties = Properties().apply {
    load(secretFile.inputStream())
}

val majorVersion = 1
val minorVersion = 1
val patchVersion = 0
val buildVersion = 5

android {
    namespace = "com.no5ing.bbibbi"
    compileSdk = 34

    signingConfigs {
        create("release") {
            keyPassword = secretProperties["keyPassword"]?.toString()
            keyAlias = secretProperties["keyAlias"]?.toString()
            storeFile = file(secretProperties["storeFile"]?.toString()?:"")
            storePassword = secretProperties["storePassword"]?.toString()
        }
    }

    defaultConfig {
        applicationId = "com.no5ing.bbibbi"
        minSdk = 26
        targetSdk = 34
        versionCode = majorVersion * 10000 + minorVersion * 1000 + patchVersion * 100 + buildVersion
        versionName = "$majorVersion.$minorVersion.$patchVersion"

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
            signingConfig = signingConfigs.getByName("debug")
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
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.3"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(project(":widget"))
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation(platform("androidx.compose:compose-bom:2023.10.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.10.01"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
    implementation("androidx.compose.runtime:runtime-livedata:1.5.4")
    implementation("com.airbnb.android:lottie-compose:6.3.0")
    implementation("io.coil-kt:coil-compose:2.5.0")
    implementation("com.google.accompanist:accompanist-permissions:0.33.2-alpha")
    implementation("androidx.camera:camera-camera2:1.4.0-alpha03")
    implementation("androidx.camera:camera-lifecycle:1.4.0-alpha03")
    implementation ("androidx.camera:camera-view:1.4.0-alpha03")
    implementation("androidx.navigation:navigation-compose:2.7.6")
    implementation("androidx.paging:paging-runtime-ktx:3.2.1")
    implementation("androidx.paging:paging-compose:3.3.0-alpha02")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0-alpha01")
    implementation("androidx.security:security-crypto:1.1.0-alpha06")
    implementation("androidx.compose.material:material:1.5.4")
    implementation("com.mixpanel.android:mixpanel-android:7.4.0")
    implementation("com.google.dagger:hilt-android:2.49")
    kapt("com.google.dagger:hilt-android-compiler:2.49")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.retrofit2:converter-jackson:2.9.0")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.16.0")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.16.0")
    implementation("com.jakewharton.timber:timber:5.0.1")
    implementation("com.squareup.okhttp3:logging-interceptor:5.0.0-alpha.2")
    implementation("com.github.skydoves:sandwich-retrofit:2.0.5")
    implementation("com.github.skydoves:sandwich-retrofit-serialization:2.0.5")

    implementation(platform("com.google.firebase:firebase-bom:32.7.0"))
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("com.google.firebase:firebase-messaging-ktx")
    implementation("com.google.firebase:firebase-crashlytics-ktx")
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.android.gms:play-services-auth:20.7.0")

    implementation("com.kakao.sdk:v2-all:2.19.0")
    implementation("com.kakao.sdk:v2-user:2.19.0")
    implementation("com.github.skydoves:balloon-compose:1.6.4")
    implementation("io.github.boguszpawlowski.composecalendar:composecalendar:1.1.1")
    implementation("androidx.core:core-splashscreen:1.0.1")
    implementation("com.android.installreferrer:installreferrer:2.2")

    implementation("androidx.glance:glance-appwidget:1.0.0")
    implementation("androidx.glance:glance-material3:1.0.0")
    implementation("androidx.work:work-runtime-ktx:2.9.0")
    implementation("androidx.profileinstaller:profileinstaller:1.3.1")
}