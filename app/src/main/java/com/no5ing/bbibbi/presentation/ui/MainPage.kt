package com.no5ing.bbibbi.presentation.ui

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.no5ing.bbibbi.presentation.ui.navigation.destination.CameraViewDestination
import com.no5ing.bbibbi.presentation.ui.navigation.destination.NavigationDestination
import com.no5ing.bbibbi.presentation.ui.navigation.destination.NavigationDestination.Companion.composable
import com.no5ing.bbibbi.presentation.ui.navigation.graph.landingGraph
import com.no5ing.bbibbi.presentation.ui.navigation.graph.mainGraph
import com.no5ing.bbibbi.presentation.ui.navigation.graph.postGraph
import com.no5ing.bbibbi.presentation.ui.navigation.graph.registerGraph
import com.no5ing.bbibbi.presentation.ui.navigation.graph.settingGraph

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MainPage(
    snackBarHostState: SnackbarHostState,
    navController: NavHostController,
    isAlreadyLoggedIn: Boolean,
) {
    Scaffold(
        snackbarHost = {
            CustomSnackBarHost(
                hostState = snackBarHostState
            )
        }
    ) { innerPadding ->
        Box(
            Modifier.padding(
                start = innerPadding.calculateStartPadding(
                    LayoutDirection.Ltr
                ),
                top = 0.dp,
                end = innerPadding.calculateEndPadding(LayoutDirection.Ltr)
            )
        ) {
            NavHost(
                navController = navController,
                startDestination = if (isAlreadyLoggedIn)
                    NavigationDestination.mainPageRoute
                else
                    NavigationDestination.landingPageRoute,
                enterTransition = { fadeIn(animationSpec = tween(250)) },
                exitTransition = { fadeOut(animationSpec = tween(200)) },
            ) {
                landingGraph(
                    navController = navController,
                )
                registerGraph(
                    navController = navController,
                )
                mainGraph(
                    navController = navController,
                )
                settingGraph(
                    navController = navController,
                )
                postGraph(
                    navController = navController,
                )
                composable(
                    controller = navController,
                    destination = CameraViewDestination,
                    enterTransition = {
                        slideInVertically {
                            it
                        }
                    },
                    popExitTransition = {
                        slideOutVertically {
                            it
                        }
                    }
                )
            }
        }
    }
}