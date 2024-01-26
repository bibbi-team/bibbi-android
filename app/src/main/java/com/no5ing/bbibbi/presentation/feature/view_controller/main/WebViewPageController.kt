package com.no5ing.bbibbi.presentation.feature.view_controller.main

import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.navArgument
import com.no5ing.bbibbi.presentation.feature.view.main.webview.WebViewPage
import com.no5ing.bbibbi.presentation.feature.view_controller.NavigationDestination

object WebViewPageController : NavigationDestination(
    route = settingWebViewPageRoute,
    arguments = listOf(navArgument("webViewUrl") {}),
) {
    @Composable
    override fun Render(navController: NavHostController, backStackEntry: NavBackStackEntry) {
        WebViewPage(
            onDispose = {
                navController.popBackStack()
            },
            webViewUrl = backStackEntry.arguments?.getString("webViewUrl")
                ?: throw IllegalArgumentException("webViewUrl is null")
        )
    }

    fun NavHostController.goWebViewPage(webViewUrl: String) {
        navigate(WebViewPageController, params = listOf("webViewUrl" to webViewUrl))
    }
}