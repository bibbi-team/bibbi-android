package com.no5ing.bbibbi.presentation.feature.view_controller.main

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.navArgument
import com.no5ing.bbibbi.presentation.feature.view.main.post_upload.PostUploadPage
import com.no5ing.bbibbi.presentation.feature.view_controller.NavigationDestination

object PostUploadPageController : NavigationDestination(
    route = postUploadRoute,
) {
    @Composable
    override fun Render(navController: NavHostController, backStackEntry: NavBackStackEntry) {
        val imageCaptureState = backStackEntry.savedStateHandle
            .getLiveData<Uri?>("imageUrl")
            .observeAsState()
        PostUploadPage(
            imageUrl = imageCaptureState,
            onDispose = {
                navController.popBackStack()
            },
        )
    }
}

object PostReUploadPageController : NavigationDestination(
    route = postReUploadRoute,
    arguments = listOf(navArgument("imageUrl") {}),
) {
    @Composable
    override fun Render(navController: NavHostController, backStackEntry: NavBackStackEntry) {
        val imageCaptureState = backStackEntry.arguments?.getString("imageUrl")
        val uriState = remember { mutableStateOf(Uri.parse(imageCaptureState)) }
        PostUploadPage(
            imageUrl = uriState,
            isUnsaveMode = true,
            onDispose = {
                navController.popBackStack()
            },
        )
    }
}