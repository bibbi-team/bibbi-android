package com.no5ing.bbibbi.presentation.view_controller

import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import com.no5ing.bbibbi.presentation.view.common.CameraView

object CameraViewDestination : NavigationDestination(
    route = cameraViewRoute,
    arguments = emptyList(),
) {
    @Composable
    override fun Render(navController: NavHostController, backStackEntry: NavBackStackEntry) {
        CameraView(
            onDispose = {
                navController.popBackStack()
            },
            onImageCaptured = { image ->
                navController.previousBackStackEntry?.savedStateHandle?.set(
                    "imageUrl",
                    image
                )
                navController.popBackStack()
            }
        )
    }
}