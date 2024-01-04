package com.no5ing.bbibbi.presentation.ui.feature.setting.webview

import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.viewinterop.AndroidView
import kotlinx.coroutines.launch
import timber.log.Timber

@Composable
fun WebViewPage(
    webViewUrl: String,
    onDispose: () -> Unit,
) {
    val backgroundColor = MaterialTheme.colorScheme.background.toArgb()
    val scope = rememberCoroutineScope()
    Surface(
        modifier = Modifier
            .fillMaxSize()
    ) {
        AndroidView(
            factory = { context ->
                WebView(context).apply {
                    settings.javaScriptEnabled = true
                    webViewClient = WebViewClient()
                    addJavascriptInterface(
                        object {
                            @android.webkit.JavascriptInterface
                            fun goBack() {
                                Timber.d("okh")
                                scope.launch {
                                    onDispose()
                                }
                            }
                        },
                        "android"
                    )
                    settings.loadWithOverviewMode = true
                    settings.useWideViewPort = true
                    settings.setSupportZoom(false)
                    setBackgroundColor(backgroundColor)
                }
            },
            update = { webView ->
                webView.loadUrl(webViewUrl)
            }
        )

    }

}