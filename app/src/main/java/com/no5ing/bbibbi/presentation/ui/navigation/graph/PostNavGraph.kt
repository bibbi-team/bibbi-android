package com.no5ing.bbibbi.presentation.ui.navigation.graph

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Stable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.google.accompanist.navigation.animation.navigation
import com.no5ing.bbibbi.presentation.ui.navigation.destination.NavigationDestination.Companion.composable
import com.no5ing.bbibbi.presentation.ui.navigation.destination.NavigationDestination.Companion.postPageRoute
import com.no5ing.bbibbi.presentation.ui.navigation.destination.NavigationDestination.Companion.postViewPageRoute
import com.no5ing.bbibbi.presentation.ui.navigation.destination.PostUploadDestination
import com.no5ing.bbibbi.presentation.ui.navigation.destination.PostViewDestination

@OptIn(ExperimentalAnimationApi::class)
@Stable
fun NavGraphBuilder.postGraph(
    navController: NavHostController,
) {
    navigation(
        startDestination = postViewPageRoute,
        route = postPageRoute,
    ) {
        composable(
            controller = navController,
            destination = PostViewDestination,
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
            destination = PostUploadDestination
        )
    }
}