package com.no5ing.bbibbi.presentation.ui.navigation.destination

import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.navArgument
import com.no5ing.bbibbi.presentation.state.register.nickname.rememberRegisterNickNamePageState
import com.no5ing.bbibbi.presentation.state.register.profile_image.rememberRegisterProfileImagePageState
import com.no5ing.bbibbi.presentation.ui.feature.register.day_of_birth.RegisterDayOfBirthPage
import com.no5ing.bbibbi.presentation.ui.feature.register.nickname.RegisterNickNamePage
import com.no5ing.bbibbi.presentation.ui.feature.register.profile_image.RegisterProfileImagePage
import timber.log.Timber

object RegisterNicknameDestination : NavigationDestination(
    route = registerNickNameRoute,
    content = { navController, _ ->
        val nickNameState = rememberRegisterNickNamePageState()
        RegisterNickNamePage(
            state = nickNameState,
            onNextPage = {
                navController.navigate(
                    destination = RegisterDayOfBirthDestination,
                    params = listOf("nickName" to nickNameState.nicknameTextState.value)
                )
            })
    }
)

object RegisterDayOfBirthDestination : NavigationDestination(
    route = registerDayOfBirthRoute,
    arguments = listOf(navArgument("nickName") { }),
    content = { navController, backStackEntry ->
        val nickName = backStackEntry.arguments?.getString("nickName")
            ?: "UNKNOWN"
        RegisterDayOfBirthPage(
            nickName = nickName,
            onNextPage = {
                navController.navigate(
                    destination = RegisterProfileImageDestination,
                    params = listOf("nickName" to nickName, "dayOfBirth" to it)
                )
            }
        )
    }
)

object RegisterProfileImageDestination : NavigationDestination(
    route = registerProfileImageRoute,
    arguments = listOf(navArgument("nickName") { }, navArgument("dayOfBirth") { }),
    content = { navController, backStackEntry ->
        val imgState = remember { mutableStateOf<Uri?>(null) }
        val imgUrl = backStackEntry.savedStateHandle.remove<Uri?>("imageUrl")
        if (imgUrl != null) {
            Timber.d("Img state updated")
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
                    route = LandingLoginDestination.route,
                    inclusive = true
                )
                navController.navigate(
                    destination = LandingLoginDestination,
                )
                navController.navigate(
                    destination = LandingOnBoardingDestination,
                )
            },
            onTapCamera = {
                navController.navigate(
                    destination = CameraViewDestination,
                )
            }
        )
    }
)