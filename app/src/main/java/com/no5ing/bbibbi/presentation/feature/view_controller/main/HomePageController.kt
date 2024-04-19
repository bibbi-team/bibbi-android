package com.no5ing.bbibbi.presentation.feature.view_controller.main

import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import com.no5ing.bbibbi.presentation.feature.view.main.home.HomePage
import com.no5ing.bbibbi.presentation.feature.view_controller.CameraViewPageController.goCameraViewPage
import com.no5ing.bbibbi.presentation.feature.view_controller.NavigationDestination
import com.no5ing.bbibbi.presentation.feature.view_controller.main.CalendarPageController.goCalendarPage
import com.no5ing.bbibbi.presentation.feature.view_controller.main.FamilyListPageController.goFamilyListPage
import com.no5ing.bbibbi.presentation.feature.view_controller.main.PostReUploadPageController.goPostReUploadPage
import com.no5ing.bbibbi.presentation.feature.view_controller.main.PostUploadPageController.goPostUploadPage
import com.no5ing.bbibbi.presentation.feature.view_controller.main.PostViewPageController.goPostViewPage
import com.no5ing.bbibbi.presentation.feature.view_controller.main.ProfilePageController.goProfilePage

object HomePageController : NavigationDestination(
    route = mainHomePageRoute,
) {
    @Composable
    override fun Render(navController: NavHostController, backStackEntry: NavBackStackEntry) {
        HomePage(
            onTapLeft = {
                navController.goFamilyListPage()
            },
            onTapRight = {
                navController.goCalendarPage()
            },
            onTapProfile = {
                navController.goProfilePage(it)
            },
            onTapContent = {
                navController.goPostViewPage(it)
            },
            onTapUpload = {
                navController.goPostUploadPage()
                navController.goCameraViewPage()
            },
            onTapInvite = {
                navController.goFamilyListPage()
            },
            onUnsavedPost = {
                navController.goPostReUploadPage(it.toString())
            }
        )
    }

    fun NavHostController.goHomePage() {
        navigate(route = mainPageRoute)
    }
}
