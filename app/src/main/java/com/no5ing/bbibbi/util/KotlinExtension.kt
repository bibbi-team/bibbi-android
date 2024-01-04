package com.no5ing.bbibbi.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.PermissionStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import timber.log.Timber
import kotlin.coroutines.CoroutineContext

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

fun Context.forceRestart() {
    if (this is Activity) {
        Timber.d("Restarting!!")
        this.finish()
        this.startActivity(this.intent)
    }
}

enum class CustomDialogPosition {
    BOTTOM, TOP
}

fun Modifier.customDialogModifier(pos: CustomDialogPosition) = layout { measurable, constraints ->

    val placeable = measurable.measure(constraints);
    layout(constraints.maxWidth, constraints.maxHeight){
        when(pos) {
            CustomDialogPosition.BOTTOM -> {
                placeable.place(0, constraints.maxHeight - placeable.height, 10f)
            }
            CustomDialogPosition.TOP -> {
                placeable.place(0,0,10f)
            }
        }
    }
}