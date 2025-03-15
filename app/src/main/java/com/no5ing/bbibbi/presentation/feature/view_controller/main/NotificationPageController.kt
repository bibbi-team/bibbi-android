package com.no5ing.bbibbi.presentation.feature.view_controller.main

import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import com.no5ing.bbibbi.presentation.feature.view.main.notification.NotificationPage
import com.no5ing.bbibbi.presentation.feature.view_controller.NavigationDestination

object NotificationPageController : NavigationDestination(
    route = mainNotificationPageRoute,
) {
    @Composable
    override fun Render(navController: NavHostController, backStackEntry: NavBackStackEntry) {
        NotificationPage(
            onDispose = {
                navController.popBackStack()
            },
            onTapNotification = {
                navController.navigateUnsafeDeepLink(it.aosDeepLink)
            }
        )
    }

    fun NavHostController.goNotificationPage() {
        navigate(NotificationPageController)
    }
}
