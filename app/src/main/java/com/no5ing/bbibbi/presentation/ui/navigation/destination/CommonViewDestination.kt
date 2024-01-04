package com.no5ing.bbibbi.presentation.ui.navigation.destination

import com.no5ing.bbibbi.presentation.ui.feature.common.CameraView

object CameraViewDestination : NavigationDestination(
    route = cameraViewRoute,
    arguments = emptyList(),
    content = { navController, _ ->
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
)