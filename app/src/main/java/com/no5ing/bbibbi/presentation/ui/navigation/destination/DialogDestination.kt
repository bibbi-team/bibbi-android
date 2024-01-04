package com.no5ing.bbibbi.presentation.ui.navigation.destination

import androidx.compose.runtime.Composable
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import com.no5ing.bbibbi.presentation.ui.feature.dialog.CameraDialog

abstract class DialogDestination(
    route: String,
    arguments: List<NamedNavArgument> = emptyList(),
    content: @Composable (NavHostController, NavBackStackEntry) -> Unit,
) : NavigationDestination(route, arguments, null, content)

object CameraDialogDestination : DialogDestination(
    route = cameraDialogRoute,
    arguments = emptyList(), content = { navController, _ ->
        CameraDialog(
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