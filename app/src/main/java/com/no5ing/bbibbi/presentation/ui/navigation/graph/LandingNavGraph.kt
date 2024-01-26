package com.no5ing.bbibbi.presentation.ui.navigation.graph

import androidx.compose.runtime.Stable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.navigation
import com.no5ing.bbibbi.presentation.feature.view_controller.LandingAlreadyFamilyExistsDestination
import com.no5ing.bbibbi.presentation.feature.view_controller.LandingJoinFamilyDestination
import com.no5ing.bbibbi.presentation.feature.view_controller.LandingJoinFamilyWithLinkDestination
import com.no5ing.bbibbi.presentation.feature.view_controller.LandingLoginDestination
import com.no5ing.bbibbi.presentation.feature.view_controller.LandingOnBoardingDestination
import com.no5ing.bbibbi.presentation.feature.view_controller.NavigationDestination.Companion.composable
import com.no5ing.bbibbi.presentation.feature.view_controller.NavigationDestination.Companion.landingPageRoute

@Stable
fun NavGraphBuilder.landingGraph(
    navController: NavHostController,
) {
    navigation(
        startDestination = LandingLoginDestination.route,
        route = landingPageRoute,
    ) {
        composable(
            controller = navController,
            destination = LandingLoginDestination,
        )
        composable(
            controller = navController,
            destination = LandingOnBoardingDestination,
        )
        composable(
            controller = navController,
            destination = LandingAlreadyFamilyExistsDestination,
        )
        composable(
            controller = navController,
            destination = LandingJoinFamilyDestination,
        )
        composable(
            controller = navController,
            destination = LandingJoinFamilyWithLinkDestination,
        )
    }
}