package com.no5ing.bbibbi.util

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.DisplayMetrics
import android.view.Window
import android.view.WindowManager
import androidx.browser.customtabs.CustomTabsIntent
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
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
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.MediaType
import okhttp3.RequestBody
import okio.BufferedSink
import timber.log.Timber
import java.io.ByteArrayOutputStream
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.streams.toList

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

suspend fun ImageCapture.takePhotoWithImage(context: Context, requiredFlip: Boolean): Uri? =
    suspendCoroutine { continuation ->
        this.takePicture(
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageCapturedCallback() {
                override fun onError(e: ImageCaptureException) {
                    Timber.e("[CameraView] photo capture failed", e)
                    continuation.resume(null)
                }

                override fun onCaptureSuccess(image: ImageProxy) {
                    val fileName = "${System.currentTimeMillis()}.jpg"
                    val faos = context.openFileOutput(fileName, Context.MODE_PRIVATE)
                    val bitmap = if (!requiredFlip)
                        image.toBitmap()
                            .rotateWithCropCenterRecycle(image.imageInfo.rotationDegrees)
                    else
                        image.toBitmap()
                            .rotateWithCropCenterRecycle(image.imageInfo.rotationDegrees).flip()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, faos)
                    faos.close()
                    continuation.resume(Uri.fromFile(context.getFileStreamPath(fileName)))
                }
            }
        )
    }

suspend fun ImageCapture.takePhotoTemporary(context: Context): ImageProxy? =
    suspendCoroutine { continuation ->
        this.takePicture(
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageCapturedCallback() {
                override fun onError(e: ImageCaptureException) {
                    Timber.e("[CameraView] photo capture failed", e)
                    continuation.resume(null)
                }

                override fun onCaptureSuccess(image: ImageProxy) {
                    continuation.resume(image)
                }
            }
        )
    }

fun Bitmap.rotateWithCropCenter(degrees: Int): Bitmap {
    val matrix = Matrix().apply { postRotate(degrees.toFloat()) }
    val newBitMap = if (width >= height) {
        Bitmap.createBitmap(this, width / 2 - height / 2, 0, height, height, matrix, true)
    } else {
        Bitmap.createBitmap(this, 0, height / 2 - width / 2, width, width, matrix, true)
    }
    return newBitMap
}

fun Bitmap.rotateWithCropCenterRecycle(degrees: Int): Bitmap {
    val matrix = Matrix().apply { postRotate(degrees.toFloat()) }
    val newBitMap = if (width >= height) {
        Bitmap.createBitmap(this, width / 2 - height / 2, 0, height, height, matrix, true)
    } else {
        Bitmap.createBitmap(this, 0, height / 2 - width / 2, width, width, matrix, true)
    }
    this.recycle()
    return newBitMap
}

fun Bitmap.flip(): Bitmap {
    val matrix = Matrix().apply { preScale(-1.0f, 1f) }
    return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)

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

fun Context.exit() {
    if (this is Activity) {
        Timber.d("[Activity] Exiting application...")
        this.finishAffinity()
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


fun getLinkIdFromUrl(url: String): String {
    return url.split("/").last()
}

suspend fun getInstallReferrerClient(context: Context) = suspendCancellableCoroutine {
    val referrerClient = InstallReferrerClient.newBuilder(context).build()
    referrerClient.startConnection(object : InstallReferrerStateListener {
        override fun onInstallReferrerSetupFinished(responseCode: Int) {
            when (responseCode) {
                InstallReferrerClient.InstallReferrerResponse.OK -> {
                    // Connection established.
                    if (it.isActive)
                        it.resume(referrerClient)
                }

                else -> {
                    if (it.isActive)
                        it.resume(null)
                }
            }
        }

        override fun onInstallReferrerServiceDisconnected() {
            if (it.isActive)
                it.resume(null)
        }
    })
}

fun String.toCodePointList() = codePoints().toList().map { String(Character.toChars(it)) }
fun String.codePointLength() = codePoints().count()

fun randomBoolean() = (0..1).random() == 1

fun gapUntilNext(): Long {
    val current = LocalDateTime.now()
    if (current.hour < 10)
        return -1
    val tomorrow = LocalDateTime
        .of(current.year, current.month, current.dayOfMonth, 0, 0, 0)
        .plusDays(1)
    return current.until(tomorrow, ChronoUnit.SECONDS)
}

@Composable
fun getAdView(): AdView {
    val localContext = LocalContext.current
    return remember {
        AdView(localContext).apply {
            this.setAdSize(AdSize.BANNER)
            this.adUnitId = "ca-app-pub-7835112884789455/9259792746"
            loadAd(AdRequest.Builder().build())
        }
    }
}
