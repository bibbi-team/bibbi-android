package com.no5ing.bbibbi.presentation.feature.view_controller.main

import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import com.no5ing.bbibbi.presentation.feature.view.main.location_picker.LocationPickerPage
import com.no5ing.bbibbi.presentation.feature.view_controller.NavigationDestination

object LocationPickerPageController : NavigationDestination(
    route = locationPickerRoute,
) {
    const val LOCATION_LAT_KEY = "locationLat"
    const val LOCATION_LNG_KEY = "locationLng"
    const val LOCATION_ADDRESS_KEY = "locationAddress"

    @Composable
    override fun Render(navController: NavHostController, backStackEntry: NavBackStackEntry) {
        LocationPickerPage(
            onDispose = {
                navController.popBackStack()
            },
            onConfirmLocation = { lat, lng, address ->
                navController.previousBackStackEntry?.savedStateHandle?.apply {
                    set(LOCATION_LAT_KEY, lat)
                    set(LOCATION_LNG_KEY, lng)
                    set(LOCATION_ADDRESS_KEY, address)
                }
                navController.popBackStack()
            },
        )
    }

    fun NavHostController.goLocationPickerPage() {
        navigate(LocationPickerPageController)
    }
}
