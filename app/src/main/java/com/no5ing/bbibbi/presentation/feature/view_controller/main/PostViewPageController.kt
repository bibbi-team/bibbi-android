package com.no5ing.bbibbi.presentation.feature.view_controller.main

import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.navArgument
import com.no5ing.bbibbi.presentation.feature.view.main.post_view.PostViewPage
import com.no5ing.bbibbi.presentation.feature.view_controller.NavigationDestination

object PostViewPageController : NavigationDestination(
    route = postViewPageRoute,
    pathVariable = navArgument("postId") {},
) {
    @Composable
    override fun Render(navController: NavHostController, backStackEntry: NavBackStackEntry) {
        PostViewPage(
            postId = backStackEntry.arguments?.getString("postId") ?: "UNKNOWN",
            onDispose = {
                navController.popBackStack()
            },
            onTapProfile = {
                navController.navigate(
                    destination = ProfilePageController,
                    path = it.memberId
                )
            },
            onTapRealEmojiCreate = {
                navController.navigate(
                    destination = CreateRealEmojiPageController,
                    params = listOf(
                        "initialEmoji" to it
                    )
                )
            }
        )
    }
}