package com.no5ing.bbibbi.presentation.feature.view_controller

import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.navArgument
import com.no5ing.bbibbi.BuildConfig
import com.no5ing.bbibbi.presentation.feature.view.main.change_nickname.ChangeNicknamePage
import com.no5ing.bbibbi.presentation.feature.view.main.setting_home.SettingHomePage
import com.no5ing.bbibbi.presentation.feature.view.main.quit.QuitPage
import com.no5ing.bbibbi.presentation.feature.view.main.webview.WebViewPage
import com.no5ing.bbibbi.util.forceRestart

object SettingDestination : NavigationDestination(
    route = settingHomePageRoute,
) {
    @Composable
    override fun Render(navController: NavHostController, backStackEntry: NavBackStackEntry) {
        SettingHomePage(
            onDispose = {
                navController.popBackStack()
            },
            onLogout = {
                navController.context.forceRestart()
            },
            onQuit = {
                navController.navigate(
                    QuitDestination
                )
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
            },
            onFamilyQuitCompleted = {
                navController.popAll()
                navController.navigate(
                    LandingJoinFamilyDestination
                )
            }
        )
    }
}

object ChangeNicknameDestination : NavigationDestination(
    route = settingNickNameRoute,
) {
    @Composable
    override fun Render(navController: NavHostController, backStackEntry: NavBackStackEntry) {
        ChangeNicknamePage(
            onDispose = {
                navController.popBackStack()
            }
        )
    }
}

object WebViewDestination : NavigationDestination(
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
}

object QuitDestination : NavigationDestination(
    route = settingQuitPageRoute,
) {
    @Composable
    override fun Render(navController: NavHostController, backStackEntry: NavBackStackEntry) {
        QuitPage(
            onDispose = {
                navController.popBackStack()
            },
            onQuitSuccess = {
                navController.context.forceRestart()
            }
        )
    }
}