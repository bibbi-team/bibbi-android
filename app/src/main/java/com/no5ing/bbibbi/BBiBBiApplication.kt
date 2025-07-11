package com.no5ing.bbibbi

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import coil.memory.MemoryCache
import com.google.android.gms.ads.MobileAds
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.appCheck
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import com.kakao.sdk.common.KakaoSdk
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import timber.log.Timber

@HiltAndroidApp
class BBiBBiApplication : Application(), ImageLoaderFactory {
    @OptIn(ExperimentalCoroutinesApi::class)
    val dispatcher = Dispatchers.IO.limitedParallelism(4)

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        FirebaseApp.initializeApp(this)
        Firebase.appCheck.installAppCheckProviderFactory(
            PlayIntegrityAppCheckProviderFactory.getInstance(),
        )
        KakaoSdk.init(this, BuildConfig.kakaoApiKey)
        Thread {
            MobileAds.initialize(this)
        }.start()
    }

    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .memoryCache {
                MemoryCache.Builder(this)
                    .maxSizePercent(0.25)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(this.cacheDir.resolve("image_cache"))
                    .maxSizeBytes(50 * 1024 * 1024)
                    .build()
            }
            .dispatcher(dispatcher)
            .bitmapFactoryMaxParallelism(4)
            .respectCacheHeaders(false)
            .build()
    }
}