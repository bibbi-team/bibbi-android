package com.no5ing.bbibbi.presentation.feature.view_controller.main

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import com.no5ing.bbibbi.presentation.feature.view.main.mission_upload.MissionUploadPage
import com.no5ing.bbibbi.presentation.feature.view_controller.NavigationDestination

object MissionUploadPageController : NavigationDestination(
    route = uploadMissionPreviewPageRoute,
) {
    @Composable
    override fun Render(navController: NavHostController, backStackEntry: NavBackStackEntry) {
        val imageCaptureState = backStackEntry.savedStateHandle
            .getLiveData<Uri?>("imageUrl")
            .observeAsState()
        MissionUploadPage(
            imageUrl = imageCaptureState,
            onDispose = {
                navController.popBackStack()
            },
        )
    }

    fun NavHostController.goMissionUploadPage() {
        navigate(MissionUploadPageController)
    }
}
