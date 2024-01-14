package com.no5ing.bbibbi.presentation.ui.navigation.destination

import androidx.navigation.navArgument
import com.no5ing.bbibbi.BuildConfig
import com.no5ing.bbibbi.presentation.ui.feature.setting.change_nickname.ChangeNicknamePage
import com.no5ing.bbibbi.presentation.ui.feature.setting.home.SettingHomePage
import com.no5ing.bbibbi.presentation.ui.feature.setting.webview.WebViewPage
import com.no5ing.bbibbi.util.forceRestart

object SettingDestination : NavigationDestination(
    route = settingHomePageRoute,
    content = { navController, _ ->
        SettingHomePage(
            onDispose = {
                navController.popBackStack()
            },
            onLogout = {
                navController.context.forceRestart()
            },
            onQuitCompleted = {
                navController.context.forceRestart()
            },
            onPrivacy = {
                navController.navigate(
                    WebViewDestination,
                    params = listOf(
                        "webViewUrl" to BuildConfig.privacyUrl
                    )
                )
            },
            onTerm = {
                navController.navigate(
                    WebViewDestination,
                    params = listOf(
                        "webViewUrl" to BuildConfig.termUrl
                    )
                )
            }
        )
    }
)

object ChangeNicknameDestination : NavigationDestination(
    route = settingNickNameRoute,
    content = { navController, _ ->
        ChangeNicknamePage(
            onDispose = {
                navController.popBackStack()
            }
        )
    }
)

object WebViewDestination : NavigationDestination(
    route = settingWebViewPageRoute,
    arguments = listOf(navArgument("webViewUrl") {}),
    content = { navController, backStackEntry ->
        WebViewPage(
            onDispose = {
                navController.popBackStack()
            },
            webViewUrl = backStackEntry.arguments?.getString("webViewUrl")
                ?: throw IllegalArgumentException("webViewUrl is null")
        )

    }
)