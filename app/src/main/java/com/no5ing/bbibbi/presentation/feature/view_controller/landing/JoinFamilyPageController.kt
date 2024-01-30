package com.no5ing.bbibbi.presentation.feature.view_controller.landing

import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import com.no5ing.bbibbi.presentation.feature.view.landing.join_family.JoinFamilyPage
import com.no5ing.bbibbi.presentation.feature.view_controller.NavigationDestination
import com.no5ing.bbibbi.presentation.feature.view_controller.landing.JoinFamilyWithLinkPageController.goJoinFamilyWithLinkPage
import com.no5ing.bbibbi.presentation.feature.view_controller.main.HomePageController.goHomePage

object JoinFamilyPageController : NavigationDestination(
    route = landingJoinFamilyRoute,
) {
    @Composable
    override fun Render(navController: NavHostController, backStackEntry: NavBackStackEntry) {
        JoinFamilyPage(
            onTapJoinWithLink = {
                navController.goJoinFamilyWithLinkPage()
            },
            onFamilyCreationComplete = {
                navController.popAll()
                navController.goHomePage()
            }
        )
    }

    fun NavHostController.goJoinFamilyPage() {
        navigate(JoinFamilyPageController)
    }
}
