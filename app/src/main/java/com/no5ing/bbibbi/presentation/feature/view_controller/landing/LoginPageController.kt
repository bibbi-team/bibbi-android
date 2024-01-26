package com.no5ing.bbibbi.presentation.feature.view_controller.landing

import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import com.no5ing.bbibbi.presentation.feature.state.landing.login.LoginSucceedResult
import com.no5ing.bbibbi.presentation.feature.view.landing.login.LoginPage
import com.no5ing.bbibbi.presentation.feature.view_controller.NavigationDestination
import com.no5ing.bbibbi.presentation.feature.view_controller.landing.OnBoardingPageController.goOnBoardingPage
import com.no5ing.bbibbi.presentation.feature.view_controller.register.RegisterNicknamePageController
import com.no5ing.bbibbi.presentation.feature.view_controller.register.RegisterNicknamePageController.goRegisterNicknamePage

object LoginPageController : NavigationDestination(
    route = landingLoginPageRoute,
) {
    @Composable
    override fun Render(navController: NavHostController, backStackEntry: NavBackStackEntry) {
        LoginPage(
            onCompleted = { result ->
                when (result) {
                    LoginSucceedResult.PERMANENT_HAS_FAMILY -> {
                        //navController.navigate(RegisterNicknameDestination)
                        navController.popAll()
                        navController.goOnBoardingPage()
                    }

                    LoginSucceedResult.PERMANENT_NO_FAMILY -> {
                        navController.goOnBoardingPage()
                    }

                    LoginSucceedResult.TEMPORARY -> {
                        navController.goRegisterNicknamePage()
                    }
                }
            },
        )
    }

    fun NavHostController.goLoginPage() {
        navigate(LoginPageController)
    }
}