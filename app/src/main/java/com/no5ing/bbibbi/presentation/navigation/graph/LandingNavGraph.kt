package com.no5ing.bbibbi.presentation.navigation.graph

import androidx.compose.runtime.Stable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.navigation
import com.no5ing.bbibbi.presentation.feature.view_controller.landing.AlreadyFamilyExistsPageController
import com.no5ing.bbibbi.presentation.feature.view_controller.landing.JoinFamilyPageController
import com.no5ing.bbibbi.presentation.feature.view_controller.landing.JoinFamilyWithLinkPageController
import com.no5ing.bbibbi.presentation.feature.view_controller.landing.LoginPageController
import com.no5ing.bbibbi.presentation.feature.view_controller.landing.OnBoardingPageController
import com.no5ing.bbibbi.presentation.feature.view_controller.NavigationDestination.Companion.composable
import com.no5ing.bbibbi.presentation.feature.view_controller.NavigationDestination.Companion.landingPageRoute

@Stable
fun NavGraphBuilder.landingGraph(
    navController: NavHostController,
) {
    navigation(
        startDestination = LoginPageController.route,
        route = landingPageRoute,
    ) {
        composable(
            controller = navController,
            destination = LoginPageController,
        )
        composable(
            controller = navController,
            destination = OnBoardingPageController,
        )
        composable(
            controller = navController,
            destination = AlreadyFamilyExistsPageController,
        )
        composable(
            controller = navController,
            destination = JoinFamilyPageController,
        )
        composable(
            controller = navController,
            destination = JoinFamilyWithLinkPageController,
        )
    }
}