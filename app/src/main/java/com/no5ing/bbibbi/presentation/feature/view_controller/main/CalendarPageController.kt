package com.no5ing.bbibbi.presentation.feature.view_controller.main

import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import com.no5ing.bbibbi.presentation.feature.view.main.calendar.MainCalendarPage
import com.no5ing.bbibbi.presentation.feature.view_controller.NavigationDestination
import com.no5ing.bbibbi.presentation.feature.view_controller.main.CalendarDetailPageController.goCalendarDetailPage

object CalendarPageController : NavigationDestination(
    route = mainCalendarPageRoute,
) {
    @Composable
    override fun Render(navController: NavHostController, backStackEntry: NavBackStackEntry) {
        MainCalendarPage(
            onDispose = {
                navController.popBackStack()
            },
            onTapDay = {
                navController.goCalendarDetailPage(it)
            }
        )
    }

    fun NavHostController.goCalendarPage() {
        navigate(CalendarPageController)
    }
}