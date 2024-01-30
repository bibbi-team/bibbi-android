package com.no5ing.bbibbi.presentation.feature.view_controller.landing

import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import com.no5ing.bbibbi.presentation.feature.view.landing.onboarding.OnBoardingPage
import com.no5ing.bbibbi.presentation.feature.view_controller.NavigationDestination
import com.no5ing.bbibbi.presentation.feature.view_controller.landing.JoinFamilyPageController.goJoinFamilyPage
import com.no5ing.bbibbi.presentation.feature.view_controller.main.HomePageController.goHomePage

object OnBoardingPageController : NavigationDestination(
    route = landingOnBoardingRoute,
) {
    @Composable
    override fun Render(navController: NavHostController, backStackEntry: NavBackStackEntry) {
        OnBoardingPage(
            onAlreadyHaveFamily = {
                navController.popAll()
                navController.goHomePage()
            },
            onFamilyNotExists = {
                navController.goJoinFamilyPage()
            }
        )
    }

    fun NavHostController.goOnBoardingPage() {
        navigate(OnBoardingPageController)
    }
}
