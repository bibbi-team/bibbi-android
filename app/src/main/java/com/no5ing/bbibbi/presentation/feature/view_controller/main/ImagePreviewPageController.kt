package com.no5ing.bbibbi.presentation.feature.view_controller.main

import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.navArgument
import com.no5ing.bbibbi.presentation.feature.view.main.image_preview.ImagePreviewPage
import com.no5ing.bbibbi.presentation.feature.view_controller.NavigationDestination

object ImagePreviewPageController : NavigationDestination(
    route = mainImagePreviewRoute,
    arguments = listOf(navArgument("imageUrl") {})
) {
    @Composable
    override fun Render(navController: NavHostController, backStackEntry: NavBackStackEntry) {
        val imageUrl = backStackEntry.arguments?.getString("imageUrl")
            ?: throw RuntimeException("imageUrl is required")
        ImagePreviewPage(
            imageUrl = imageUrl,
            onDispose = {
                navController.popBackStack()
            },
        )
    }

    fun NavHostController.goImagePreviewPage(imageUrl: String) {
        navigate(ImagePreviewPageController, params = listOf("imageUrl" to imageUrl))
    }
}