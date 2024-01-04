package com.no5ing.bbibbi.presentation.ui.navigation.graph

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Stable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.google.accompanist.navigation.animation.navigation
import com.no5ing.bbibbi.presentation.ui.navigation.destination.NavigationDestination.Companion.composable
import com.no5ing.bbibbi.presentation.ui.navigation.destination.NavigationDestination.Companion.registerPageRoute
import com.no5ing.bbibbi.presentation.ui.navigation.destination.RegisterDayOfBirthDestination
import com.no5ing.bbibbi.presentation.ui.navigation.destination.RegisterNicknameDestination
import com.no5ing.bbibbi.presentation.ui.navigation.destination.RegisterProfileImageDestination

@OptIn(ExperimentalAnimationApi::class)
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