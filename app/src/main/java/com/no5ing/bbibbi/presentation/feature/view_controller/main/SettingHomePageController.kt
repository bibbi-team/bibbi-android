package com.no5ing.bbibbi.presentation.feature.view_controller.main

import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import com.no5ing.bbibbi.BuildConfig
import com.no5ing.bbibbi.presentation.feature.view.main.setting_home.SettingHomePage
import com.no5ing.bbibbi.presentation.feature.view_controller.NavigationDestination
import com.no5ing.bbibbi.presentation.feature.view_controller.landing.JoinFamilyPageController.goJoinFamilyPage
import com.no5ing.bbibbi.presentation.feature.view_controller.main.QuitPageController.goQuitPage
import com.no5ing.bbibbi.presentation.feature.view_controller.main.WebViewPageController.goWebViewPage
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
            onLogOutCompleted = {
                navController.context.forceRestart()
            },
            onQuit = {
                navController.goQuitPage()
            },
            onPrivacy = {
                navController.goWebViewPage(BuildConfig.privacyUrl)
            },
            onTerm = {
                navController.goWebViewPage(BuildConfig.termUrl)
            },
            onFamilyQuitCompleted = {
                navController.popAll()
                navController.goJoinFamilyPage()
            }
        )
    }

    fun NavHostController.goSettingHomePage() {
        navigate(SettingHomePageController)
    }
}