package com.no5ing.bbibbi.util

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.res.Resources
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.DisplayMetrics
import android.view.Window
import android.view.WindowManager
import androidx.browser.customtabs.CustomTabsIntent
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.window.DialogWindowProvider
import androidx.core.content.ContextCompat
import com.android.installreferrer.api.InstallReferrerClient
import com.android.installreferrer.api.InstallReferrerStateListener
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.PermissionStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import timber.log.Timber
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

fun <A, B> List<A>.parallelMap(
    context: CoroutineContext = Dispatchers.Default,
    f: suspend (A) -> B
): List<B> = runBlocking {
    map { async(context) { f(it) } }.map { it.await() }
}

fun Context.openBrowser(url: String) {
    CustomTabsIntent.Builder().build().launchUrl(this, Uri.parse(url))
//    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
//    startActivity(intent)
}

@Composable
@ReadOnlyComposable
fun localResources(): Resources {
    LocalConfiguration.current
    return LocalContext.current.resources
}

@OptIn(ExperimentalPermissionsApi::class)
val emptyPermissionState = object : PermissionState {
    override val permission: String
        get() = ""
    override val status: PermissionStatus
        get() = PermissionStatus.Granted

    override fun launchPermissionRequest() {

    }
}

suspend fun Context.getCameraProvider(): ProcessCameraProvider =
    suspendCoroutine { continuation ->
        ProcessCameraProvider.getInstance(this).also { cameraProvider ->
            cameraProvider.addListener({
                continuation.resume(cameraProvider.get())
            }, ContextCompat.getMainExecutor(this))
        }
    }

suspend fun ImageCapture.takePhoto(context: Context): Uri? =
    suspendCoroutine { continuation ->
        val name = "${System.currentTimeMillis()}.jpg"
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/BBiBBi-Image")
            }
        }

        val outputOptions = ImageCapture.OutputFileOptions
            .Builder(
                context.contentResolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues
            )
            .build()

        this.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(e: ImageCaptureException) {
                    Timber.e("[CameraView] photo capture failed", e)
                    continuation.resume(null)
                }

                override fun onImageSaved(
                    output: ImageCapture.OutputFileResults
                ) {
                    Timber.d("onImageSaved: ${output.savedUri}")
                    continuation.resume(output.savedUri)
                }
            }
        )
    }
fun Context.openMarket() {
    val intent = Intent(Intent.ACTION_VIEW)
    intent.data = Uri.parse("market://details?id=com.no5ing.bbibbi")
    this.startActivity(intent)
}

fun Context.forceRestart() {
    if (this is Activity) {
        Timber.d("[Activity] Restarting application...")
        this.finish()
        this.startActivity(this.intent)
    }
}

enum class CustomDialogPosition {
    BOTTOM, TOP
}

fun Modifier.customDialogModifier(pos: CustomDialogPosition) = layout { measurable, constraints ->

    val placeable = measurable.measure(constraints)
    layout(constraints.maxWidth, constraints.maxHeight) {
        when (pos) {
            CustomDialogPosition.BOTTOM -> {
                placeable.place(0, constraints.maxHeight - placeable.height, 10f)
            }

            CustomDialogPosition.TOP -> {
                placeable.place(0, 0, 10f)
            }
        }
    }
}

@Composable
fun getDialogWindow(): Window? = (LocalView.current.parent as? DialogWindowProvider)?.window

@Composable
fun getActivityWindow(): Window? = LocalView.current.context.getActivityWindow()

private tailrec fun Context.getActivityWindow(): Window? =
    when (this) {
        is Activity -> window
        is ContextWrapper -> baseContext.getActivityWindow()
        else -> null
    }

@Composable
fun getScreenSize(): Pair<Dp, Dp> {
    val context = LocalContext.current
    val windowManager =
        remember { context.getSystemService(Context.WINDOW_SERVICE) as WindowManager }

    val metrics = DisplayMetrics().apply {
        windowManager.defaultDisplay.getRealMetrics(this)
    }
    return with(LocalDensity.current) {
        Pair(metrics.widthPixels.toDp(), metrics.heightPixels.toDp())
    }
}

fun Context.findAndroidActivity(): Activity? {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    return null
}

fun getLinkIdFromUrl(url: String): String {
    return url.split("/").last()
}

suspend fun getInstallReferrerClient(context: Context) = suspendCoroutine {
    val referrerClient = InstallReferrerClient.newBuilder(context).build()
    referrerClient.startConnection(object : InstallReferrerStateListener {
        override fun onInstallReferrerSetupFinished(responseCode: Int) {
            when (responseCode) {
                InstallReferrerClient.InstallReferrerResponse.OK -> {
                    // Connection established.
                    it.resume(referrerClient)
                }

                else -> {
                    it.resume(null)
                }
            }
        }

        override fun onInstallReferrerServiceDisconnected() {}
    })
}