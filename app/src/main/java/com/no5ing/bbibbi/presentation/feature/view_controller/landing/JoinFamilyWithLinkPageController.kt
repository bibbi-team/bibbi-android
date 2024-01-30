package com.no5ing.bbibbi.presentation.feature.view_controller.landing

import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import com.no5ing.bbibbi.presentation.feature.view.landing.join_family_with_link.JoinFamilyWithLinkPage
import com.no5ing.bbibbi.presentation.feature.view_controller.NavigationDestination
import com.no5ing.bbibbi.presentation.feature.view_controller.main.HomePageController.goHomePage

object JoinFamilyWithLinkPageController : NavigationDestination(
    route = landingJoinFamilyWithLinkRoute,
) {
    @Composable
    override fun Render(navController: NavHostController, backStackEntry: NavBackStackEntry) {
        JoinFamilyWithLinkPage(
            onDispose = {
                navController.popBackStack()
            },
            onJoinComplete = {
                navController.popAll()
                navController.goHomePage()
            }
        )
    }

    fun NavHostController.goJoinFamilyWithLinkPage() {
        navigate(JoinFamilyWithLinkPageController)
    }
}