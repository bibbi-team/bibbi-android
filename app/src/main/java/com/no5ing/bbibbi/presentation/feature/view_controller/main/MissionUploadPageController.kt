package com.no5ing.bbibbi.presentation.feature.view_controller.main

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import com.no5ing.bbibbi.presentation.feature.view.main.mission_upload.MissionUploadPage
import com.no5ing.bbibbi.presentation.feature.view_controller.NavigationDestination
import com.no5ing.bbibbi.presentation.feature.view_controller.main.LocationPickerPageController.LOCATION_ADDRESS_KEY
import com.no5ing.bbibbi.presentation.feature.view_controller.main.LocationPickerPageController.LOCATION_LAT_KEY
import com.no5ing.bbibbi.presentation.feature.view_controller.main.LocationPickerPageController.LOCATION_LNG_KEY
import com.no5ing.bbibbi.presentation.feature.view_controller.main.LocationPickerPageController.goLocationPickerPage

object MissionUploadPageController : NavigationDestination(
    route = uploadMissionPreviewPageRoute,
) {
    @Composable
    override fun Render(navController: NavHostController, backStackEntry: NavBackStackEntry) {
        val imageCaptureState = backStackEntry.savedStateHandle
            .getLiveData<Uri?>("imageUrl")
            .observeAsState()
        val locationLat = backStackEntry.savedStateHandle
            .getLiveData<Double>(LOCATION_LAT_KEY)
            .observeAsState()
        val locationLng = backStackEntry.savedStateHandle
            .getLiveData<Double>(LOCATION_LNG_KEY)
            .observeAsState()
        val locationAddress = backStackEntry.savedStateHandle
            .getLiveData<String>(LOCATION_ADDRESS_KEY)
            .observeAsState()
        MissionUploadPage(
            imageUrl = imageCaptureState,
            locationLatitude = locationLat,
            locationLongitude = locationLng,
            locationAddress = locationAddress,
            onDispose = {
                navController.popBackStack()
            },
            onNavigateToLocationPicker = {
                navController.goLocationPickerPage()
            },
        )
    }

    fun NavHostController.goMissionUploadPage() {
        navigate(MissionUploadPageController)
    }
}
