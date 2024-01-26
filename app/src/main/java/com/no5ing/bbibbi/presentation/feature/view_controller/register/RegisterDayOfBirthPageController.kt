package com.no5ing.bbibbi.presentation.feature.view_controller.register

import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.navArgument
import com.no5ing.bbibbi.presentation.feature.view.register.day_of_birth.RegisterDayOfBirthPage
import com.no5ing.bbibbi.presentation.feature.view_controller.NavigationDestination

object RegisterDayOfBirthPageController : NavigationDestination(
    route = registerDayOfBirthRoute,
    arguments = listOf(navArgument("nickName") { }),
) {
    @Composable
    override fun Render(navController: NavHostController, backStackEntry: NavBackStackEntry) {
        val nickName = backStackEntry.arguments?.getString("nickName")
            ?: "UNKNOWN"
        RegisterDayOfBirthPage(
            nickName = nickName,
            onNextPage = {
                navController.navigate(
                    destination = RegisterProfileImagePageController,
                    params = listOf("nickName" to nickName, "dayOfBirth" to it)
                )
            }
        )
    }
}