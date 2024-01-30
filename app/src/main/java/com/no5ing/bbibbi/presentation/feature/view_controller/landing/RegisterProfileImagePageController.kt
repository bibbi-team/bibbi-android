package com.no5ing.bbibbi.presentation.feature.view_controller.landing

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.navArgument
import com.no5ing.bbibbi.presentation.feature.state.register.profile_image.rememberRegisterProfileImagePageState
import com.no5ing.bbibbi.presentation.feature.view.landing.profile_image.RegisterProfileImagePage
import com.no5ing.bbibbi.presentation.feature.view_controller.CameraViewPageController
import com.no5ing.bbibbi.presentation.feature.view_controller.NavigationDestination
import com.no5ing.bbibbi.presentation.feature.view_controller.landing.LoginPageController.goLoginPage
import com.no5ing.bbibbi.presentation.feature.view_controller.landing.OnBoardingPageController.goOnBoardingPage


object RegisterProfileImagePageController : NavigationDestination(
    route = registerProfileImageRoute,
    arguments = listOf(navArgument("nickName") { }, navArgument("dayOfBirth") { }),
) {
    @Composable
    override fun Render(navController: NavHostController, backStackEntry: NavBackStackEntry) {
        val imgState = remember { mutableStateOf<Uri?>(null) }
        val imgUrl = backStackEntry.savedStateHandle.remove<Uri?>("imageUrl")
        if (imgUrl != null) {
            imgState.value = imgUrl
        }

        RegisterProfileImagePage(
            state = rememberRegisterProfileImagePageState(
                profileImageUri = imgState,
            ),
            nickName = backStackEntry.arguments?.getString("nickName")
                ?: throw RuntimeException(),
            dayOfBirth = backStackEntry.arguments?.getString("dayOfBirth")
                ?: throw RuntimeException(),
            onNextPage = {
                navController.popBackStack(
                    route = route,
                    inclusive = true
                )
                navController.goLoginPage()
                navController.goOnBoardingPage()
            },
            onTapCamera = {
                navController.navigate(
                    destination = CameraViewPageController,
                )
            },
            onDispose = {
                navController.popBackStack()
            },
        )
    }

    fun NavHostController.goRegisterProfileImagePage(nickName: String, dayOfBirth: String) {
        navigate(
            RegisterProfileImagePageController,
            params = listOf("nickName" to nickName, "dayOfBirth" to dayOfBirth)
        )
    }
}