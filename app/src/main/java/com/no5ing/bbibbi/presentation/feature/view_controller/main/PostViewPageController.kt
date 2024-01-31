package com.no5ing.bbibbi.presentation.feature.view_controller.main

import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.navArgument
import com.no5ing.bbibbi.presentation.feature.view.main.post_view.PostViewPage
import com.no5ing.bbibbi.presentation.feature.view_controller.NavigationDestination
import com.no5ing.bbibbi.presentation.feature.view_controller.main.CreateRealEmojiPageController.goCreateRealEmojiPage
import com.no5ing.bbibbi.presentation.feature.view_controller.main.ProfilePageController.goProfilePage

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
                navController.goProfilePage(it.memberId)
            },
            onTapRealEmojiCreate = {
                navController.goCreateRealEmojiPage(it)
            }
        )
    }

    fun NavHostController.goPostViewPage(postId: String) {
        navigate(PostViewPageController, path = postId)
    }
}