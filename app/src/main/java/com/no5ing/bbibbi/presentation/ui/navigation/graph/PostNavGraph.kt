package com.no5ing.bbibbi.presentation.ui.navigation.graph

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Stable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.google.accompanist.navigation.animation.navigation
import com.no5ing.bbibbi.presentation.ui.navigation.animation.fullHorizontalSlideInToLeft
import com.no5ing.bbibbi.presentation.ui.navigation.animation.fullHorizontalSlideOutToRight
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
                fullHorizontalSlideInToLeft()
            },
            popExitTransition = {
                fullHorizontalSlideOutToRight()
            }
        )
        composable(
            controller = navController,
            destination = PostUploadDestination
        )
    }
}