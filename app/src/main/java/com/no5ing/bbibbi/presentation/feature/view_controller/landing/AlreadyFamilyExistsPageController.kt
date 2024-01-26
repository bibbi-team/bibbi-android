package com.no5ing.bbibbi.presentation.feature.view_controller.landing

import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import com.no5ing.bbibbi.presentation.feature.view.landing.already_family_exists.AlreadyFamilyExistsView
import com.no5ing.bbibbi.presentation.feature.view_controller.NavigationDestination


object AlreadyFamilyExistsPageController : NavigationDestination(
    route = landingAlreadyFamilyExistsRoute,
) {
    @Composable
    override fun Render(navController: NavHostController, backStackEntry: NavBackStackEntry) {
        AlreadyFamilyExistsView(
            onTapDispose = {
                navController.popBackStack()
            }
        )
    }
}


