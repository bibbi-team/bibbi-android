package com.no5ing.bbibbi.presentation.ui.navigation.graph

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Stable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.google.accompanist.navigation.animation.navigation
import com.no5ing.bbibbi.presentation.ui.navigation.destination.ChangeNicknameDestination
import com.no5ing.bbibbi.presentation.ui.navigation.destination.NavigationDestination.Companion.composable
import com.no5ing.bbibbi.presentation.ui.navigation.destination.NavigationDestination.Companion.settingPageRoute
import com.no5ing.bbibbi.presentation.ui.navigation.destination.SettingDestination
import com.no5ing.bbibbi.presentation.ui.navigation.destination.WebViewDestination

@OptIn(ExperimentalAnimationApi::class)
@Stable
fun NavGraphBuilder.settingGraph(
    navController: NavHostController,
) {
    navigation(
        startDestination = SettingDestination.route,
        route = settingPageRoute,
    ) {
        composable(
            controller = navController,
            destination = SettingDestination,
            enterTransition = {
                slideInHorizontally {
                    it
                }
            },
            popExitTransition = {
                slideOutHorizontally {
                    it
                }
            }
        )
        composable(
            controller = navController,
            destination = ChangeNicknameDestination,
        )
        composable(
            controller = navController,
            destination = WebViewDestination,
            enterTransition = {
                slideInHorizontally {
                    it
                }
            },
            popExitTransition = {
                slideOutHorizontally {
                    it
                }
            }
        )
    }
}