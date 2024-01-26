package com.no5ing.bbibbi.presentation.feature

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
import com.no5ing.bbibbi.presentation.navigation.animation.defaultFadeIn
import com.no5ing.bbibbi.presentation.navigation.animation.defaultFadeOut
import com.no5ing.bbibbi.presentation.feature.view_controller.CameraViewDestination
import com.no5ing.bbibbi.presentation.feature.view_controller.NavigationDestination
import com.no5ing.bbibbi.presentation.feature.view_controller.NavigationDestination.Companion.composable
import com.no5ing.bbibbi.presentation.component.CustomSnackBarHost
import com.no5ing.bbibbi.presentation.navigation.graph.landingGraph
import com.no5ing.bbibbi.presentation.navigation.graph.mainGraph
import com.no5ing.bbibbi.presentation.navigation.graph.registerGraph

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
                enterTransition = { defaultFadeIn() },
                exitTransition = { defaultFadeOut() },
            ) {
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
                landingGraph(
                    navController = navController,
                )
                registerGraph(
                    navController = navController,
                )
                mainGraph(
                    navController = navController,
                )
            }
        }
    }
}