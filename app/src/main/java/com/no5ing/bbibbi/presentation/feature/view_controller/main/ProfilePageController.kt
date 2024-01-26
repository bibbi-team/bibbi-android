package com.no5ing.bbibbi.presentation.feature.view_controller.main

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.navArgument
import com.no5ing.bbibbi.presentation.feature.view.main.profile.ProfilePage
import com.no5ing.bbibbi.presentation.feature.view_controller.CameraViewDestination
import com.no5ing.bbibbi.presentation.feature.view_controller.NavigationDestination
import timber.log.Timber

object ProfilePageController : NavigationDestination(
    route = mainProfilePageRoute,
    pathVariable = navArgument("memberId") {}
) {
    @Composable
    override fun Render(navController: NavHostController, backStackEntry: NavBackStackEntry) {
        val imgState = remember { mutableStateOf<Uri?>(null) }
        val imgUrl = backStackEntry.savedStateHandle.remove<Uri?>("imageUrl")
        if (imgUrl != null) {
            Timber.d("[ProfilePage] Img state updated")
            imgState.value = imgUrl
        }
        ProfilePage(
            memberId = backStackEntry.arguments?.getString("memberId")
                ?: "UNKNOWN",
            onDispose = {
                navController.popBackStack()
            },
            onTapSetting = {
                navController.navigate(SettingHomePageController)
            },
            onTapPost = {
                navController.navigate(
                    destination = PostViewPageController,
                    path = it.postId
                )
            },
            onTapChangeNickname = {
                navController.navigate(
                    ChangeNickNamePageController
                )
            },
            onTapCamera = {
                navController.navigate(
                    destination = CameraViewDestination,
                )
            },
            changeableUriState = imgState,
        )
    }
}