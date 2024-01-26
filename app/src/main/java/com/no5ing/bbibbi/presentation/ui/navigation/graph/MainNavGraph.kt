package com.no5ing.bbibbi.presentation.ui.navigation.graph

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Stable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.navigation
import com.no5ing.bbibbi.presentation.ui.navigation.animation.fullHorizontalSlideInToLeft
import com.no5ing.bbibbi.presentation.ui.navigation.animation.fullHorizontalSlideInToRight
import com.no5ing.bbibbi.presentation.ui.navigation.animation.fullHorizontalSlideOutToLeft
import com.no5ing.bbibbi.presentation.ui.navigation.animation.fullHorizontalSlideOutToRight
import com.no5ing.bbibbi.presentation.ui.navigation.animation.fullSlideInVertically
import com.no5ing.bbibbi.presentation.ui.navigation.animation.fullSlideOutVertically
import com.no5ing.bbibbi.presentation.ui.navigation.destination.ChangeNicknameDestination
import com.no5ing.bbibbi.presentation.ui.navigation.destination.CreateRealEmojiDestination
import com.no5ing.bbibbi.presentation.ui.navigation.destination.MainCalendarDestination
import com.no5ing.bbibbi.presentation.ui.navigation.destination.MainCalendarDetailDestination
import com.no5ing.bbibbi.presentation.ui.navigation.destination.MainFamilyDestination
import com.no5ing.bbibbi.presentation.ui.navigation.destination.MainHomeDestination
import com.no5ing.bbibbi.presentation.ui.navigation.destination.MainProfileDestination
import com.no5ing.bbibbi.presentation.ui.navigation.destination.NavigationDestination
import com.no5ing.bbibbi.presentation.ui.navigation.destination.NavigationDestination.Companion.composable
import com.no5ing.bbibbi.presentation.ui.navigation.destination.NavigationDestination.Companion.mainPageRoute
import com.no5ing.bbibbi.presentation.ui.navigation.destination.PostReUploadDestination
import com.no5ing.bbibbi.presentation.ui.navigation.destination.PostUploadDestination
import com.no5ing.bbibbi.presentation.ui.navigation.destination.PostViewDestination
import com.no5ing.bbibbi.presentation.ui.navigation.destination.QuitDestination
import com.no5ing.bbibbi.presentation.ui.navigation.destination.SettingDestination
import com.no5ing.bbibbi.presentation.ui.navigation.destination.WebViewDestination

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
                if (destination == MainFamilyDestination.route) fullHorizontalSlideOutToRight()
                else if (destination.startsWith(MainProfileDestination.route)) fadeOut()
                else fullHorizontalSlideOutToLeft()
            },
            popEnterTransition = {
                val initial = initialState.destination.route ?: ""
                if (initial == MainFamilyDestination.route) fullHorizontalSlideInToLeft()
                else if (initial.startsWith(MainProfileDestination.route)) fadeIn()
                else fullHorizontalSlideInToRight()
            }
        )
        composable(
            controller = navController,
            destination = MainProfileDestination,
            enterTransition = {
                fullSlideInVertically()
            },
            popExitTransition = {
                fullSlideOutVertically()
            }
        )
        composable(
            controller = navController,
            destination = MainFamilyDestination,
            enterTransition = {
                fullHorizontalSlideInToRight()
            },
            popExitTransition = {
                fullHorizontalSlideOutToLeft()
            }
        )
        composable(
            controller = navController,
            destination = MainCalendarDestination,
            enterTransition = {
                fullHorizontalSlideInToLeft()
            },
            popExitTransition = {
                fullHorizontalSlideOutToRight()
            }
        )
        composable(
            controller = navController,
            destination = MainCalendarDetailDestination,
            enterTransition = {
                fullHorizontalSlideInToLeft()
            },
            popExitTransition = {
                fullHorizontalSlideOutToRight()
            }
        )
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
        composable(
            controller = navController,
            destination = PostReUploadDestination
        )
        composable(
            controller = navController,
            destination = CreateRealEmojiDestination
        )
        composable(
            controller = navController,
            destination = SettingDestination,
            enterTransition = {
                fullHorizontalSlideInToLeft()
            },
            popExitTransition = {
                fullHorizontalSlideOutToRight()
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
                fullHorizontalSlideInToLeft()
            },
            popExitTransition = {
                fullHorizontalSlideOutToRight()
            }
        )
        composable(
            controller = navController,
            destination = QuitDestination,
        )
    }
}