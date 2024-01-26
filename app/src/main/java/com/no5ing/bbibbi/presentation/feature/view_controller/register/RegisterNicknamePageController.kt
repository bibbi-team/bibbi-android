package com.no5ing.bbibbi.presentation.feature.view_controller.register

import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import com.no5ing.bbibbi.presentation.feature.state.register.nickname.rememberRegisterNickNamePageState
import com.no5ing.bbibbi.presentation.feature.view.register.nickname.RegisterNickNamePage
import com.no5ing.bbibbi.presentation.feature.view_controller.NavigationDestination
import com.no5ing.bbibbi.presentation.feature.view_controller.register.RegisterDayOfBirthPageController.goRegisterDayOfBirthPage

object RegisterNicknamePageController : NavigationDestination(
    route = registerNickNameRoute,
) {
    @Composable
    override fun Render(navController: NavHostController, backStackEntry: NavBackStackEntry) {
        val nickNameState = rememberRegisterNickNamePageState()
        RegisterNickNamePage(
            state = nickNameState,
            onNextPage = {
                navController.goRegisterDayOfBirthPage(nickNameState.nicknameTextState.value)
            })
    }

    fun NavHostController.goRegisterNicknamePage() {
        navigate(RegisterNicknamePageController)
    }
}
