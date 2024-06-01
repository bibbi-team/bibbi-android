package com.no5ing.bbibbi.presentation.feature.view_controller.main

import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import com.no5ing.bbibbi.presentation.feature.view.main.mission_upload_camera.UploadMissionCamera
import com.no5ing.bbibbi.presentation.feature.view_controller.NavigationDestination

object UploadMissionPageController : NavigationDestination(
    route = uploadMissionPageRoute,
) {
    @Composable
    override fun Render(navController: NavHostController, backStackEntry: NavBackStackEntry) {
        UploadMissionCamera(
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

    fun NavHostController.goMissionCameraPage() {
        navigate(UploadMissionPageController)
    }
}