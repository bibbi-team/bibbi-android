package com.no5ing.bbibbi.presentation.feature.view_controller.main

import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.navArgument
import com.no5ing.bbibbi.presentation.feature.view.main.create_real_emoji.CreateRealEmojiPage
import com.no5ing.bbibbi.presentation.feature.view_controller.NavigationDestination

object CreateRealEmojiPageController : NavigationDestination(
    arguments = listOf(navArgument("initialEmoji") {}),
    route = postCreateRealEmojiRoute,
) {
    @Composable
    override fun Render(navController: NavHostController, backStackEntry: NavBackStackEntry) {
        CreateRealEmojiPage(
            initialEmoji = backStackEntry.arguments?.getString("initialEmoji")
                ?: throw RuntimeException(),
            onDispose = {
                navController.popBackStack()
            },
        )
    }

    fun NavHostController.goCreateRealEmojiPage(initialEmoji: String) {
        navigate(CreateRealEmojiPageController, params = listOf("initialEmoji" to initialEmoji))
    }
}