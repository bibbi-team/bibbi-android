package com.no5ing.bbibbi.presentation.feature.view_controller.main

import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import com.no5ing.bbibbi.presentation.feature.view.main.change_nickname.ChangeNicknamePage
import com.no5ing.bbibbi.presentation.feature.view_controller.NavigationDestination

object ChangeNickNamePageController : NavigationDestination(
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

    fun NavHostController.goChangeNickNamePage() {
        navigate(ChangeNickNamePageController)
    }
}