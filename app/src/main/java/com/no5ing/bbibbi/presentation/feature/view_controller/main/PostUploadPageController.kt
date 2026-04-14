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
import com.no5ing.bbibbi.presentation.feature.view_controller.main.LocationPickerPageController.LOCATION_ADDRESS_KEY
import com.no5ing.bbibbi.presentation.feature.view_controller.main.LocationPickerPageController.LOCATION_LAT_KEY
import com.no5ing.bbibbi.presentation.feature.view_controller.main.LocationPickerPageController.LOCATION_LNG_KEY
import com.no5ing.bbibbi.presentation.feature.view_controller.main.LocationPickerPageController.goLocationPickerPage

object PostUploadPageController : NavigationDestination(
    route = postUploadRoute,
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
        PostUploadPage(
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

    fun NavHostController.goPostUploadPage() {
        navigate(PostUploadPageController)
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
        val locationLat = backStackEntry.savedStateHandle
            .getLiveData<Double>(LOCATION_LAT_KEY)
            .observeAsState()
        val locationLng = backStackEntry.savedStateHandle
            .getLiveData<Double>(LOCATION_LNG_KEY)
            .observeAsState()
        val locationAddress = backStackEntry.savedStateHandle
            .getLiveData<String>(LOCATION_ADDRESS_KEY)
            .observeAsState()
        PostUploadPage(
            imageUrl = uriState,
            isUnsaveMode = true,
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

    fun NavHostController.goPostReUploadPage(imageUrl: String) {
        navigate(PostReUploadPageController, params = listOf("imageUrl" to imageUrl))
    }
}