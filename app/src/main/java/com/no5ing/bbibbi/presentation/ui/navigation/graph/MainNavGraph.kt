package com.no5ing.bbibbi.presentation.ui.navigation.graph

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Stable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.google.accompanist.navigation.animation.navigation
import com.no5ing.bbibbi.presentation.ui.navigation.destination.MainCalendarDestination
import com.no5ing.bbibbi.presentation.ui.navigation.destination.MainCalendarDetailDestination
import com.no5ing.bbibbi.presentation.ui.navigation.destination.MainFamilyDestination
import com.no5ing.bbibbi.presentation.ui.navigation.destination.MainHomeDestination
import com.no5ing.bbibbi.presentation.ui.navigation.destination.MainProfileDestination
import com.no5ing.bbibbi.presentation.ui.navigation.destination.NavigationDestination.Companion.composable
import com.no5ing.bbibbi.presentation.ui.navigation.destination.NavigationDestination.Companion.mainPageRoute

@OptIn(ExperimentalAnimationApi::class)
@Stable
fun NavGraphBuilder.mainGraph(
    navController: NavHostController,
) {
    navigation(
        startDestination = MainHomeDestination.route,
        route = mainPageRoute,
    ) {
        composable(
            controller = navController,
            destination = MainHomeDestination,
            exitTransition = {
                val destination = targetState.destination.route ?: ""
                if (destination == MainFamilyDestination.route) {
                    slideOutHorizontally {
                        +it
                    }
                }
                else slideOutHorizontally {
                    -it
                }
            },
            popEnterTransition = {
                val initial = initialState.destination.route ?: ""
                if (initial == MainFamilyDestination.route) {
                    slideInHorizontally {
                        +it
                    }
                }
                else slideInHorizontally {
                    -it
                }
            }
        )
        composable(
            controller = navController,
            destination = MainProfileDestination,
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
        composable(
            controller = navController,
            destination = MainFamilyDestination,
            enterTransition = {
                slideInHorizontally {
                    -it
                }
            },
            popExitTransition = {
                slideOutHorizontally {
                    -it
                }
            }
        )
        composable(
            controller = navController,
            destination = MainCalendarDestination,
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
            destination = MainCalendarDetailDestination,
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