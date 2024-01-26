package com.no5ing.bbibbi.presentation.feature.view_controller.main

import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import com.no5ing.bbibbi.BuildConfig
import com.no5ing.bbibbi.presentation.feature.view.main.setting_home.SettingHomePage
import com.no5ing.bbibbi.presentation.feature.view_controller.NavigationDestination
import com.no5ing.bbibbi.presentation.feature.view_controller.landing.JoinFamilyPageController
import com.no5ing.bbibbi.util.forceRestart

object SettingHomePageController : NavigationDestination(
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
                    QuitPageController
                )
            },
            onPrivacy = {
                navController.navigate(
                    WebViewPageController,
                    params = listOf(
                        "webViewUrl" to BuildConfig.privacyUrl
                    )
                )
            },
            onTerm = {
                navController.navigate(
                    WebViewPageController,
                    params = listOf(
                        "webViewUrl" to BuildConfig.termUrl
                    )
                )
            },
            onFamilyQuitCompleted = {
                navController.popAll()
                navController.navigate(
                    JoinFamilyPageController
                )
            }
        )
    }
}