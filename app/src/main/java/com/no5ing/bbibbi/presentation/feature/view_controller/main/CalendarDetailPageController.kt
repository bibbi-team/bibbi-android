package com.no5ing.bbibbi.presentation.feature.view_controller.main

import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.navArgument
import com.no5ing.bbibbi.presentation.feature.view.main.calendar.detail.CalendarDetailPage
import com.no5ing.bbibbi.presentation.feature.view_controller.NavigationDestination
import com.no5ing.bbibbi.presentation.feature.view_controller.main.CreateRealEmojiPageController.goCreateRealEmojiPage
import com.no5ing.bbibbi.presentation.feature.view_controller.main.ProfilePageController.goProfilePage
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
                navController.goProfilePage(it.memberId)
            },
            onTapRealEmojiCreate = {
                navController.goCreateRealEmojiPage(it)
            }
        )
    }

    fun NavHostController.goCalendarDetailPage(date: LocalDate) {
        navigate(CalendarDetailPageController, params = listOf("date" to date.toString()))
    }
}