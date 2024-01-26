package com.no5ing.bbibbi.presentation.ui.navigation.graph

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Stable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.navigation
import com.no5ing.bbibbi.presentation.feature.view_controller.NavigationDestination.Companion.composable
import com.no5ing.bbibbi.presentation.feature.view_controller.NavigationDestination.Companion.registerPageRoute
import com.no5ing.bbibbi.presentation.feature.view_controller.RegisterDayOfBirthDestination
import com.no5ing.bbibbi.presentation.feature.view_controller.RegisterNicknameDestination
import com.no5ing.bbibbi.presentation.feature.view_controller.RegisterProfileImageDestination

@Stable
fun NavGraphBuilder.registerGraph(
    navController: NavHostController,
) {
    navigation(
        startDestination = RegisterNicknameDestination.route,
        route = registerPageRoute,
    ) {
        composable(
            controller = navController,
            destination = RegisterNicknameDestination,
            exitTransition = {
                val destination = targetState.destination.route ?: ""
                when {
                    destination.startsWith(RegisterDayOfBirthDestination.route) -> slideOutHorizontally { -it }
                    else -> null
                }
            },
        )
        composable(
            controller = navController,
            destination = RegisterDayOfBirthDestination,
            enterTransition = {
                when (initialState.destination.route) {
                    RegisterNicknameDestination.route -> slideInHorizontally { it }
                    else -> null
                }
            },
            exitTransition = {
                val destination = targetState.destination.route ?: ""
                when {
                    destination.startsWith(RegisterProfileImageDestination.route) -> slideOutHorizontally { -it }
                    else -> null
                }
            }
        )
        composable(
            controller = navController,
            destination = RegisterProfileImageDestination,
            enterTransition = {
                val initial = initialState.destination.route ?: ""
                when {
                    initial.startsWith(RegisterDayOfBirthDestination.route) -> slideInHorizontally { it }
                    else -> null
                }
            },
        )
    }
}