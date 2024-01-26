package com.no5ing.bbibbi.presentation.navigation.graph

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Stable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.navigation
import com.no5ing.bbibbi.presentation.feature.view_controller.NavigationDestination.Companion.composable
import com.no5ing.bbibbi.presentation.feature.view_controller.NavigationDestination.Companion.registerPageRoute
import com.no5ing.bbibbi.presentation.feature.view_controller.register.RegisterDayOfBirthPageController
import com.no5ing.bbibbi.presentation.feature.view_controller.register.RegisterNicknamePageController
import com.no5ing.bbibbi.presentation.feature.view_controller.register.RegisterProfileImagePageController

@Stable
fun NavGraphBuilder.registerGraph(
    navController: NavHostController,
) {
    navigation(
        startDestination = RegisterNicknamePageController.route,
        route = registerPageRoute,
    ) {
        composable(
            controller = navController,
            destination = RegisterNicknamePageController,
            exitTransition = {
                val destination = targetState.destination.route ?: ""
                when {
                    destination.startsWith(RegisterDayOfBirthPageController.route) -> slideOutHorizontally { -it }
                    else -> null
                }
            },
        )
        composable(
            controller = navController,
            destination = RegisterDayOfBirthPageController,
            enterTransition = {
                when (initialState.destination.route) {
                    RegisterNicknamePageController.route -> slideInHorizontally { it }
                    else -> null
                }
            },
            exitTransition = {
                val destination = targetState.destination.route ?: ""
                when {
                    destination.startsWith(RegisterProfileImagePageController.route) -> slideOutHorizontally { -it }
                    else -> null
                }
            }
        )
        composable(
            controller = navController,
            destination = RegisterProfileImagePageController,
            enterTransition = {
                val initial = initialState.destination.route ?: ""
                when {
                    initial.startsWith(RegisterDayOfBirthPageController.route) -> slideInHorizontally { it }
                    else -> null
                }
            },
        )
    }
}