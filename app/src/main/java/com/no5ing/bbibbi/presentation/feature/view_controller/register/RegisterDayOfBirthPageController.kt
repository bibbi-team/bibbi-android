package com.no5ing.bbibbi.presentation.feature.view_controller.register

import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.navArgument
import com.no5ing.bbibbi.presentation.feature.view.register.day_of_birth.RegisterDayOfBirthPage
import com.no5ing.bbibbi.presentation.feature.view_controller.NavigationDestination
import com.no5ing.bbibbi.presentation.feature.view_controller.register.RegisterProfileImagePageController.goRegisterProfileImagePage

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
                navController.goRegisterProfileImagePage(nickName, it)
            },
            onDispose = {
                navController.popBackStack()
            },
        )
    }

    fun NavHostController.goRegisterDayOfBirthPage(nickName: String) {
        navigate(RegisterDayOfBirthPageController, params = listOf("nickName" to nickName))
    }
}