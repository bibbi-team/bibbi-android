package com.no5ing.bbibbi.presentation.feature.view_controller.main

import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.navArgument
import com.no5ing.bbibbi.presentation.feature.view.main.calendar.detail.CalendarDetailPage
import com.no5ing.bbibbi.presentation.feature.view_controller.NavigationDestination
import java.time.LocalDate

object CalendarDetailPageController : NavigationDestination(
    route = mainCalendarDetailPageRoute,
    arguments = listOf(navArgument("date") { }),
) {
    @Composable
    override fun Render(navController: NavHostController, backStackEntry: NavBackStackEntry) {
        val dateStr = backStackEntry.arguments?.getString("date")
        val date = dateStr?.let(LocalDate::parse) ?: LocalDate.now()
        CalendarDetailPage(
            initialDay = date,
            onDispose = {
                navController.popBackStack()
            },
            onTapProfile = {
                navController.navigate(
                    destination = ProfilePageController,
                    path = it.memberId
                )
            },
            onTapRealEmojiCreate = {
                navController.navigate(
                    destination = CreateRealEmojiPageController,
                    params = listOf(
                        "initialEmoji" to it
                    )
                )
            }
        )
    }
}