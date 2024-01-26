package com.no5ing.bbibbi.presentation.feature.view_controller.main

import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import com.no5ing.bbibbi.presentation.feature.view.main.quit.QuitPage
import com.no5ing.bbibbi.presentation.feature.view_controller.NavigationDestination
import com.no5ing.bbibbi.util.forceRestart

object QuitPageController : NavigationDestination(
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