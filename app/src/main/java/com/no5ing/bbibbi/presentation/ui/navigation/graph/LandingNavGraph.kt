package com.no5ing.bbibbi.presentation.ui.navigation.graph

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Stable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.google.accompanist.navigation.animation.navigation
import com.no5ing.bbibbi.presentation.ui.navigation.destination.LandingAlreadyFamilyExistsDestination

import com.no5ing.bbibbi.presentation.ui.navigation.destination.LandingLoginDestination
import com.no5ing.bbibbi.presentation.ui.navigation.destination.LandingOnBoardingDestination
import com.no5ing.bbibbi.presentation.ui.navigation.destination.NavigationDestination.Companion.composable
import com.no5ing.bbibbi.presentation.ui.navigation.destination.NavigationDestination.Companion.landingPageRoute

@OptIn(ExperimentalAnimationApi::class)
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
    }
}