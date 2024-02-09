package com.no5ing.bbibbi.presentation.navigation.graph

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Stable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.navigation
import com.no5ing.bbibbi.presentation.feature.view_controller.NavigationDestination.Companion.composable
import com.no5ing.bbibbi.presentation.feature.view_controller.NavigationDestination.Companion.mainPageRoute
import com.no5ing.bbibbi.presentation.feature.view_controller.main.CalendarDetailPageController
import com.no5ing.bbibbi.presentation.feature.view_controller.main.CalendarPageController
import com.no5ing.bbibbi.presentation.feature.view_controller.main.ChangeNickNamePageController
import com.no5ing.bbibbi.presentation.feature.view_controller.main.CreateRealEmojiPageController
import com.no5ing.bbibbi.presentation.feature.view_controller.main.FamilyListPageController
import com.no5ing.bbibbi.presentation.feature.view_controller.main.HomePageController
import com.no5ing.bbibbi.presentation.feature.view_controller.main.ImagePreviewPageController
import com.no5ing.bbibbi.presentation.feature.view_controller.main.PostReUploadPageController
import com.no5ing.bbibbi.presentation.feature.view_controller.main.PostUploadPageController
import com.no5ing.bbibbi.presentation.feature.view_controller.main.PostViewPageController
import com.no5ing.bbibbi.presentation.feature.view_controller.main.ProfilePageController
import com.no5ing.bbibbi.presentation.feature.view_controller.main.QuitPageController
import com.no5ing.bbibbi.presentation.feature.view_controller.main.SettingHomePageController
import com.no5ing.bbibbi.presentation.feature.view_controller.main.WebViewPageController
import com.no5ing.bbibbi.presentation.navigation.animation.fullHorizontalSlideInToLeft
import com.no5ing.bbibbi.presentation.navigation.animation.fullHorizontalSlideInToRight
import com.no5ing.bbibbi.presentation.navigation.animation.fullHorizontalSlideOutToLeft
import com.no5ing.bbibbi.presentation.navigation.animation.fullHorizontalSlideOutToRight
import com.no5ing.bbibbi.presentation.navigation.animation.fullSlideInVertically
import com.no5ing.bbibbi.presentation.navigation.animation.fullSlideOutVertically

@Stable
fun NavGraphBuilder.mainGraph(
    navController: NavHostController,
) {
    navigation(
        startDestination = HomePageController.route,
        route = mainPageRoute,
    ) {
        composable(
            controller = navController,
            destination = HomePageController,
            exitTransition = {
                val destination = targetState.destination.route ?: ""
                if (destination == FamilyListPageController.route) fullHorizontalSlideOutToRight()
                else if (destination.startsWith(ProfilePageController.route)) fadeOut()
                else fullHorizontalSlideOutToLeft()
            },
            popEnterTransition = {
                val initial = initialState.destination.route ?: ""
                if (initial == FamilyListPageController.route) fullHorizontalSlideInToLeft()
                else if (initial.startsWith(ProfilePageController.route)) fadeIn()
                else fullHorizontalSlideInToRight()
            }
        )
        composable(
            controller = navController,
            destination = ProfilePageController,
            enterTransition = {
                fullSlideInVertically()
            },
            popExitTransition = {
                fullSlideOutVertically()
            }
        )
        composable(
            controller = navController,
            destination = FamilyListPageController,
            enterTransition = {
                fullHorizontalSlideInToRight()
            },
            popExitTransition = {
                fullHorizontalSlideOutToLeft()
            }
        )
        composable(
            controller = navController,
            destination = CalendarPageController,
            enterTransition = {
                fullHorizontalSlideInToLeft()
            },
            popExitTransition = {
                fullHorizontalSlideOutToRight()
            }
        )
        composable(
            controller = navController,
            destination = CalendarDetailPageController,
            enterTransition = {
                fullHorizontalSlideInToLeft()
            },
            popExitTransition = {
                fullHorizontalSlideOutToRight()
            }
        )
        composable(
            controller = navController,
            destination = PostViewPageController,
            enterTransition = {
                fullHorizontalSlideInToLeft()
            },
            popExitTransition = {
                fullHorizontalSlideOutToRight()
            }
        )
        composable(
            controller = navController,
            destination = PostUploadPageController
        )
        composable(
            controller = navController,
            destination = PostReUploadPageController
        )
        composable(
            controller = navController,
            destination = CreateRealEmojiPageController
        )
        composable(
            controller = navController,
            destination = ImagePreviewPageController
        )
        composable(
            controller = navController,
            destination = SettingHomePageController,
            enterTransition = {
                fullHorizontalSlideInToLeft()
            },
            popExitTransition = {
                fullHorizontalSlideOutToRight()
            }
        )
        composable(
            controller = navController,
            destination = ChangeNickNamePageController,
        )
        composable(
            controller = navController,
            destination = WebViewPageController,
            enterTransition = {
                fullHorizontalSlideInToLeft()
            },
            popExitTransition = {
                fullHorizontalSlideOutToRight()
            }
        )
        composable(
            controller = navController,
            destination = QuitPageController,
        )
    }
}