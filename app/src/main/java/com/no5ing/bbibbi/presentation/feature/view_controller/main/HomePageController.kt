package com.no5ing.bbibbi.presentation.feature.view_controller.main

import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import com.no5ing.bbibbi.presentation.feature.view.main.home.HomePage
import com.no5ing.bbibbi.presentation.feature.view_controller.CameraViewDestination
import com.no5ing.bbibbi.presentation.feature.view_controller.NavigationDestination

object HomePageController : NavigationDestination(
    route = mainHomePageRoute,
) {
    @Composable
    override fun Render(navController: NavHostController, backStackEntry: NavBackStackEntry) {
        HomePage(
            onTapLeft = {
                navController.navigate(FamilyListPageController)
            },
            onTapRight = {
                navController.navigate(CalendarPageController)
            },
            onTapProfile = {
                navController.navigate(
                    destination = ProfilePageController,
                    path = it.memberId
                )
            },
            onTapContent = {
                navController.navigate(
                    destination = PostViewPageController,
                    path = it.postId
                )
            },
            onTapUpload = {
                navController.navigate(PostUploadPageController)
                navController.navigate(
                    destination = CameraViewDestination,
                )
            },
            onTapInvite = {
                navController.navigate(FamilyListPageController)
            },
            onUnsavedPost = {
                navController.navigate(
                    PostReUploadPageController,
                    params = listOf("imageUrl" to it.toString())
                )
            }
        )
    }
}
