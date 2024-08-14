package com.no5ing.bbibbi.presentation.feature.view_controller.main

import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import com.no5ing.bbibbi.presentation.feature.view.main.change_family_name.ChangeFamilyNamePage
import com.no5ing.bbibbi.presentation.feature.view_controller.NavigationDestination

object ChangeFamilyNamePageController : NavigationDestination(
    route = settingFamilyNameRoute,
) {
    @Composable
    override fun Render(navController: NavHostController, backStackEntry: NavBackStackEntry) {
        ChangeFamilyNamePage(
            onDispose = {
                navController.popBackStack()
            }
        )
    }

    fun NavHostController.goChangeFamilyNamePage() {
        navigate(ChangeFamilyNamePageController)
    }
}