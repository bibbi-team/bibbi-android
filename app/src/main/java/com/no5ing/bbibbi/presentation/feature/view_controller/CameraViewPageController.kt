package com.no5ing.bbibbi.presentation.feature.view_controller

import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import com.no5ing.bbibbi.presentation.feature.view.common.CameraView

object CameraViewPageController : NavigationDestination(
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

    fun NavHostController.goCameraViewPage() {
        navigate(CameraViewPageController)
    }
}